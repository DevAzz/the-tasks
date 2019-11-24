/**
 *
 */
package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.TaskEntity;
import ru.devazz.repository.DefaultTaskRepository;
import ru.devazz.repository.TasksRepository;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.ITaskHistoryService;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.TaskEvent;
import ru.devazz.server.api.model.*;
import ru.devazz.server.api.model.enums.*;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.DefaultTaskConverter;
import ru.devazz.service.impl.converters.TaskEntityConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса взаимодействия с задачами
 */
@Service
public class TaskService extends AbstractEntityService<TaskModel, TaskEntity>
        implements ITaskService {

    /** Сервис событий */
    private IEventService eventService;

    /** Сервис работы с должностями */
    private ISubordinationElementService subElService;

    /** Сервис работы с историческими записями */
    private ITaskHistoryService historyService;

    /** Репозиторий типовых задач */
    private DefaultTaskRepository defaultTaskRepository;

    private JmsTemplate broker;

    private TaskEntityConverter converter;

    private TasksRepository repository;

    private DefaultTaskConverter defaultTaskConverter;

    public TaskService(IEventService eventService,
                       ISubordinationElementService subElService,
                       ITaskHistoryService historyService,
                       DefaultTaskRepository defaultTaskRepository,
                       JmsTemplate broker, TaskEntityConverter converter,
                       TasksRepository repository,
                       DefaultTaskConverter defaultTaskConverter) {
        super(repository, converter, broker);
        this.eventService = eventService;
        this.subElService = subElService;
        this.historyService = historyService;
        this.defaultTaskRepository = defaultTaskRepository;
        this.broker = broker;
        this.converter = converter;
        this.repository = repository;
        this.defaultTaskConverter = defaultTaskConverter;
    }

    @Override
    public TaskModel add(TaskModel aEntity, Boolean aNeedPublishEvent) {
        Long suid = (long) (Math.random() * 10000000L) + 1000000L;
        if ((null == aEntity.getSuid())
            || ((null != aEntity.getSuid()) && (0 == aEntity.getSuid()))) {
            aEntity.setSuid(suid);
        }
        TaskModel createdEntity = super.add(aEntity, aNeedPublishEvent);

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
            createEventByEntity(SystemEventType.DELETE, converter.entityToModel(deletedEntity));
        }

    }

    @Override
    public void update(TaskModel aEntity, Boolean aNeedPublishEvent) {
        TaskModel oldEntity = get(aEntity.getSuid());
        TaskStatus oldStatus = (null != oldEntity) ? oldEntity.getStatus() : null;
        super.update(aEntity, aNeedPublishEvent);

        //	@formatter:off
        TaskHistoryModel historyEntity = TaskHistoryModel.builder()
                .taskSuid(aEntity.getSuid())
                .date(new Date())
                .build();
        //	@formatter:on

        SubordinationElementModel subEl = null;
        switch (aEntity.getStatus()) {
            case DONE:
                if (aNeedPublishEvent) {
                    broker.convertAndSend(getQueueName(),
                                          getEventByEntity(SystemEventType.DONE, aEntity));
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
                            .setText("Пользователь " + subEl.getName() +
                                     " завершил выполнение задачи");
                    historyEntity.setTitle("Задача завершена");
                }

                break;
            case REWORK:
                if (aNeedPublishEvent) {
                    broker.convertAndSend(getQueueName(),
                                          getEventByEntity(SystemEventType.REWORK, aEntity));
                    createEventByEntity(SystemEventType.REWORK, aEntity);
                }
                historyEntity.setActorSuid(aEntity.getAuthorSuid());
                historyEntity.setHistoryType(TaskHistoryType.TASK_REWORK);
                subEl = subElService.get(aEntity.getAuthorSuid());
                historyEntity
                        .setText("Пользователь " + subEl.getName() +
                                 " отправил задачу на доработку");
                historyEntity.setTitle("Задача отправлена на доработку");
                break;
            case FAILD:
                if (aNeedPublishEvent) {
                    broker.convertAndSend(getQueueName(),
                                          getEventByEntity(SystemEventType.FAIL, aEntity));
                    createEventByEntity(SystemEventType.FAIL, aEntity);
                }
                historyEntity.setActorSuid(aEntity.getAuthorSuid());
                historyEntity.setHistoryType(TaskHistoryType.TASK_FAILED);
                subEl = subElService.get(aEntity.getAuthorSuid());
                historyEntity
                        .setText("Пользователь " + subEl.getName() +
                                 " отметил задачу как проваленную");
                historyEntity.setTitle("Задача провалена");
                break;
            case CLOSED:
                if (aNeedPublishEvent) {
                    broker.convertAndSend(getQueueName(),
                                          getEventByEntity(SystemEventType.CLOSED, aEntity));
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
                    broker.convertAndSend(getQueueName(),
                                          getEventByEntity(SystemEventType.UPDATE, aEntity));
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
    protected Class<? extends ObjectEvent> getTypeEntityEvent() {
        return TaskEvent.class;
    }

    @Override
    protected String getQueueName() {
        return JmsQueueName.TASKS.getName();
    }

    /**
     * Создание события по задаче
     *
     * @param aType тип события
     * @param aEntity задача
     */
    private void createEventByEntity(SystemEventType aType, TaskModel aEntity) {
        if (null != aEntity) {
            SubordinationElementModel author = subElService.get(aEntity.getAuthorSuid());
            SubordinationElementModel executer = subElService.get(aEntity.getExecutorSuid());

            EventModel entity = new EventModel();
            Long suid = (long) (Math.random() * 10000000L) + 1000000L;
            System.out.println();
            System.out.println(aType.getName() + " " + suid);
            System.out.println();
            entity.setDate(new Date());
            entity.setEventType(aType.getName());
            entity.setSuid(suid);

            entity.setAuthorSuid(author.getSuid());
            entity.setExecutorSuid(executer.getSuid());
            entity.setTaskSuid(aEntity.getSuid());

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String nameEntity = new String(Base64.getDecoder().decode(aEntity.getName()));

            switch (aType) {
                case CREATE:
                    entity.setName(new String(Base64.getEncoder()
                                                      .encode(("Создана задача \"" + nameEntity +
                                                               "\"" + ", Дата начала: "
                                                               + formatter
                                                                       .format(aEntity.getStartDate()))
                                                                      .getBytes())));

                    break;
                case DELETE:
                    entity.setName(new String(Base64.getEncoder().encode(("Удалена задача \""
                                                                          + nameEntity + "\"" +
                                                                          ". " + formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                    break;
                case UPDATE:
                    entity.setName(new String(Base64.getEncoder().encode(("Обновлена задача \""
                                                                          + nameEntity + "\"" +
                                                                          ". " + formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                    break;
                case DONE:
                    entity.setName(new String(Base64.getEncoder().encode(("Завершена задача \""
                                                                          + nameEntity + "\"" +
                                                                          ". " + formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                    break;
                case REWORK:
                    entity.setName(
                            new String(Base64.getEncoder()
                                               .encode(("Задача \"" + nameEntity + "\""
                                                        + " отправлена на доработку. "
                                                        + formatter.format(new Date()))
                                                               .getBytes())));
                    break;
                case FAIL:
                    entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
                                                                          + "\"" + " отклонена. " +
                                                                          formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                    break;
                case CLOSED:
                    entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
                                                                          + "\"" + " принята. " +
                                                                          formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                case OVERDUE:
                    entity.setName(new String(Base64.getEncoder().encode(("Задача \"" + nameEntity
                                                                          + "\"" + " просрочена. " +
                                                                          formatter
                                                                                  .format(new Date()))
                                                                                 .getBytes())));
                case TASK_REMAPPING:
                    entity.setName(
                            new String(
                                    Base64.getEncoder()
                                            .encode(("Задача \"" + nameEntity + "\""
                                                     + " переназначена. " +
                                                     formatter.format(new Date()))
                                                            .getBytes())));
                default:
                    break;
            }
            eventService.add(entity, true);
        }
    }

    @Override
    public List<TaskModel> getTasksByAuthor(Long aPositionSuid) {
        return repository.getTasksByAuthor(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getTasksByExecutor(Long aPositionSuid) {
        return repository.getTasksByExecutor(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    /**
     * Получение типовых задач по SUID поевого поста
     *
     * @param aPositionSuid должность
     * @return список типовых задач
     */
    @Override
    public List<DefaultTaskModel> getDefaultTaskBySub(Long aPositionSuid) {
        return defaultTaskRepository.getDefaultTaskByExecuter(aPositionSuid).stream()
                .map(defaultTaskConverter::entityToModel).collect(Collectors.toList());
    }

    /**
     * Получение типовой задачи
     *
     * @param aPositionSuid SUID задачи
     * @return сущность типовой задачи
     */
    @Override
    public DefaultTaskModel getDefaultTaskBySUID(Long aPositionSuid) {
        return defaultTaskConverter.entityToModel(defaultTaskRepository.get(aPositionSuid));
    }

    /**
     * Получение всех типовых задач
     *
     * @return список типовых задач
     */
    @Override
    public List<DefaultTaskModel> getDefaultTaskAll() {
        return defaultTaskRepository.getAll().stream().map(defaultTaskConverter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllUserTasks(Long aPositionSuid) {
        List<TaskModel> result = new ArrayList<>();
        for (TaskType type : TaskType.values()) {
            result.addAll(repository.getTasksByType(aPositionSuid, type).stream()
                                  .map(converter::entityToModel).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public List<TaskModel> getClosedTasks(Long aPositionSuid) {
        return repository.getTasksByType(aPositionSuid, TaskType.ARCHIVAL).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getInTasks(Long aPositionSuid) {
        return repository.getTasksByExecutor(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getOutTasks(Long aPositionSuid) {
        return repository.getTasksByAuthor(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getDefaultTasks(Long aPositionSuid) {
        return repository.getDefaultTask(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllTasksByAuthor(Long aPositionSuid) {
        return repository.getAllTasksByAuthor(aPositionSuid).stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllTasksByExecutor(Long aPositionSuid) {
        return repository.getAllTasksByExecutor(aPositionSuid).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getSubElPageTasks(Long aPositionSuid, int aLimit, int aOffset) {
        return repository.getSubElPageTasks(aPositionSuid, aLimit, aOffset).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public Long getCountTasks(TasksViewType aType, Long aPositionSuid, Filter aFilter) {
        return repository.getCountTasks(aType, aPositionSuid, aFilter);
    }

    @Override
    public List<TaskModel> getCloseTasksWithPagination(Long aPositionSuid, int aLimit,
                                                       int aOffset) {
        return repository.getTasksByTypeWithPagination(aPositionSuid, TaskType.ARCHIVAL,
                                                       aLimit, aOffset).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getInTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
        return repository.getInTasksWithPagination(aPositionSuid, aLimit, aOffset).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getOutTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
        return repository.getOutTasksWithPagination(aPositionSuid, aLimit, aOffset).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getDefaultTasksWithPagination(Long aPositionSuid, int aLimit,
                                                         int aOffset) {
        return repository.getTasksByTypeWithPagination(aPositionSuid, TaskType.DEFAULT, aLimit,
                                                       aOffset).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public Integer getPageNumberByTask(Long aPositionSuid, Long aTaskSuid, Integer aCountTaskOnPage,
                                       TasksViewType aTypeView, Filter aFilter) {
        return calcTaskPageNumber(
                repository.getTasksByViewType(aPositionSuid, aTypeView, aFilter), aTaskSuid,
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
    public List<DefaultTaskModel> getDefaultTasksByAuthor(Long aPositionSuid) {
        return defaultTaskRepository.getDefaultTasksByAuthor(aPositionSuid).stream()
                .map(defaultTaskConverter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<Long> getDefaultTasksAuthorsSuids() {
        return defaultTaskRepository.getDefaultTasksAuthorsSuids();
    }

    @Override
    public List<TaskModel> getTasksByViewTypeWithPagination(Long aPositionSuid,
                                                            TasksViewType aViewType, int aLimit,
                                                            int aOffset, Filter aFilter) {
        return repository.getTasksByViewTypeWithPagination(aPositionSuid, aViewType, aLimit,
                                                           aOffset, aFilter).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getCycleTasks() {
        return repository.getCycleTasks().stream().map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllUserTasksWithFilter(Long aPositionSuid, Filter aFilter) {
        return repository.getAllUserTasksWithFilter(aPositionSuid, aFilter).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllUserTasksAuthorWithFilter(Long aPositionSuid, Filter aFilter) {
        return repository.getAllUserTasksAuthorWithFilter(aPositionSuid, aFilter).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAllUserTasksExecutorWithFilter(Long aPositionSuid, Filter aFilter) {
        return repository.getAllUserTasksExecutorWithFilter(aPositionSuid, aFilter).stream()
                .map(converter::entityToModel).collect(Collectors.toList());
    }

    @Override
    public List<TaskModel> getAll() {
        return repository.getAll().stream().map(converter::entityToModel)
                .collect(Collectors.toList());
    }

}
