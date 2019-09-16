package ru.devazz.service;

import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.entity.TaskEntity;
import ru.devazz.entity.UserEntity;

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
	List<UserEntity> searchUsersByName(String aName, Long aUserSuid);

	/**
	 * Осущесвляет поиск пользователей по должности
	 *
	 * @param aPosition должность пользователя
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<UserEntity> searchUsersByPosition(String aPosition, Long aUserSuid);

	/**
	 * Осуществляет поиск боевых постов
	 *
	 * @param aName наименование боевого поста
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<SubordinationElementEntity> searchSubElsByName(String aName, Long aUserSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aName наименование задачи
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return список соответствий
	 */
	List<TaskEntity> searchTasksByName(String aName, Long aUserSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aAuthorSubElSuid идентификатор автора
	 * @return список соответствий
	 */
	List<TaskEntity> searchTasksByAuthor(Long aAuthorSubElSuid);

	/**
	 * Осуществляет поиск задач по их наименованию
	 *
	 * @param aExecutorSubElSuid идентификатор исполнителя
	 * @return список соответствий
	 */
	List<TaskEntity> searchTasksByExecutor(Long aExecutorSubElSuid);

}
