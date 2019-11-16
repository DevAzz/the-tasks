package ru.devazz.server.api.model;

/**
 * Интерфейс сущности
 */
public interface IEntity {

	/**
	 * Возвращает наименование сущности
	 *
	 * @return наименование сущности
	 */
	String getName();

	/**
	 * Возвращает идентификатор записи
	 * 
	 * @return идентификатор записи
	 */
	Long getSuid();

}
