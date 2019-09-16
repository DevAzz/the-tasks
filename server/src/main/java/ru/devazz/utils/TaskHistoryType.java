package ru.devazz.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Перечисление типов исторических записей
 */
public enum TaskHistoryType {

	/** Задача завершена */
	TASK_DONE("taskDoneType"),

	/** Задача завершена с опозданием */
	TASK_OVERDUE_DONE("taskOverdueDone"),

	/** Задача оправлена на доработку */
	TASK_REWORK("taskReworkType"),

	/** Задача провалена */
	TASK_FAILED("taskFailedType"),

	/** Задача обновлена */
	TASK_UPDATED("taskUpdatedType"),

	/** Задача просрочена */
	TASK_OVERDUE("taskOverdueType"),

	/** Задача переназначена */
	TASK_REMAPPING("taskRemappingType"),

	/** Задача закрыта */
	TASK_CLOSED("taskClosedType");

	/** Идентификатор типа */
	private String typeSuid;

	/**
	 * Конструктор
	 *
	 * @param typeSuid
	 */
	private TaskHistoryType(String typeSuid) {
		this.typeSuid = typeSuid;
	}

	/**
	 * Возвращает {@link#typeSuid}
	 *
	 * @return the {@link#typeSuid}
	 */
	public String getTypeSuid() {
		return typeSuid;
	}

	/**
	 * Воззвращает список идентификаторов
	 *
	 * @return список идентификаторов
	 */
	public static List<String> getSuids() {
		List<String> result = new ArrayList<>();
		for (TaskHistoryType status : values()) {
			result.add(status.getTypeSuid());
		}
		return result;
	}

	/**
	 * Возвращяет тип исторической записи по идентификатору
	 *
	 * @param aSuid идентификатор
	 * @return тип исторической записи
	 */
	public static TaskHistoryType getTaskHistoryTypeBySuid(String aSuid) {
		TaskHistoryType status = null;
		for (TaskHistoryType value : TaskHistoryType.values()) {
			String suid = value.typeSuid;
			if (suid.equals(aSuid)) {
				status = value;
				break;
			}
		}
		return status;
	}

}
