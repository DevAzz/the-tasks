package ru.devazz.entity;

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
