package ru.devazz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.enums.CycleTypeTask;
import ru.devazz.server.api.model.enums.TaskPriority;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.TaskType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность "Задача"
 */
@Data
@Builder
@Entity
@Table(name = "tasks", schema = "tasksdb")
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity implements IEntity {

	/** Идентификатор задачи */
	@Id
	@Column(name = "id")
	private Long taskSuid;

	/** Наименование задачи */
	@Column(name = "name", nullable = false, unique = true, columnDefinition = "text")
	private String name;

	/** Примечание задачи */
	@Column(name = "note", nullable = false, unique = true, columnDefinition = "text")
	private String note;

	/** Описание задачи */
	@Column(name = "description", nullable = false, unique = true, columnDefinition = "text")
	private String description;

	/** Статус задачи */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private TaskStatus status;

	/** Приоритет задачи */
	@Enumerated(EnumType.STRING)
	@Column(name = "priority")
	private TaskPriority priority;

	/** Дата начала */
	@Column(name = "start_date", nullable = false, unique = true, columnDefinition = "datetime")
	private LocalDateTime startDate;

	/** Дата конца */
	@Column(name = "end_date", nullable = false, unique = true, columnDefinition = "datetime")
	private LocalDateTime endDate;

	/** идентификатор должностного лица (автор задачи) */
	@Column(name = "author_id")
	private Long authorSuid;

	/** идентификатор должностного лица (исполнитель задачи) */
	@Column(name = "executor_id", columnDefinition = "bigint")
	private Long executorSuid;

	/** Прикрепляемый документ */
	@Column(name = "document", length = 999999999)
	@Lob
	private byte[] document;

	/** Наименование документа */
	@Column(name = "document_name")
	private String documentName;

	/** Тип задачи */
	@Enumerated(EnumType.STRING)
	@Column(name = "task_type")
	private TaskType taskType;

	/** Тип циклического назначения */
	@Enumerated(EnumType.STRING)
	@Column(name = "cycle_type")
	private CycleTypeTask cycleType;

	/** Время цикличного назначения (часы или дата) */
	@Column(name = "cycle_time", columnDefinition = "varchar")
	private String cycleTime;

	public Long getSuid() {
		return taskSuid;
	}
}
