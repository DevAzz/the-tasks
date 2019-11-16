package ru.devazz.server.api.model.enums;

/**
 * Перечисление типов сортировки
 */
public enum SortType {

	/** Сортировка по дате (по убыванию) */
	SORT_BY_DATE_FIRST_OLD,

	/** Сортировка по дате (по возврастанию) */
	SORT_BY_DATE_FIRST_NEW,

	/** Сортировка по приоритету (по убыванию) */
	SORT_BY_PRIORITY_DESCENDING,

	/** Сортировка по приоритету (по возрастанию) */
	SORT_BY_PRIORITY_ASCENDING;

}
