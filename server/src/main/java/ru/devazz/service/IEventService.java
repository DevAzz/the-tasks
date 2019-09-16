package ru.devazz.service;

import ru.devazz.entity.EventEntity;

import java.util.List;

/**
 * Общий интерфейс работы с событиями
 */
public interface IEventService extends IEntityService<EventEntity> {

	/**
	 * Возвращает список событий по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return список событий
	 */
	public List<EventEntity> getEventsByTaskSuid(Long aSuid);

	/**
	 * Удаляет события по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 */
	public void deleteEventsByTaskSuid(Long aSuid);

}
