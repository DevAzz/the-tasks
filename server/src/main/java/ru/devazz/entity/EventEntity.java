package ru.devazz.entity;

import lombok.Data;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Сущность "Событие"
 */
@Data
@Entity
@Table(name = "events", schema = "tasksdb")
public class EventEntity implements Serializable, IEntity {

	/** Идентификатор события */
	@Id
	@Column(name = "idevents")
	private Long suid;

	/** Наименование события */
	@Column(name = "name", nullable = false, columnDefinition = "text")
	private String name;

	/** Дата события */
	@Column(name = "date", nullable = false, columnDefinition = "datetime")
	private Date date;

	/** Тип события */
	@Column(name = "event_type", nullable = false, columnDefinition = "varchar")
	private String eventType;

	/** Идентификатор боевого поста */
	@Column(name = "author_suid", nullable = false, columnDefinition = "bigint")
	private Long authorSuid;

	/** Идентификатор боевого поста */
	@Column(name = "executor_suid", nullable = false, columnDefinition = "bigint")
	private Long executorSuid;

	/** Идентификатор задачи */
	@Column(name = "tasks_task_suid", columnDefinition = "bigint")
	private Long taskSuid;
}
