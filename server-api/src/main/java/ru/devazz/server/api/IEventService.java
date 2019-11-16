package ru.devazz.server.api;

import ru.devazz.server.api.model.EventModel;

import java.util.List;

/**
 * Общий интерфейс работы с событиями
 */
public interface IEventService extends IEntityService<EventModel> {

	/**
	 * Возвращает список событий по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return список событий
	 */
	public List<EventModel> getEventsByTaskSuid(Long aSuid);

	/**
	 * Удаляет события по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 */
	public void deleteEventsByTaskSuid(Long aSuid);

}
