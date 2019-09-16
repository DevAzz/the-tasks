package ru.devazz.utils;

/**
 * День недели
 */
public enum DayOfWeek {

	/** Понедельник */
	MONDAY("Понедельник"),

	/** Вторник */
	TUESDAY("Вторник"),

	/** Среда */
	WEDNESDAY("Среда"),

	/** Четверг */
	THURSDAY("Четверг"),

	/** Пятница */
	FRIDAY("Пятница"),

	/** Суббота */
	SATURDAY("Суббота"),

	/** Воскресенье */
	SUNDAY("Воскресенье");

	/** Наименование */
	private String name;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private DayOfWeek(String name) {
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

	/**
	 * Возвращает элемент перечисления по его имени
	 * 
	 * @param aName имя
	 * @return элемент перечисления
	 */
	public static DayOfWeek getDayByName(String aName) {
		DayOfWeek result = null;
		for (DayOfWeek value : DayOfWeek.values()) {
			if (value.name.equals(aName)) {
				result = value;
				break;
			}
		}
		return result;
	}

}
