package ru.devazz.entity;

import lombok.Data;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Сущность Элемент дерева подчиненности
 */
@Data
@Entity
@Table(name = "subordination_element", schema = "tasksdb")
public class SubordinationElementEntity implements Serializable, IEntity {

	/** Идентификатор подразделения */
	@Id
	@Column(name = "idsubel", nullable = false, unique = true, columnDefinition = "bigint")
	private Long suid;

	/** Наименование */
	@Column(name = "name", nullable = false, unique = true, columnDefinition = "varchar")
	private String name;

	/** Подчиненные */
	@ManyToMany(fetch = FetchType.EAGER)
	private List<SubordinationElementEntity> subordinates;

	/** Идентификатор роли */
	@Column(name = "role_suid", columnDefinition = "bigint")
	private Long roleSuid;

	/** Флаг корневого элемента */
	@Column(name = "rootElement")
	private Boolean rootElement;

	/**
	 * Копирование элемента
	 * 
	 * @return копия текущего элемента
	 */
	public SubordinationElementEntity copy() {
		SubordinationElementEntity result = new SubordinationElementEntity();
		result.setName(name);
		result.setRoleSuid(roleSuid);
		result.setRootElement(rootElement);
		result.setSubordinates(subordinates);
		result.setSuid(suid);
		return result;
	}

}
