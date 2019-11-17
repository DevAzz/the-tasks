package ru.devazz.entities;

import ru.devazz.interfaces.SelectableObject;
import ru.devazz.server.api.model.enums.CycleTypeTask;
import ru.devazz.server.api.model.enums.TaskPriority;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.TaskType;
import ru.devazz.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.Date;

/**
 * Задача
 */
public class Task implements SelectableObject {

	/** Идентификатор задачи */
	private Long suid;

	/** Наименование задачи */
	private String name;

	/** Примечание задачи */
	private String note;

	/** Описание задачи */
	private String description;

	/** Статус задачи */
	private TaskStatus status;

	/** Приоритет задачи */
	private TaskPriority priority;

	/** Процент выполления задачи */
	private Double execPercent;

	/** Дата начала */
	private Date startDateTime;

	/** Дата завершения */
	private Date endDateTime;

	/** Исполнитель */
	private SubordinationElement executor;

	/** Прикрепленный документ */
	private File document;

	/** Наименование документа */
	private String documentName;

	/** Автор задачи */
	private SubordinationElement author;

	/** Тип задачи */
	private TaskType type;

	/** Тип циклического назначения */
	private CycleTypeTask cycleType;

	/** Время цикличного назначения (часы или дата) */
	private String cycleTime;

	/**
	 * Конструктор
	 *
	 * @param name Наименование задачи
	 * @param status Статус задачи
	 */
	public Task(Long aSuid, String name, String note, String aDescription, TaskStatus status,
				TaskPriority aPriority, Double aPercent, Date startDateTime, Date endDateTime) {
		super();
		this.name = name;
		this.note = note;
		this.description = aDescription;
		this.status = status;
		this.suid = aSuid;
		this.priority = aPriority;
		this.execPercent = aPercent;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	/**
	 * Конструктор
	 */
	public Task() {
	}

	/**
	 * Билдер задачи
	 */
	public class TaskBuilder {

		/** Задача */
		private Task task;

		/**
		 * Конструктор
		 */
		public TaskBuilder(Task aTask) {
			task = aTask;
		}

		/**
		 * Возвращает {@link#task}
		 *
		 * @return the {@link#task}
		 */
		public Task toTask() {
			return task;
		}

		/**
		 * Добавляет идентификатор задачи
		 *
		 * @param aSuid идентификатор задачи
		 * @return билдер
		 */
		public TaskBuilder addSuid(Long aSuid) {
			task.setSuid(aSuid);
			return this;
		}

		public TaskBuilder addName(String aName) {
			task.setName(aName);
			return this;
		}

		public TaskBuilder addNote(String aNote) {
			task.setNote(aNote);
			return this;
		}

		public TaskBuilder addDescription(String aDescription) {
			task.setDescription(aDescription);
			return this;
		}

		public TaskBuilder addStatus(TaskStatus aStatus) {
			task.setStatus(aStatus);
			return this;
		}

		public TaskBuilder addTaskPriority(TaskPriority aPriority) {
			task.setPriority(aPriority);
			return this;
		}

		public TaskBuilder addProgress(Double aProgress) {
			task.setExecPercent(aProgress);
			return this;
		}

		public TaskBuilder addStartDateTime(Date aDate) {
			task.setStartDateTime(aDate);
			return this;
		}

		public TaskBuilder addExecutor(SubordinationElement aElement) {
			task.setExecutor(aElement);
			return this;
		}

		public TaskBuilder addAuthor(SubordinationElement aElement) {
			task.setAuthor(aElement);
			return this;
		}

		public TaskBuilder addDocumentName(String aName) {
			task.setDocumentName(aName);
			return this;
		}

		public TaskBuilder addTaskType(TaskType aTaskType) {
			task.setType(aTaskType);
			return this;
		}

		public TaskBuilder addDocument(byte[] aDocumentArr) {
			if (null != aDocumentArr) {
				try (FileOutputStream outputStream = new FileOutputStream(
						Utils.getInstance().getTempDir() + task.getDocumentName())) {
					outputStream.write(aDocumentArr);

					document = new File(Utils.getInstance().getTempDir() + task.getDocumentName());
					task.setDocument(document);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return this;
		}

		public TaskBuilder addDocument(File aDocument) {
			if (null != aDocument) {
				task.setDocument(aDocument);
			}
			return this;
		}

		public TaskBuilder addEndDateTime(Date aDate) {
			task.setEndDateTime(aDate);
			return this;
		}

		public TaskBuilder addCycleType(CycleTypeTask aType) {
			task.setCycleType(aType);
			return this;
		}

		public TaskBuilder addCycleTime(String aTime) {
			task.setCycleTime(aTime);
			return this;
		}

	}

	/**
	 * Конструктор
	 */
	public Task(String aName) {
		super();
		this.name = aName;
	}

	/**
	 * Возвращает {@link #name}
	 *
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает значение {@link #name}
	 *
	 * @param name {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link #status}
	 *
	 * @return {@link #status}
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Устанавливает значение {@link #status}
	 *
	 * @param status {@link #status}
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	/**
	 * Возвращает {@link#note}
	 *
	 * @return the {@link#note}
	 */
	public String getNote() {
		return note;
	}

	/**
	 * Устанавливает значение полю {@link#note}
	 *
	 * @param {@link#note}
	 */
	public void setNote(String note) {
		this.note = note;
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
	 * Возвращает {@link#description}
	 *
	 * @return the {@link#description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Устанавливает значение полю {@link#description}
	 *
	 * @param {@link#description}
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Возвращает {@link#priority}
	 *
	 * @return the {@link#priority}
	 */
	public TaskPriority getPriority() {
		return priority;
	}

	/**
	 * Устанавливает значение полю {@link#priority}
	 *
	 * @param {@link#priority}
	 */
	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	/**
	 * Возвращает {@link#execPercent}
	 *
	 * @return the {@link#execPercent}
	 */
	public Double getExecPercent() {
		return execPercent;
	}

	/**
	 * Устанавливает значение полю {@link#execPercent}
	 *
	 * @param {@link#execPercent}
	 */
	public void setExecPercent(Double execPercent) {
		this.execPercent = execPercent;
	}

	/**
	 * Возвращает {@link#startDateTime}
	 *
	 * @return the {@link#startDateTime}
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * Устанавливает значение полю {@link#startDateTime}
	 *
	 * @param startDateTime значение поля
	 */
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * Возвращает {@link#endDateTime}
	 *
	 * @return the {@link#endDateTime}
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * Устанавливает значение полю {@link#endDateTime}
	 *
	 * @param endDateTime значение поля
	 */
	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
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
	 * Возвращает дату начала
	 *
	 * @return дата начала
	 * @throws ParseException в случае ошибки парсинга
	 */
	public Date getStartedDate() throws ParseException {
		return startDateTime;
	}

	/**
	 * Возвращает дату начала
	 *
	 * @return дата начала
	 * @throws ParseException в случае ошибки парсинга
	 */
	public Date getEndDate() throws ParseException {
		return endDateTime;
	}

	/**
	 * Возвращает {@link#document}
	 *
	 * @return the {@link#document}
	 */
	public File getDocument() {
		return document;
	}

	/**
	 * Устанавливает значение полю {@link#document}
	 *
	 * @param document значение поля
	 */
	public void setDocument(File document) {
		this.document = document;
	}

	/**
	 * Возвращает {@link#documentName}
	 *
	 * @return the {@link#documentName}
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Устанавливает значение полю {@link#documentName}
	 *
	 * @param documentName значение поля
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
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
	 * Возвращает {@link#type}
	 *
	 * @return the {@link#type}
	 */
	public TaskType getType() {
		return type;
	}

	/**
	 * Устанавливает значение полю {@link#type}
	 *
	 * @param type значение поля
	 */
	public void setType(TaskType type) {
		this.type = type;
	}

	/**
	 * Возвращает {@link#cycleType}
	 *
	 * @return the {@link#cycleType}
	 */
	public CycleTypeTask getCycleType() {
		return cycleType;
	}

	/**
	 * Устанавливает значение полю {@link#cycleType}
	 *
	 * @param cycleType значение поля
	 */
	public void setCycleType(CycleTypeTask cycleType) {
		this.cycleType = cycleType;
	}

	/**
	 * Возвращает {@link#cycleTime}
	 *
	 * @return the {@link#cycleTime}
	 */
	public String getCycleTime() {
		return cycleTime;
	}

	/**
	 * Устанавливает значение полю {@link#cycleTime}
	 *
	 * @param cycleTime значение поля
	 */
	public void setCycleTime(String cycleTime) {
		this.cycleTime = cycleTime;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Task [suid=" + suid + ", name=" + name + "]";
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((author == null) ? 0 : author.hashCode());
		result = (prime * result) + ((description == null) ? 0 : description.hashCode());
		result = (prime * result) + ((document == null) ? 0 : document.hashCode());
		result = (prime * result) + ((documentName == null) ? 0 : documentName.hashCode());
		result = (prime * result) + ((endDateTime == null) ? 0 : endDateTime.hashCode());
		result = (prime * result) + ((execPercent == null) ? 0 : execPercent.hashCode());
		result = (prime * result) + ((executor == null) ? 0 : executor.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + ((note == null) ? 0 : note.hashCode());
		result = (prime * result) + ((priority == null) ? 0 : priority.hashCode());
		result = (prime * result) + ((startDateTime == null) ? 0 : startDateTime.hashCode());
		result = (prime * result) + ((status == null) ? 0 : status.hashCode());
		result = (prime * result) + ((suid == null) ? 0 : suid.hashCode());
		result = (prime * result) + ((type == null) ? 0 : type.hashCode());
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
		Task other = (Task) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (document == null) {
			if (other.document != null) {
				return false;
			}
		} else if (!document.equals(other.document)) {
			return false;
		}
		if (endDateTime == null) {
			if (other.endDateTime != null) {
				return false;
			}
		} else if (!endDateTime.equals(other.endDateTime)) {
			return false;
		}
		if (executor == null) {
			if (other.executor != null) {
				return false;
			}
		} else if (!executor.equals(other.executor)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (note == null) {
			if (other.note != null) {
				return false;
			}
		} else if (!note.equals(other.note)) {
			return false;
		}
		if (priority != other.priority) {
			return false;
		}
		if (startDateTime == null) {
			if (other.startDateTime != null) {
				return false;
			}
		} else if (!startDateTime.equals(other.startDateTime)) {
			return false;
		}
		if (status != other.status) {
			return false;
		}
		if (suid == null) {
			if (other.suid != null) {
				return false;
			}
		} else if (!suid.equals(other.suid)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	/**
	 * Возвращает построитель задачи
	 *
	 * @return построитель задачи
	 */
	public TaskBuilder getBuilder() {
		return new TaskBuilder(this);
	}

	/**
	 * Создает на основе оригинальной задачи ее копию
	 *
	 * @return копия задачи
	 */
	public Task copy() {
		// @formatter:off
		Task copyTask = new Task().getBuilder()
				.addSuid(suid)
				.addName(name)
				.addNote(note)
				.addDescription(description)
				.addStatus(status)
				.addTaskPriority(priority)
				.addProgress(execPercent)
				.addStartDateTime(startDateTime)
				.addEndDateTime(endDateTime)
				.addExecutor(executor)
				.addAuthor(author)
				.addDocumentName(documentName)
				.addTaskType(type)
				.addCycleType(cycleType)
				.addCycleTime(cycleTime)
				.addDocument(document).toTask();
		// @formatter:on
		return copyTask;
	}

}
