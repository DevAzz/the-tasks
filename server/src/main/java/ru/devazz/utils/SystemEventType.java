package ru.devazz.utils;

/**
 * Типы событий
 */
public enum SystemEventType {

	CREATE("created"),

	UPDATE("updated"),

	FAIL("failed"),

	OVERDUE("overdue"),

	DELETE("deleted"),

	DONE("done"),

	REWORK("rework"),

	CLOSED("closed"),

	USER_ONLINE("userOnline"),

	USER_OFFLINE("userOffline"),

	TASK_REMAPPING("taskRemapping"),

	TIME_LEFT_OVER("time_left_over");

	/** Наименование */
	private String name;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private SystemEventType(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the {@link#name}
	 */
	public String getName() {
		return name;
	}

}
