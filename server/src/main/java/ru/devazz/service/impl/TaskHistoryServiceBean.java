package ru.devazz.service.impl;

import ru.devazz.entity.TaskHistoryEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.event.TaskHistoryEvent;
import ru.devazz.repository.AbstractRepository;
import ru.devazz.repository.TaskHistoryRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.ITaskHistoryService;
import ru.devazz.utils.Filter;
import ru.devazz.utils.TaskHistoryType;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Реализация интерфейса взаимодействия с историческими записями
 */
public class TaskHistoryServiceBean extends AbstractEntityService<TaskHistoryEntity>
		implements ITaskHistoryService {

	@Override
	public TaskHistoryEntity add(TaskHistoryEntity aEntity, Boolean aNeedPublishEvent) {
		if (!checkRepeatTaskHistoryEntry(aEntity)) {
			Long suid = (long) (Math.random() * 10000000L) + 1000000L;
			aEntity.setSuid(suid);
			super.add(aEntity, aNeedPublishEvent);
		}
		return aEntity;
	}

	@Override
	public List<TaskHistoryEntity> getTaskHistory(Long aTaskSuid, Filter aFilter) {
		return getRepository().getTaskHistory(aTaskSuid, aFilter);
	}

	@Override
	protected AbstractRepository<TaskHistoryEntity> createRepository() {
		return new TaskHistoryRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return TaskHistoryEvent.class;
	}

	@Override
	protected TaskHistoryRepository getRepository() {
		return (TaskHistoryRepository) repository;
	}

	@Override
	public List<TaskHistoryEntity> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
			int aOffset) {
		return getRepository().getTaskHistoryWithPagination(aTaskSuid, aLimit, aOffset);
	}

	@Override
	public int getCountPages(Long aTaskSuid, Integer aCountEntriesOnPage, Filter aFilter) {
		return getRepository().getCountPages(aTaskSuid, aCountEntriesOnPage, aFilter);
	}

	@Override
	public Integer getNumberPageByTask(TaskHistoryEntity aEntry, Long aTaskSuid,
			Integer aCountTaskOnPage, Filter aFilter) {
		return getRepository().getNumberPageByTask(aEntry, aTaskSuid, aCountTaskOnPage, aFilter);
	}

	@Override
	public List<TaskHistoryEntity> getAllTaskHistoryEntriesByType(Long aTaskSuid,
			TaskHistoryType aType) {
		return getRepository().getAllTaskHistoryEntriesByType(aTaskSuid, aType);
	}

	/**
	 * Проверяет наличие эквивалентных исторических записей для задачи (записей с
	 * тем же временем)
	 *
	 * @param aEntry добавляемая запись
	 * @return {@code true} - в случае, если есть хотя бы одна такая запись
	 */
	private boolean checkRepeatTaskHistoryEntry(TaskHistoryEntity aEntry) {
		boolean result = false;
		SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		List<TaskHistoryEntity> list = getRepository()
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
	public List<TaskHistoryEntity> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
			int aOffset, Filter aFilter) {
		return getRepository().getTaskHistoryWithPagination(aTaskSuid, aLimit, aOffset, aFilter);
	}

	@Override
	public void deleteHistory(Long aTaskSuid) {
		getRepository().deleteHistory(aTaskSuid);
	}

}
