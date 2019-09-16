package ru.devazz.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Статус задачи
 */
public enum TaskStatus {

	DONE("Выполнена", "statusDone"),

	CLOSED("Закрыта", "statusClosed"),

	ASSIGNED("Назначена", "statusAssigned"),

	WORKING("В работе", "statusWorking"),

	FAILD("Не выполнена", "statusFaild"),

	OVERDUE("Просрочена", "statusOverdue"),

	REWORK("На доработке", "statusRework");

	/** Наименование статуса */
	private String name;

	private String menuSuid;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private TaskStatus(String name, String menuSuid) {
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
		for (TaskStatus status : values()) {
			result.add(status.getMenuSuid());
		}
		return result;
	}

	public static TaskStatus getStatusBySuid(String aSuid) {
		TaskStatus status = null;
		for (TaskStatus value : TaskStatus.values()) {
			String suid = value.menuSuid;
			if (suid.equals(aSuid)) {
				status = value;
				break;
			}
		}
		return status;
	}

}
