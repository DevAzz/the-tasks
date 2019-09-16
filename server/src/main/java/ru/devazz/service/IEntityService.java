package ru.devazz.service;

import ru.devazz.entity.IEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.utils.SystemEventType;

import java.util.List;

/**
 * Интерфейс сервиса, взаимодействующего с базой данных
 */
public interface IEntityService<T extends IEntity> extends ICommonService {

	/**
	 * Добавляет сущность в базу данных
	 *
	 * @param aEntity сущность на основе которого создается запись в таблице
	 * @param aNeedPublishEvent необходимость публиковать событие добавления
	 * @return добавленная сущность в случае успеха или {@code null} в случае ошибки
	 */
	public T add(T aEntity, Boolean aNeedPublishEvent);

	/**
	 * Удаляет сущность
	 *
	 * @param aNeedPublishEvent необходимость публиковать событие удаления
	 * @param aSuid идентификатор сущности
	 */
	public void delete(Long aSuid, Boolean aNeedPublishEvent);

	/**
	 * Поиск сущности по ее идентификатору
	 *
	 * @param aSuid идентификатор сущности
	 * @return возвращает сущность
	 */
	public T get(Long aSuid);

	/**
	 * Редактирует сущность
	 *
	 * @param aEntity сущность
	 * @param aNeedPublishEvent необходимость публиковать событие обновления
	 */
	public void update(T aEntity, Boolean aNeedPublishEvent);

	/**
	 * Возвращает список всех сущностей
	 *
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список сущностей
	 */
	public List<T> getAll(Long aUserSuid);

	/**
	 * Возвращает событие по теме и сущности
	 *
	 * @param aType тип события
	 * @param aEntity сущность
	 * @return событие
	 */
	ObjectEvent getEventByEntity(SystemEventType aType, T aEntity);

}
