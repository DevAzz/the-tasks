package ru.devazz.entity;

import lombok.Data;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность "Роль"
 */
@Data
@Entity
@Table(name = "role", schema = "tasksdb")
public class RoleEntity implements IEntity {

	/** Идентификатор роли */
	@Id
	@Column(name = "id")
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
