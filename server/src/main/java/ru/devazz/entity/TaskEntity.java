package ru.devazz.entity;

import lombok.Builder;
import lombok.Data;
import ru.devazz.utils.CycleTypeTask;
import ru.devazz.utils.TaskPriority;
import ru.devazz.utils.TaskStatus;
import ru.devazz.utils.TaskType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Сущность "Задача"
 */
@Data
@Builder
@Entity
@Table(name = "tasks", schema = "tasksdb")
public class TaskEntity implements Serializable, IEntity {

	/** Идентификатор сериализации */
	private static final long serialVersionUID = 936798165804131990L;

	/** Идентификатор задачи */
	@Id
	@Column(name = "task_suid")
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
	private Date startDate;

	/** Дата конца */
	@Column(name = "end_date", nullable = false, unique = true, columnDefinition = "datetime")
	private Date endDate;

	/** идентификатор должностного лица (автор задачи) */
	@Column(name = "author_suid")
	private Long authorSuid;

	/** идентификатор должностного лица (исполнитель задачи) */
	@Column(name = "executor_suid", columnDefinition = "bigint")
	private Long executorSuid;

	/** Прикрепляемый документ */
	@Column(name = "document", length = 999999999)
	@Lob
	private byte[] document;

	/** Наименование документа */
	@Column(name = "documenName")
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
