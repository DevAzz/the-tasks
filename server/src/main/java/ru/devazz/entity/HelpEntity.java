package ru.devazz.entity;

import lombok.Data;
import lombok.ToString;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность "Помощь"
 */
@Data
@ToString
@Entity
@Table(name = "help", schema = "tasksdb")
public class HelpEntity implements Serializable, IEntity {

	/** Идентификатор */
	@Id
	@Column(name = "helpItemID", columnDefinition = "bigint")
	private long helpItemID;

	/** Наименование пункта помощи */
	@Column(name = "helpItemName", nullable = false, columnDefinition = "varchar")
	private String helpItemName;

	/** Текст пункта помощи */
	@Column(name = "helpItemText", nullable = false, columnDefinition = "text")
	private String helpItemText;

	/** Наименование пункта помощи */
	@Column(name = "role", nullable = false, columnDefinition = "bigint")
	private long role;

	/**
	 * Конструктор
	 */
	public HelpEntity() {
		super();
	}

	public HelpEntity(Long helpItemID, String helpItemName, String helpItemText, Long role) {
		super();
		this.helpItemID = helpItemID;
		this.helpItemName = helpItemName;
		this.helpItemText = helpItemText;
		this.role = role;
	}

	public Long getSuid() {
		return helpItemID;
	}

	public String getName() {
		return helpItemName;
	}

}
