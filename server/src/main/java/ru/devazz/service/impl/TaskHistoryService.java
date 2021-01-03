package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.TaskHistoryEntity;
import ru.devazz.repository.TaskHistoryRepository;
import ru.devazz.server.api.ITaskHistoryService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.event.TaskHistoryEvent;
import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.TaskHistoryModel;
import ru.devazz.server.api.model.enums.TaskHistoryType;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.TaskHistoryEntityConverter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса взаимодействия с историческими записями
 */
@Service
public class TaskHistoryService extends AbstractEntityService<TaskHistoryModel, TaskHistoryEntity>
		implements ITaskHistoryService {

	private JmsTemplate broker;

	private TaskHistoryRepository repository;

	private TaskHistoryEntityConverter converter;

	public TaskHistoryService(JmsTemplate broker,
			TaskHistoryRepository repository,
			TaskHistoryEntityConverter converter) {
		super(repository, converter, broker);
		this.broker = broker;
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	public TaskHistoryModel add(TaskHistoryModel aEntity, Boolean aNeedPublishEvent) {
		if (!checkRepeatTaskHistoryEntry(aEntity)) {
			Long suid = (long) (Math.random() * 10000000L) + 1000000L;
			aEntity.setSuid(suid);
			super.add(aEntity, aNeedPublishEvent);
		}
		return aEntity;
	}

	@Override
	public List<TaskHistoryModel> getTaskHistory(Long aTaskSuid, Filter aFilter) {
		return repository.getTaskHistory(aTaskSuid, aFilter).stream().map(converter::entityToModel)
				.collect(Collectors.toList());
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return TaskHistoryEvent.class;
	}

	@Override
	protected String getQueueName() {
		return QueueNameEnum.TASKS_HISTORY_QUEUE;
	}

	@Override
	public List<TaskHistoryModel> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
			int aOffset) {
		return repository.getTaskHistoryWithPagination(aTaskSuid, aLimit, aOffset).stream().map(converter::entityToModel)
				.collect(Collectors.toList());
	}

	@Override
	public int getCountPages(Long aTaskSuid, Integer aCountEntriesOnPage, Filter aFilter) {
		return repository.getCountPages(aTaskSuid, aCountEntriesOnPage, aFilter);
	}

	@Override
	public Integer getNumberPageByTask(TaskHistoryModel aEntry, Long aTaskSuid,
			Integer aCountTaskOnPage, Filter aFilter) {
		return repository.getNumberPageByTask(converter.modelToEntity(aEntry), aTaskSuid,
											  aCountTaskOnPage,
											  aFilter);
	}

	@Override
	public List<TaskHistoryModel> getAllTaskHistoryEntriesByType(Long aTaskSuid,
			TaskHistoryType aType) {
		return repository.getAllTaskHistoryEntriesByType(aTaskSuid, aType).stream().map(converter::entityToModel)
				.collect(Collectors.toList());
	}

	/**
	 * Проверяет наличие эквивалентных исторических записей для задачи (записей с
	 * тем же временем)
	 *
	 * @param aEntry добавляемая запись
	 * @return {@code true} - в случае, если есть хотя бы одна такая запись
	 */
	private boolean checkRepeatTaskHistoryEntry(TaskHistoryModel aEntry) {
		boolean result = false;
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		List<TaskHistoryEntity> list = repository
				.getAllTaskHistoryEntriesByType(aEntry.getTaskSuid(), aEntry.getHistoryType());
		for (TaskHistoryEntity entity : list) {
			String entityDate = parser.format(entity.getDate());
			String addEntryDate = parser.format(aEntry.getDate());
			if (entityDate.equals(addEntryDate)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public List<TaskHistoryModel> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
															   int aOffset, Filter aFilter) {
		return repository.getTaskHistoryWithPagination(aTaskSuid, aLimit, aOffset, aFilter).stream()
				.map(converter::entityToModel).collect(
						Collectors.toList());
	}

	@Override
	public void deleteHistory(Long aTaskSuid) {
		repository.deleteHistory(aTaskSuid);
	}

}
