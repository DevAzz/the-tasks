package ru.devazz.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Перечисление временных интервалов
 */
public enum TaskTimeInterval {

	DAY("За день", "dayFilterItem"),

	WEEK("За неделю", "weekFilterItem"),

	MONTH("За месяц", "monthFilterItem"),

	ALL_TIME("За все время", "allTimeFilterItem"),

	PARTICULAR_TIME_INTERVAL("", "particularTimeInterval"),

	CUSTOM_TIME_INTERVAL("Выбрать временной промежуток", "timeIntervalFilterItem");

	/** Наименование */
	private String name;

	/** Идентификатор */
	private String menuSuid;

	/**
	 * Конструктор
	 *
	 * @param name наименование
	 * @param menuSuid идентификатор
	 */
	private TaskTimeInterval(String name, String menuSuid) {
		this.name = name;
		this.menuSuid = menuSuid;
	}

	/**
	 * Конструктор
	 *
	 * @param name наименование
	 * @param menuSuid идентификатор
	 */
	private TaskTimeInterval(String name, String menuSuid, Date aStartDate) {
		this.name = name;
		this.menuSuid = menuSuid;
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
	 * Возвращает {@link#menuSuid}
	 *
	 * @return the {@link#menuSuid}
	 */
	public String getMenuSuid() {
		return menuSuid;
	}

	/**
	 * Воззвращает список идентификаторов
	 *
	 * @return список идентификаторов
	 */
	public static List<String> getSuids() {
		List<String> result = new ArrayList<>();
		for (TaskTimeInterval status : values()) {
			result.add(status.getMenuSuid());
		}
		return result;
	}

	public static TaskTimeInterval getTimeIntervalBySuid(String aSuid) {
		TaskTimeInterval status = null;
		for (TaskTimeInterval value : TaskTimeInterval.values()) {
			String suid = value.menuSuid;
			if (suid.equals(aSuid)) {
				status = value;
				break;
			}
		}
		return status;
	}

}
