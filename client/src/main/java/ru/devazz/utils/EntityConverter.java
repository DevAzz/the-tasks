package ru.devazz.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.*;
import ru.siencesquad.hqtasks.ui.entities.*;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;

import javax.naming.NamingException;
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
	public SubordinationElement convertSubElEntityToClientWrap(SubordinationElementEntity aEntity) {
		SubordinationElement element = new SubordinationElement();
		element.setSuid(aEntity.getSuid());
		element.setName(aEntity.getName());
		element.setRoleSuid(aEntity.getRoleSuid());
		element.setRootElement(aEntity.getRootElement());
		ObservableList<SubordinationElement> subordinates = FXCollections.observableArrayList();
		for (SubordinationElementEntity entity : aEntity.getSubordinates()) {
			subordinates.add(convertSubElEntityToClientWrap(entity));
		}
		element.setSubElements(subordinates);
		return element;
	}

	/**
	 * Конвертирует сущность задачи в клиентскую обертку
	 *
	 * @param aEntity сущность задачи
	 * @return задача
	 */
	public Task convertTaskEntityToClientWrapTask(TaskEntity aEntity) {
		Task task = null;
		if (null != aEntity) {
			String name = "";
			String note = "";
			String description = "";
			String documentName = "";
			try {
				SubordinatioElementServiceRemote subElService = EJBProxyFactory.getInstance()
						.getService(SubordinatioElementServiceRemote.class);

				SubordinationElementEntity entity = subElService.get(aEntity.getExecutorSuid());
				SubordinationElementEntity entityAuthor = (null != aEntity.getAuthorSuid())
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
					.addSuid(aEntity.getTaskSuid())
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
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public TaskEntity convertClientWrapTaskToTaskEntity(Task aTask) throws Exception {
		String base64Name = Utils.getInstance().toBase64(aTask.getName().trim());
		String base64Note = Utils.getInstance().toBase64(aTask.getNote());
		String base64Description = Utils.getInstance().toBase64(aTask.getDescription());
		SubordinationElement author = aTask.getAuthor();

		TaskEntity entity = new TaskEntity();
		entity.setTaskSuid(aTask.getSuid());
		entity.setAuthorSuid((null != author) ? author.getSuid() : null);
		entity.setName(base64Name);
		entity.setNote(base64Note);
		entity.setDescription(base64Description);
		entity.setPriority(aTask.getPriority());
		entity.setStatus(aTask.getStatus());
		entity.setStartDate(aTask.getStartedDate());
		entity.setEndDate(aTask.getEndDate());
		entity.setExecutorSuid(aTask.getExecutor().getSuid());
		entity.setTaskType(aTask.getType());
		entity.setCycleTime(aTask.getCycleTime());
		entity.setCycleType(aTask.getCycleType());

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
	 * @param defaultTaskEntity сущность типовой задачи
	 * @return клиентская обертка над типовой задачей
	 */
	public DefaultTask convertDefaultTaskEntityToClientWrapDefaultTask(
			DefaultTaskEntity defaultTaskEntity) {
		DefaultTask entity = new DefaultTask();
		entity.setDefaultTaskID(defaultTaskEntity.getDefaultTaskID());
		entity.setName(defaultTaskEntity.getName());
		entity.setNote(defaultTaskEntity.getNote());
		entity.setStartTime(defaultTaskEntity.getStartTime());
		entity.setEndTime(defaultTaskEntity.getEndTime());
		entity.setSubordinationSUID(defaultTaskEntity.getSubordinationSUID());
		return entity;

	}

	/**
	 * Ковертирует клиентскую обертку над типовой задачей в клиентскую обертку над
	 * ней
	 *
	 * @param defaultTask клиентская обертка над типовой задачей
	 * @return сущность типовой задачи
	 */
	public DefaultTaskEntity convertClientWrapDefaultTaskToDefaultTaskEntity(
			DefaultTask defaultTask) {
		DefaultTaskEntity entity = new DefaultTaskEntity();
		entity.setDefaultTaskID(defaultTask.getDefaultTaskID());
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
	 * @throws NamingException в случае ошибки
	 */
	public Event convertEventEntityToClientWrapEvent(EventEntity aEntity) throws NamingException {
		String name = Utils.getInstance().fromBase64(aEntity.getName());
		SubordinatioElementServiceRemote service = EJBProxyFactory.getInstance()
				.getService(SubordinatioElementServiceRemote.class);
		SubordinationElement author = convertSubElEntityToClientWrap(
				service.get(aEntity.getAuthorSuid()));
		SubordinationElement executor = convertSubElEntityToClientWrap(
				service.get(aEntity.getExecutorSuid()));

		// @formatter:off
		Event event = new Event().getBuilder()
				.addSuid(aEntity.getIdEvents())
				.addTaskSuid(aEntity.getTaskSuid())
				.addName(name)
				.addDate(aEntity.getDate())
				.addAuthor(author)
				.addExecutor(executor)
				.addEventType(aEntity.getEventType())
				.toEvent();
		// @formatter:on
		return event;
	}

	/**
	 * Создает клиентскую обертку над пользователем
	 *
	 * @param aEntity сущность пользователя
	 * @return клиентская обертка пользователем
	 */
	public User convertUserEntitytoClientWrapUser(UserEntity aEntity) {
		User user = null;
		if (null != aEntity) {
		// @formatter:off
		user = new User().getBuilder()
				.addSuid(aEntity.getSuid())
				.addRoleSuid(aEntity.getIdrole())
				.addName(aEntity.getName())
				.addMilitaryRank(aEntity.getMilitaryRank())
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
