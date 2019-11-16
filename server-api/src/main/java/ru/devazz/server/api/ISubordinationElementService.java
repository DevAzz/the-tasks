package ru.devazz.server.api;

import ru.devazz.server.api.model.SubordinationElementModel;

import java.util.List;

/**
 * Общий интерфейс сервиса дерева подчиненности
 */
public interface ISubordinationElementService extends IEntityService<SubordinationElementModel> {

	/**
	 * Ищет элементы дерева подчиненности по роли
	 *
	 * @param aRoleSuid роль
	 * @return список элементов, подходящих по роли
	 */
	public List<SubordinationElementModel> getSubElByUserRole(Long aRoleSuid);

	public List<SubordinationElementModel> getTotalSubElList();

}
