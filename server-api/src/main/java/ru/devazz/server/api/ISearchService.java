package ru.devazz.server.api;

import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.UserModel;

import java.util.List;

/**
 * Общий интерфейс сервиса поиска объектов
 */
public interface ISearchService extends ICommonService {

	/**
	 * Осущесвляет поиск пользователей по ФИО
	 *
	 * @param aName ФИО пользователя
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<UserModel> searchUsersByName(String aName, Long aUserSuid);

	/**
	 * Осущесвляет поиск пользователей по должности
	 *
	 * @param aPosition должность пользователя
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<UserModel> searchUsersByPosition(String aPosition, Long aUserSuid);

	/**
	 * Осуществляет поиск должностей
	 *
	 * @param aName наименование должности
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<SubordinationElementModel> searchSubElsByName(String aName, Long aUserSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aName наименование задачи
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<TaskModel> searchTasksByName(String aName, Long aUserSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aAuthorSubElSuid идентификатор автора
	 * @return список соответствий
	 */
	List<TaskModel> searchTasksByAuthor(Long aAuthorSubElSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aExecutorSubElSuid идентификатор исполнителя
	 * @return список соответствий
	 */
	List<TaskModel> searchTasksByExecutor(Long aExecutorSubElSuid);

}
