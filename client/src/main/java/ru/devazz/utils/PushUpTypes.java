package ru.devazz.utils;

/**
 * Типы popup сообщений
 */

public enum PushUpTypes {
	/**
	 * Сообщение об истечении времени
	 */
	TIME_LEFT_OVER_PUSH("task time out"),
	/**
	 * Сообщение о новой задаче
	 */
	NEW_PUSH("new task"),
	/**
	 * Сообщение типа "Внимание"
	 */
	ATTENTION_PUSH("attention"),

	/** Сообщение о просроченной задаче */
	OVERDUE_PUSH("overdue"),

	/** Сообщение о удаленной задаче */
	DELETED_PUSH("deleted"),

	/** Сообщение о выполненой задаче */
	DONE_PUSH("done"),

	/** Сообщение о задаче, отправленной на доработку */
	REWORK_PUSH("rework"),

	/** Сообщение о проваленной задаче */
	CLOSED_PUSH("closed"),

	/** Сообщение о проваленной задаче */
	FAILED_PUSH("failed"),

	/** Сообщение о входе пользователя в систему */
	USER_ONLINE_PUSH("userOnline"),

	/**
	 * Информативное сообщение (в основном приветствия)
	 */
	HELLO_PUSH("hello");

	/** Имя статуса */
	private String name;

	/**
	 * Конструктор типа
	 */
	private PushUpTypes(String name) {
		this.name = name;
	}

	/**
	 * Возвращает имя типа
	 *
	 * @return тип сообщения
	 */
	public String getName() {
		return name;
	}

	/**
	 * Установка имени типа
	 *
	 * @param name имя типа сообщения
	 */
	public void setName(String name) {
		this.name = name;
	}

}
