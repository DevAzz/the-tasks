package ru.devazz.entities;

import ru.siencesquad.hqtasks.ui.utils.EventType;

import java.util.Date;

/**
 * Событие системы
 */
public class Event {

	/** Иднентификатор стобытия */
	private Long suid;

	/** Идентификатор задачи */
	private Long taskId;

	/** Наименование события */
	private String name;

	/** Дата события */
	private Date date;

	/** Тип события для фильтра по типу события */
	private EventType eventType;

	/** Наименование боевого, связанного с событием */
	private SubordinationElement author;

	/** Наименование боевого, связанного с событием */
	private SubordinationElement executor;

	/**
	 * Конструктор
	 */
	public Event() {

	}

	/**
	 * Конструктор
	 *
	 * @param name наименование события
	 * @param aDate дата
	 * @param aPosition должностное лицо
	 */
	public Event(Long aSuid, String name, Date aDate, Long aTaskSuid, EventType aEventType,
			SubordinationElement aAuthor, SubordinationElement aExecutor) {
		super();
		this.suid = aSuid;
		this.taskId = aTaskSuid;
		this.name = name;
		this.date = aDate;
		this.eventType = aEventType;
		this.author = aAuthor;
		this.executor = aExecutor;
	}

	/**
	 * Возвращает {@link#eventType}
	 *
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * Устанавливает значение полю eventType
	 *
	 * @param eventType значение поле
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
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
	 * Устанавливает значение полю {@link#name}
	 *
	 * @param {@link#name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#date}
	 *
	 * @return the {@link#date}
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Устанавливает значение полю {@link#date}
	 *
	 * @param {@link#date}
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Возвращает {@link#suid}
	 *
	 * @return the {@link#suid}
	 */
	public Long getSuid() {
		return suid;
	}

	/**
	 * Устанавливает значение полю {@link#suid}
	 *
	 * @param {@link#suid}
	 */
	public void setSuid(Long suid) {
		this.suid = suid;
	}

	/**
	 * Возвращает {@link#taskId}
	 *
	 * @return the {@link#taskId}
	 */
	public Long getTaskId() {
		return taskId;
	}

	/**
	 * Устанавливает значение полю {@link#taskId}
	 *
	 * @param taskId значение поля
	 */
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	/**
	 * Возвращает {@link#author}
	 *
	 * @return the {@link#author}
	 */
	public SubordinationElement getAuthor() {
		return author;
	}

	/**
	 * Устанавливает значение полю {@link#author}
	 *
	 * @param author значение поля
	 */
	public void setAuthor(SubordinationElement author) {
		this.author = author;
	}

	/**
	 * Возвращает {@link#executor}
	 *
	 * @return the {@link#executor}
	 */
	public SubordinationElement getExecutor() {
		return executor;
	}

	/**
	 * Устанавливает значение полю {@link#executor}
	 *
	 * @param executor значение поля
	 */
	public void setExecutor(SubordinationElement executor) {
		this.executor = executor;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((date == null) ? 0 : date.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + ((suid == null) ? 0 : suid.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Event other = (Event) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (suid == null) {
			if (other.suid != null) {
				return false;
			}
		} else if (!suid.equals(other.suid)) {
			return false;
		}
		return true;
	}

	/**
	 * Билдер события
	 */
	public class EventBuilder {

		/** Событие */
		private Event event;

		/**
		 * Конструктор
		 */
		public EventBuilder(Event aEvent) {
			event = aEvent;
		}

		/**
		 * Возвращает {@link#task}
		 *
		 * @return the {@link#task}
		 */
		public Event toEvent() {
			return event;
		}

		/**
		 * Добавляет идентификатор события
		 *
		 * @param aSuid идентификатор задачи
		 * @return билдер
		 */
		public EventBuilder addSuid(Long aSuid) {
			event.setSuid(aSuid);
			return this;
		}

		public EventBuilder addName(String aName) {
			event.setName(aName);
			return this;
		}

		public EventBuilder addDate(Date aDate) {
			event.setDate(aDate);
			return this;
		}

		public EventBuilder addAuthor(SubordinationElement aAuthor) {
			event.setAuthor(aAuthor);
			return this;
		}

		public EventBuilder addExecutor(SubordinationElement aExecutor) {
			event.setExecutor(aExecutor);
			return this;
		}

		public EventBuilder addTaskSuid(Long aSuid) {
			event.setTaskId(aSuid);
			return this;
		}

		public EventBuilder addEventType(String aEventType) {
			switch (aEventType) {
			case "created":
				event.setEventType(EventType.CREATED);
				break;
			case "rework":
				event.setEventType(EventType.REWORK);
				break;
			case "deleted":
				event.setEventType(EventType.DELETED);
				break;
			case "closed":
				event.setEventType(EventType.CLOSED);
				break;
			case "updated":
				event.setEventType(EventType.UPDATED);
				break;
			case "overdue":
				event.setEventType(EventType.OVERDUE);
				break;
			case "failed":
				event.setEventType(EventType.FAILD);
				break;
			case "done":
				event.setEventType(EventType.DONE);
				break;
			}
			return this;
		}

	}

	public EventBuilder getBuilder() {
		return new EventBuilder(this);
	}

}
