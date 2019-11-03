/**
 *
 */
package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.*;
import ru.devazz.event.ObjectEvent;
import ru.devazz.event.TaskEvent;
import ru.devazz.repository.DefaultTaskRepository;
import ru.devazz.repository.TasksRepository;
import ru.devazz.service.*;
import ru.devazz.utils.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса взаимодействия с задачами
 */
@Service
@AllArgsConstructor
public class TaskService extends AbstractEntityService<TaskEntity>
		implements ITaskService {

	/** Сервис событий */
	private IEventService eventService;

	/** Сервис работы с должностями */
	private ISubordinationElementService subElService;

	/** Сервис работы с историческими записями */
	private ITaskHistoryService historyService;

	/** Репозиторий типовых задач */
	private DefaultTaskRepository defaultTaskRepository = new DefaultTaskRepository();

	private JmsTemplate broker;

	@Override
	public TaskEntity add(TaskEntity aEntity, Boolean aNeedPublishEvent) {
		Long suid = (long) (Math.random() * 10000000L) + 1000000L;
		if ((null == aEntity.getSuid())
				|| ((null != aEntity.getSuid()) && (0 == aEntity.getSuid()))) {
			aEntity.setTaskSuid(suid);
		}
		TaskEntity createdEntity = super.add(aEntity, aNeedPublishEvent);

		if (aNeedPublishEvent) {
			createEventByEntity(SystemEventType.CREATE, createdEntity);
		}
		return createdEntity;
	}

	@Override
	public void delete(Long aSuid, Boolean aNeedPublishEvent) {
		TaskEntity deletedEntity = repository.get(aSuid);
		historyService.deleteHistory(aSuid);
		super.delete(aSuid, aNeedPublishEvent);
		if (aNeedPublishEvent) {
			createEventByEntity(SystemEventType.DELETE, deletedEntity);
		}

	}

	@Override
	public void update(TaskEntity aEntity, Boolean aNeedPublishEvent) {
		TaskEntity oldEntity = get(aEntity.getSuid());
		TaskStatus oldStatus = (null != oldEntity) ? oldEntity.getStatus() : null;
		super.update(aEntity, aNeedPublishEvent);

		//	@formatter:off
		TaskHistoryEntity historyEntity = TaskHistoryEntity.builder()
				.taskSuid(aEntity.getSuid())
				.date(new Date())
				.build();
		//	@formatter:on

		SubordinationElementEntity subEl = null;
		switch (aEntity.getStatus()) {
		case DONE:
			if (aNeedPublishEvent) {
				//TODO добовить отправку
//				publisher.sendEvent(getEventByEntity(SystemEventType.DONE, aEntity));
				createEventByEntity(SystemEventType.DONE, aEntity);
			}
			historyEntity.setActorSuid(aEntity.getExecutorSuid());
			subEl = subElService.get(aEntity.getExecutorSuid());

			if (TaskStatus.OVERDUE.equals(oldStatus)) {
				historyEntity.setHistoryType(TaskHistoryType.TASK_OVERDUE_DONE);
				historyEntity.setText("Пользователь " + subEl.getName()
						+ " завершил выполнение задачи c опозданием");
				historyEntity.setTitle("Задача завершена с опозданием");
			} else {
				historyEntity.setHistoryType(TaskHistoryType.TASK_DONE);
				historyEntity
						.setText("Пользователь " + subEl.getName() + " завершил выполнение задачи");
				historyEntity.setTitle("Задача завершена");
			}

			break;
		case REWORK:
			if (aNeedPublishEvent) {
//				publisher.sendEvent(getEventByEntity(SystemEventType.REWORK, aEntity));
				createEventByEntity(SystemEventType.REWORK, aEntity);
			}
			historyEntity.setActorSuid(aEntity.getAuthorSuid());
			historyEntity.setHistoryType(TaskHistoryType.TASK_REWORK);
			subEl = subElService.get(aEntity.getAuthorSuid());
			historyEntity
					.setText("Пользователь " + subEl.getName() + " отправил задачу на доработку");
			historyEntity.setTitle("Задача отправлена на доработку");
			break;
		case FAILD:
			if (aNeedPublishEvent) {
//				publisher.sendEvent(getEventByEntity(SystemEventType.FAIL, aEntity));
				createEventByEntity(SystemEventType.FAIL, aEntity);
			}
			historyEntity.setActorSuid(aEntity.getAuthorSuid());
			historyEntity.setHistoryType(TaskHistoryType.TASK_FAILED);
			subEl = subElService.get(aEntity.getAuthorSuid());
			historyEntity
					.setText("Пользователь " + subEl.getName() + " отметил задачу как проваленную");
			historyEntity.setTitle("Задача провалена");
			break;
		case CLOSED:
			if (aNeedPublishEvent) {
//				publisher.sendEvent(getEventByEntity(SystemEventType.CLOSED, aEntity));
				createEventByEntity(SystemEventType.CLOSED, aEntity);
			}
			historyEntity.setActorSuid(aEntity.getAuthorSuid());
			historyEntity.setHistoryType(TaskHistoryType.TASK_CLOSED);
			subEl = subElService.get(aEntity.getAuthorSuid());
			historyEntity.setText("Пользователь " + subEl.getName() + " закрыл задачу");
			historyEntity.setTitle("Задача закрыта");
			break;
		default:
			if (aNeedPublishEvent) {
				createEventByEntity(SystemEventType.UPDATE, aEntity);
			}
			historyEntity.setActorSuid(aEntity.getAuthorSuid());
			historyEntity.setHistoryType(TaskHistoryType.TASK_UPDATED);
			subEl = subElService.get(aEntity.getAuthorSuid());
			historyEntity.setText("Пользователь " + subEl.getName() + " обновил задачу");
			historyEntity.setTitle("Задача обновлена");
		}

		if (null != historyEntity.getHistoryType()) {
			historyService.add(historyEntity, true);
		}
	}

	@Override
	protected TasksRepository createRepository() {
		return new TasksRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return TaskEvent.class;
	}

	@Override
	protected JmsTemplate getBroker() {
		return broker;
	}

	/**
	 * Создание события по задаче
	 *
	 * @param aType тип события
	 * @param aEntity задача
	 */
	public void createEventByEntity(SystemEventType aType, TaskEntity aEntity) {
		if (null != aEntity) {
			SubordinationElementEntity author = subElService.get(aEntity.getAuthorSuid());
			SubordinationElementEntity executer = subElService.get(aEntity.getExecutorSuid());

			EventEntity entity = new EventEntity();
			Long suid = (long) (Math.random() * 10000000L) + 1000000L;
			System.out.println();
			System.out.println(aType.getName() + " " + suid);
			System.out.println();
			entity.setDate(new Date());
			entity.setEventType(aType.getName());
			entity.setIdEvents(suid);

			entity.setAuthorSuid(author.getSuid());
			entity.setExecutorSuid(executer.getSuid());
			entity.setTaskSuid(aEntity.getTaskSuid());

			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
			String nameEntity = new String(Base64.getDecoder().decode(aEntity.getName()));

			switch (aType) {
			case CREATE:
				entity.setName(new String(Base64.getEncoder()
						.encode(("Создана задача \"" + nameEntity + "\"" + ", Дата начала: "
								+ formatter.format(aEntity.getStartDate())).getBytes())));

				break;
			case DELETE:
				entity.setName(new String(Base64.getEncoder().encode(("Удалена задача \""
						+ nameEntity + "\"" + ". " + formatter.format(new Date())).getBytes())));
				break;
			case UPDATE:
				entity.setName(new String(Base64.getEncoder().encode(("Обновлена задача \""
						+ nameEntity + "\"" + ". " + formatter.format(new Date())).getBytes())));
				break;
			case DONE:
				entity.setName(new String(Base64.getEncoder().encode(("Завершена задача \""
						+ nameEntity + "\"" + ". " + formatter.format(new Date())).getBytes())));
				break;
			case REWORK:
				entity.setName(
						new String(Base64.getEncoder()
								.encode(("Задача \"" + nameEntity + "\""
										+ " отправлена на доработку. "
										+ formatter.format(new Date())).getBytes())));
				break;
			case FAIL:
				entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
						+ "\"" + " отклонена. " + formatter.format(new Date())).getBytes())));
				break;
			case CLOSED:
				entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
						+ "\"" + " принята. " + formatter.format(new Date())).getBytes())));
			case OVERDUE:
				entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
						+ "\"" + " просрочена. " + formatter.format(new Date())).getBytes())));
			case TASK_REMAPPING:
				entity.setName(
						new String(
								Base64.getEncoder()
										.encode(("Задача \"" + nameEntity + "\""
												+ " переназначена. " + formatter.format(new Date()))
														.getBytes())));
			default:
				break;
			}
			eventService.add(entity, true);
		}
	}

	@Override
	public List<TaskEntity> getTasksByAuthor(Long aPositionSuid) {
		return ((TasksRepository) repository).getTasksByAuthor(aPositionSuid);
	}

	@Override
	public List<TaskEntity> getTasksByExecutor(Long aPositionSuid) {
		return ((TasksRepository) repository).getTasksByExecutor(aPositionSuid);
	}

	/**
	 * Получение типовых задач по SUID поевого поста
	 *
	 * @param aPositionSuid боевой пост
	 * @return список типовых задач
	 */
	@Override
	public List<DefaultTaskEntity> getDefaultTaskBySub(Long aPositionSuid) {
		return defaultTaskRepository.getDefaultTaskByExecuter(aPositionSuid);
	}

	/**
	 * Получение типовой задачи
	 *
	 * @param aPositionSuid SUID задачи
	 * @return сущность типовой задачи
	 */
	@Override
	public DefaultTaskEntity getDefaultTaskBySUID(Long aPositionSuid) {
		return defaultTaskRepository.get(aPositionSuid);
	}

	/**
	 * Получение всех типовых задач
	 *
	 * @return список типовых задач
	 */
	@Override
	public List<DefaultTaskEntity> getDefaultTaskAll() {
		return defaultTaskRepository.getAll();
	}

	@Override
	public List<TaskEntity> getAllUserTasks(Long aPositionSuid) {
		List<TaskEntity> result = new ArrayList<>();
		for (TaskType type : TaskType.values()) {
			result.addAll(((TasksRepository) repository).getTasksByType(aPositionSuid, type));
		}
		return result;
	}

	@Override
	public List<TaskEntity> getClosedTasks(Long aPositionSuid) {
		return getRepository().getTasksByType(aPositionSuid, TaskType.ARCHIVAL);
	}

	@Override
	public List<TaskEntity> getInTasks(Long aPositionSuid) {
		return ((TasksRepository) repository).getTasksByExecutor(aPositionSuid);
	}

	@Override
	public List<TaskEntity> getOutTasks(Long aPositionSuid) {
		return ((TasksRepository) repository).getTasksByAuthor(aPositionSuid);
	}

	@Override
	public List<TaskEntity> getDefaultTasks(Long aPositionSuid) {
		return getRepository().getDefaultTask(aPositionSuid);
	}

	@Override
	protected TasksRepository getRepository() {
		return (TasksRepository) repository;
	}

	@Override
	public List<TaskEntity> getAllTasksByAuthor(Long aPositionSuid) {
		return getRepository().getAllTasksByAuthor(aPositionSuid);
	}

	@Override
	public List<TaskEntity> getAllTasksByExecutor(Long aPositionSuid) {
		return getRepository().getAllTasksByExecutor(aPositionSuid);
	}

	@Override
	public List<TaskEntity> getSubElPageTasks(Long aPositionSuid, int aLimit, int aOffset) {
		return getRepository().getSubElPageTasks(aPositionSuid, aLimit, aOffset);
	}

	@Override
	public Long getCountTasks(TasksViewType aType, Long aPositionSuid, Filter aFilter) {
		return getRepository().getCountTasks(aType, aPositionSuid, aFilter);
	}

	@Override
	public List<TaskEntity> getCloseTasksWithPagination(Long aPositionSuid, int aLimit,
			int aOffset) {
		return getRepository().getTasksByTypeWithPagination(aPositionSuid, TaskType.ARCHIVAL,
				aLimit, aOffset);
	}

	@Override
	public List<TaskEntity> getInTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
		return getRepository().getInTasksWithPagination(aPositionSuid, aLimit, aOffset);
	}

	@Override
	public List<TaskEntity> getOutTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
		return getRepository().getOutTasksWithPagination(aPositionSuid, aLimit, aOffset);
	}

	@Override
	public List<TaskEntity> getDefaultTasksWithPagination(Long aPositionSuid, int aLimit,
			int aOffset) {
		return getRepository().getTasksByTypeWithPagination(aPositionSuid, TaskType.DEFAULT, aLimit,
				aOffset);
	}

	@Override
	public Integer getPageNumberByTask(Long aPositionSuid, Long aTaskSuid, Integer aCountTaskOnPage,
			TasksViewType aTypeView, Filter aFilter) {
		return calcTaskPageNumber(
				getRepository().getTasksByViewType(aPositionSuid, aTypeView, aFilter), aTaskSuid,
				aCountTaskOnPage);
	}

	/**
	 * Рассчитывает номер страницы по идентификатору задачи
	 *
	 * @param aCountTaskOnPage количество задач на странице
	 * @param aTaskSuid идентификатор задачи
	 * @return номер страницы
	 */
	private Integer calcTaskPageNumber(List<TaskEntity> aList, Long aTaskSuid,
			Integer aCountTaskOnPage) {
		Integer result = 0;
		if (null != aList) {
			int k = 0;
			int countPages = 0;
			for (TaskEntity task : aList) {
				if (k == aCountTaskOnPage) {
					k = 0;
					countPages++;
				}
				k++;
				if (task.getSuid().equals(aTaskSuid)) {
					result = countPages;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<DefaultTaskEntity> getDefaultTasksByAuthor(Long aPositionSuid) {
		return defaultTaskRepository.getDefaultTasksByAuthor(aPositionSuid);
	}

	@Override
	public List<Long> getDefaultTasksAuthorsSuids() {
		return defaultTaskRepository.getDefaultTasksAuthorsSuids();
	}

	@Override
	public List<TaskEntity> getTasksByViewTypeWithPagination(Long aPositionSuid,
			TasksViewType aViewType, int aLimit, int aOffset, Filter aFilter) {
		return getRepository().getTasksByViewTypeWithPagination(aPositionSuid, aViewType, aLimit,
				aOffset, aFilter);
	}

	@Override
	public List<TaskEntity> getCycleTasks() {
		return getRepository().getCycleTasks();
	}

	@Override
	public List<TaskEntity> getAllUserTasksWithFilter(Long aPositionSuid, Filter aFilter) {
		return getRepository().getAllUserTasksWithFilter(aPositionSuid, aFilter);
	}

	@Override
	public List<TaskEntity> getAllUserTasksAuthorWithFilter(Long aPositionSuid, Filter aFilter) {
		return getRepository().getAllUserTasksAuthorWithFilter(aPositionSuid, aFilter);
	}

	@Override
	public List<TaskEntity> getAllUserTasksExecutorWithFilter(Long aPositionSuid, Filter aFilter) {
		return getRepository().getAllUserTasksExecutorWithFilter(aPositionSuid, aFilter);
	}

	@Override
	public List<TaskEntity> getAll() {
		return getRepository().getAll();
	}

}
