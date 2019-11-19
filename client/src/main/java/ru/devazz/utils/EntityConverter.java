package ru.devazz.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.*;
import ru.devazz.server.EJBProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Конвертер сущностей
 */
public class EntityConverter {

	/** Экземпляр конвертера */
	private static EntityConverter instance;

	/**
	 * Конструктор
	 */
	private EntityConverter() {
	}

	public static EntityConverter getInstatnce() {
		if (null == instance) {
			instance = new EntityConverter();
		}
		return instance;
	}

	/**
	 * Конвертирует датамодель
	 *
	 * @param aEntity датамодель
	 * @return Клиентсткая обертка над сущностью
	 */
	public SubordinationElement convertSubElEntityToClientWrap(SubordinationElementModel aEntity) {
		SubordinationElement element = new SubordinationElement();
		element.setSuid(aEntity.getSuid());
		element.setName(aEntity.getName());
		element.setRoleSuid(aEntity.getRoleSuid());
		element.setRootElement(aEntity.getRootElement());
		ObservableList<SubordinationElement> subordinates = FXCollections.observableArrayList();
//		for (SubordinationElementModel entity : aEntity.getSubordinates()) {
//			subordinates.add(convertSubElEntityToClientWrap(entity));
//		}
//		element.setSubElements(subordinates);
		return element;
	}

	/**
	 * Конвертирует сущность задачи в клиентскую обертку
	 *
	 * @param aEntity сущность задачи
	 * @return задача
	 */
	public Task convertTaskModelToClientWrapTask(TaskModel aEntity) {
		Task task = null;
		if (null != aEntity) {
			String name = "";
			String note = "";
			String description = "";
			String documentName = "";
			ISubordinationElementService subElService = EJBProxyFactory.getInstance()
					.getService(ISubordinationElementService.class);

			SubordinationElementModel entity = subElService.get(aEntity.getExecutorSuid());
			SubordinationElementModel entityAuthor = (null != aEntity.getAuthorSuid())
					? subElService.get(aEntity.getAuthorSuid())
					: null;
			name = Utils.getInstance().fromBase64(aEntity.getName());
			note = Utils.getInstance().fromBase64(aEntity.getNote());
			description = (null != aEntity.getDescription())
					? Utils.getInstance().fromBase64(aEntity.getDescription())
					: "";
			documentName = (null != aEntity.getDocumentName())
					? Utils.getInstance().fromBase64(aEntity.getDocumentName())
					: "";

			// @formatter:off
			task = new Task().getBuilder()
					.addSuid(aEntity.getSuid())
					.addName(name).addNote(note)
					.addDescription(description)
					.addStatus(aEntity.getStatus())
					.addTaskPriority(aEntity.getPriority())
					.addProgress(computeProgress(aEntity.getStartDate(), aEntity.getEndDate()))
					.addStartDateTime(aEntity.getStartDate())
					.addEndDateTime(aEntity.getEndDate())
					.addExecutor(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity))
					.addAuthor((null != entityAuthor) ? EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entityAuthor) : null)
					.addDocumentName(documentName)
					.addTaskType(aEntity.getTaskType())
					.addCycleTime(aEntity.getCycleTime())
					.addCycleType(aEntity.getCycleType())
					.addDocument(aEntity.getDocument()).toTask();
			// @formatter:on
		}
		return task;
	}

	/**
	 * Вычисляет процент выполнения задачи
	 *
	 * @param aStartDate начальная дата
	 * @param aEndDate конечная дата
	 * @return процент выполнения задачи
	 */
	public Double computeProgress(Date aStartDate, Date aEndDate) {
		Date currDate = new Date();
		Double result = null;
		if (currDate.getTime() >= aStartDate.getTime()) {
			Long totalHourDifference = (aEndDate.getTime() - aStartDate.getTime()) / 360000;
			if (totalHourDifference > 0) {
				Long currentHourDifference = (aEndDate.getTime() - new Date().getTime()) / 360000;
				result = new Double(100 - ((currentHourDifference * 100) / totalHourDifference))
						/ 100;
			}
		} else {
			result = new Double(0);
		}
		return result;
	}

	/**
	 * Конвертирует клиентскую обертку над задачей в сущность задачи
	 *
	 * @param aTask Клиентсткая обертка над сущностью задачи
	 * @return датамодель задачи
	 * @throws Exception В случае ошибки
	 */
	public TaskModel convertClientWrapTaskToTaskEntity(Task aTask) throws Exception {
		String base64Name = Utils.getInstance().toBase64(aTask.getName().trim());
		String base64Note = Utils.getInstance().toBase64(aTask.getNote());
		String base64Description = Utils.getInstance().toBase64(aTask.getDescription());
		SubordinationElement author = aTask.getAuthor();

		TaskModel entity = TaskModel.builder()
				.suid(aTask.getSuid())
				.authorSuid((null != author) ? author.getSuid() : null)
				.name(base64Name)
				.note(base64Note)
				.description(base64Description)
				.priority(aTask.getPriority())
				.status(aTask.getStatus())
				.taskType(aTask.getType())
				.startDate(aTask.getStartedDate())
				.endDate(aTask.getEndDate())
				.executorSuid(aTask.getExecutor().getSuid())
				.cycleType(aTask.getCycleType())
				.cycleTime(aTask.getCycleTime())
				.build();

		File file = aTask.getDocument();
		if (((null != file) && (file.exists()))) {
			try (FileInputStream inputStream = new FileInputStream(file)) {
				byte[] fileBytes = new byte[(int) file.length()];
				inputStream.read(fileBytes);
				String base64DocumentName = Utils.getInstance().toBase64(file.getName());
				entity.setDocumentName(base64DocumentName);
				entity.setDocument(fileBytes);
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}
		return entity;
	}

	/**
	 * Конвертирует сущность типовой задачи в клиентскую обертку над ней
	 *
	 * @param aDefaultTaskModel сущность типовой задачи
	 * @return клиентская обертка над типовой задачей
	 */
	public DefaultTask convertDefaultTaskModelToClientWrapDefaultTask(
			DefaultTaskModel aDefaultTaskModel) {
		DefaultTask entity = new DefaultTask();
		entity.setDefaultTaskID(aDefaultTaskModel.getSuid());
		entity.setName(aDefaultTaskModel.getName());
		entity.setNote(aDefaultTaskModel.getNote());
		entity.setStartTime(aDefaultTaskModel.getStartTime());
		entity.setEndTime(aDefaultTaskModel.getEndTime());
		entity.setSubordinationSUID(aDefaultTaskModel.getSubordinationSUID());
		return entity;

	}

	/**
	 * Ковертирует клиентскую обертку над типовой задачей в клиентскую обертку над
	 * ней
	 *
	 * @param defaultTask клиентская обертка над типовой задачей
	 * @return сущность типовой задачи
	 */
	public DefaultTaskModel convertClientWrapDefaultTaskToDefaultTaskModel(
			DefaultTask defaultTask) {
		DefaultTaskModel entity = new DefaultTaskModel();
		entity.setSuid(defaultTask.getDefaultTaskID());
		entity.setName(defaultTask.getName());
		entity.setNote(defaultTask.getNote());
		entity.setStartTime(defaultTask.getStartTime());
		entity.setEndTime(defaultTask.getEndTime());
		entity.setSubordinationSUID(defaultTask.getSubordinationSUID());
		return entity;

	}

	/**
	 * Создает клиентскую обертку над событием
	 *
	 * @param aEntity сущность задачи
	 * @return задача
	 */
	public Event convertEventModelToClientWrapEvent(EventModel aEntity) {
		String name = Utils.getInstance().fromBase64(aEntity.getName());
		ISubordinationElementService service = EJBProxyFactory.getInstance()
				.getService(ISubordinationElementService.class);
		SubordinationElement author = convertSubElEntityToClientWrap(
				service.get(aEntity.getAuthorSuid()));
		SubordinationElement executor = convertSubElEntityToClientWrap(
				service.get(aEntity.getExecutorSuid()));

		return new Event().getBuilder()
				.addSuid(aEntity.getSuid())
				.addTaskSuid(aEntity.getTaskSuid())
				.addName(name)
				.addDate(aEntity.getDate())
				.addAuthor(author)
				.addExecutor(executor)
				.addEventType(aEntity.getEventType())
				.toEvent();
	}

	/**
	 * Создает клиентскую обертку над пользователем
	 *
	 * @param aEntity сущность пользователя
	 * @return клиентская обертка пользователем
	 */
	public User convertUserEntitytoClientWrapUser(UserModel aEntity) {
		User user = null;
		if (null != aEntity) {
		// @formatter:off
		user = new User().getBuilder()
				.addSuid(aEntity.getSuid())
				.addRoleSuid(aEntity.getIdRole())
				.addName(aEntity.getName())
				.addPosition(aEntity.getPosition())
				.addSubElementSuid(aEntity.getPositionSuid())
				.addOnline(aEntity.getOnline())
				.addImage(Utils.getInstance().createFileImage(aEntity.getImage(), aEntity.getPositionSuid()))
				.toUser();
		// @formatter:on
		} else {
			new User();
		}
		return user;
	}

}
