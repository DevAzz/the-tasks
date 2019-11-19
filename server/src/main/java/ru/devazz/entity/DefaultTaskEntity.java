package ru.devazz.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Сущность "Типовая задача"
 */
@Data
@Entity
@Table(name = "default_tasks", schema = "tasksdb")
public class DefaultTaskEntity implements IEntity {

	/** Идентификатор */
	@Id
	@Column(name = "id", columnDefinition = "bigint")
	private long defaultTaskID;

	/** Наименование */
	@Column(name = "name", nullable = false, columnDefinition = "text")
	private String name;

	/** Примечание */
	@Column(name = "note", nullable = false, columnDefinition = "text")
	private String note;

	/** Время начала задачи */
	@Column(name = "start_time", nullable = false, unique = true, columnDefinition = "varchar")
	private String startTime;

	/** Время конца задачи */
	@Column(name = "end_time", nullable = false, unique = true, columnDefinition = "varchar")
	private String endTime;

	/** идентификатор должности (исполнитель задачи) */
	@Column(name = "subordination_id", columnDefinition = "bigint")
	private long subordinationSUID;

	/** Идентификатор автора типовой задачи */
	@Column(name = "author_id", columnDefinition = "bigint")
	private long authorSuid;

	/** Флаг принадлежности задачи к следующему дню */
	@Column(name = "next_day", nullable = false, columnDefinition = "INT", length = 1)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean nextDay;

	/**
	 * Конструктор
	 *
	 * @param defaultTaskID Идентификатор
	 * @param name Наименование
	 * @param note Примечание
	 * @param startTime Время начала задачи
	 * @param endTime Время конца задачи
	 * @param subordinationSUID идентификатор исполнителя задачи
	 */
	public DefaultTaskEntity(long defaultTaskID, String name, String note, String startTime,
			String endTime, long subordinationSUID) {
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
	public DefaultTaskEntity() {
		super();
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

	public Long getSuid() {
		return defaultTaskID;
	}
}
