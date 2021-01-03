package ru.devazz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.enums.TaskHistoryType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность "История"
 */
@Builder
@Data
@Entity
@Table(name = "task_history", schema = "tasksdb")
@AllArgsConstructor
@NoArgsConstructor
public class TaskHistoryEntity implements IEntity {

	/** Идентификатор записи */
	@Id
	@Column(name = "id")
	private Long suid;

	/** Идентификатор задачи */
	@Column(name = "task_id", columnDefinition = "bigint")
	private Long taskSuid;

	/** Тип задачи */
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private TaskHistoryType historyType;

	/** Заголовок */
	@Column(name = "title", columnDefinition = "varchar")
	private String title;

	/** Основной текст */
	@Column(name = "text", columnDefinition = "text")
	private String text;

	/** Идентификатор элетмента подчиненности */
	@Column(name = "actor_id", columnDefinition = "bigint")
	private Long actorSuid;

	/** Дата */
	@Column(name = "date", nullable = false, columnDefinition = "datetime")
	private LocalDateTime date;

	public String getName() {
		return title;
	}

	public Long getSuid() {
		return suid;
	}

	public void setSuid(Long suid) {
		this.suid = suid;
	}

}
