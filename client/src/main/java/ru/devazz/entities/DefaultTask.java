package ru.devazz.entities;

public class DefaultTask {

	/** Идентификатор */
	private Long defaultTaskID;

	/** Наименование */
	private String name;

	/** Примечание */
	private String note;

	/** Время начала задачи */
	private String startTime;

	/** Время конца задачи */
	private String endTime;

	/** идентификатор боевого поста (исполнитель задачи) */
	private Long subordinationSUID;

	/**
	 * Конструктор
	 *
	 * @param defaultTaskID идентификатор типовой задачи
	 * @param name наименование
	 * @param note примечание
	 * @param startTime дата начала
	 * @param endTime дата завершения
	 * @param subordinationSUID идентификатор элемента подчиненности (исполнитель)
	 */
	public DefaultTask(Long defaultTaskID, String name, String note, String startTime, String endTime,
			Long subordinationSUID) {
		super();
		this.defaultTaskID = defaultTaskID;
		this.name = name;
		this.note = note;
		this.startTime = startTime;
		this.endTime = endTime;
		this.subordinationSUID = subordinationSUID;
	}

	/**
	 * Конструктор
	 */
	public DefaultTask() {
	}

	/**
	 * Возвращяет идентификатор типовой задачи {link#defaultTaskID}
	 *
	 * @return the {link#defaultTaskID}
	 */
	public long getDefaultTaskID() {
		return defaultTaskID;
	}

	/**
	 * Устанавиливает индентификатор типовой задачи {link# defaultTaskID}
	 *
	 * @param defaultTaskID значение поля
	 */
	public void setDefaultTaskID(long defaultTaskID) {
		this.defaultTaskID = defaultTaskID;
	}

	/**
	 * Возвращяет имея типовой задачи {link#name}
	 *
	 * @return the {link#name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает имя типовой задачи {link#name}
	 *
	 * @param name значение поля
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возаращает описание типовой задачи {link#note}
	 *
	 * @return the {link#note}
	 */
	public String getNote() {
		return note;
	}

	/**
	 * Устанваливает описание типовой задачи {link#note}
	 *
	 * @param note значение поля
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Возвращяет время начала задачи {link#start_time}
	 *
	 * @return the {link#start_time}
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * Устанавливает время начала задачи {link#start_time}
	 *
	 * @param startTime значение поля
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * Возвращяет время конца задачи {link#end_time}
	 *
	 * @return the {link#end_time}
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * Устанавливает время конца задачи {link#end_time}
	 *
	 * @param endTime значение поля
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * Возвращяет индентификатор боевого поста {link#subordinationSUID}
	 *
	 * @return the {link#subordinationSUID}
	 */
	public Long getSubordinationSUID() {
		return subordinationSUID;
	}

	/**
	 * Устанавливает индентификатор боевого поста {link#subordinationSUID}
	 *
	 * @param subordinationSUID значение поля
	 */
	public void setSubordinationSUID(Long subordinationSUID) {
		this.subordinationSUID = subordinationSUID;
	}

	/**
	 * Возвращяет идентификатор типовой задачи {link#defaultTaskID}
	 *
	 * @return the {link#defaultTaskID}
	 */
	public Long getSuid() {
		return defaultTaskID;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
