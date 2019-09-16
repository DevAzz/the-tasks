package ru.devazz.service;

import ru.devazz.entity.SubordinationElementEntity;

import java.util.List;

/**
 * Общий интерфейс сервиса дерева подчиненности
 */
public interface ISubordinationElementService extends IEntityService<SubordinationElementEntity> {

	/**
	 * Ищет элементы дерева подчиненности по роли
	 *
	 * @param aRoleSuid роль
	 * @return список элементов, подходящих по роли
	 */
	public List<SubordinationElementEntity> getSubElByUserRole(Long aRoleSuid);

	public List<SubordinationElementEntity> getTotalSubElList();

}
