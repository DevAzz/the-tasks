package ru.devazz.utils;

/**
 * Тип события
 */
public enum EventType {

	CREATED("Создана новая задача", "created"),

	DELETED("Задача удалена", "deleted"),

	OVERDUE("Задача просрочена", "overdue"),

	UPDATED("Задача обновлена", "updated"),

	DONE("Задача выполнена", "done"),

	REWORK("Задача отправлена на доработку", "rework"),

	FAILD("Задача провалена", "failed"),

	CLOSED("Задача принята", "closed");

	/** Наименование типа */
	private String name;

	/** Идентификатор типа */
	private String suid;

	/**
	 * Конструктор
	 *
	 * @param name Наименование типа
	 * @param suid Идентификатор типа
	 */
	private EventType(String name, String suid) {
		this.name = name;
		this.suid = suid;
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает значение полю name
	 *
	 * @param name значение поле
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#suid}
	 *
	 * @return the suid
	 */
	public String getSuid() {
		return suid;
	}

	/**
	 * Устанавливает значение полю suid
	 *
	 * @param suid значение поле
	 */
	public void setSuid(String suid) {
		this.suid = suid;
	}

}
