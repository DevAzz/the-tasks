package ru.devazz.entity;

import lombok.Data;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность "Событие"
 */
@Data
@Entity
@Table(name = "events", schema = "tasksdb")
public class EventEntity implements IEntity {

	/** Идентификатор события */
	@Id
	@Column(name = "id")
	private Long suid;

	/** Наименование события */
	@Column(name = "name", nullable = false, columnDefinition = "text")
	private String name;

	/** Дата события */
	@Column(name = "date", nullable = false, columnDefinition = "datetime")
	private LocalDateTime date;

	/** Тип события */
	@Column(name = "event_type", nullable = false, columnDefinition = "varchar")
	private String eventType;

	/** Идентификатор боевого поста */
	@Column(name = "author_id", nullable = false, columnDefinition = "bigint")
	private Long authorSuid;

	/** Идентификатор боевого поста */
	@Column(name = "executor_id", nullable = false, columnDefinition = "bigint")
	private Long executorSuid;

	/** Идентификатор задачи */
	@Column(name = "task_suid", columnDefinition = "bigint")
	private Long taskSuid;
}
