package ru.devazz.server.api.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Приоритет задачи
 */
public enum TaskPriority {

	CRITICAL("Критичный", "priorityCritical", 4),

	MAJOR("Высокий", "priorityMajor", 3),

	MINOR("Средний", "priorityMinor", 2),

	TRIVIAL("Низкий", "priorityTrivial", 1),

	EVERYDAY("Повседневный", "priorityEveryDay", 0);

	/** Наименование */
	private String name;

	/** Идентификатор меню */
	private String menuSuid;

	/** Вес */
	private Integer weight;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private TaskPriority(String name, String menuSuid, Integer aWeight) {
		this.name = name;
		this.menuSuid = menuSuid;
		this.weight = aWeight;
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
	 * Возвращает {@link#weight}
	 *
	 * @return the {@link#weight}
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * Возвращает приоритет по весу
	 *
	 * @param aWeight вес приоритета
	 * @return приоритет
	 */
	public static TaskPriority getPriorityByWeight(Integer aWeight) {
		TaskPriority priority = null;
		for (TaskPriority value : TaskPriority.values()) {
			Integer weight = value.weight;
			if (weight.equals(aWeight)) {
				priority = value;
				break;
			}
		}
		return priority;
	}

	/**
	 * Возвращает приоритет по идентификатору меню
	 *
	 * @param aSuid идентификатор меню
	 * @return приоритет
	 */
	public static TaskPriority getPriorityBySuid(String aSuid) {
		TaskPriority priority = null;
		for (TaskPriority value : TaskPriority.values()) {
			String suid = value.menuSuid;
			if (suid.equals(aSuid)) {
				priority = value;
				break;
			}
		}
		return priority;
	}

	/**
	 *
	 * Возвразщает приоритет по имени
	 *
	 * @param aName имя
	 * @return приоритет
	 */
	public static TaskPriority getPriorityByName(String aName) {
		TaskPriority priority = null;
		for (TaskPriority value : TaskPriority.values()) {
			String name = value.name;
			if (name.equals(aName)) {
				priority = value;
				break;
			}
		}
		return priority;
	}

	/**
	 * Воззвращает список идентификаторов
	 *
	 * @return список идентификаторов
	 */
	public static List<String> getSuids() {
		List<String> result = new ArrayList<>();
		for (TaskPriority priority : values()) {
			result.add(priority.getMenuSuid());
		}
		return result;
	}

	/**
	 * Воззвращает список имен
	 *
	 * @return список имен
	 */
	public static List<String> getNames() {
		List<String> result = new ArrayList<>();
		for (TaskPriority priority : values()) {
			result.add(priority.getName());
		}
		return result;
	}

}
