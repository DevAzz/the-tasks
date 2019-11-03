package ru.devazz.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность "Роль"
 */
@Data
@Entity
@Table(name = "role", schema = "tasksdb")
public class RoleEntity implements Serializable, IEntity {

	/** Идентификатор роли */
	@Id
	@Column(name = "idrole")
	private Long idRole;

	/** Наименование задачи */
	@Column(name = "name_role", nullable = false, unique = true, columnDefinition = "varchar")
	private String name;

	public String getName() {
		return name;
	}

	public Long getSuid() {
		return getIdRole();
	}

}
