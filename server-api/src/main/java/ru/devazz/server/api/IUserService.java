package ru.devazz.server.api;

import ru.devazz.server.api.model.UserModel;

import java.util.List;

/**
 * Общий интерфейс сервиса работы с пользователями системы
 */
public interface IUserService extends IEntityService<UserModel> {

	/**
	 *
	 * Проверяет наличие пользователя по логину и паролю
	 *
	 * @param aUsername логин
	 * @param aPassword пароль
	 * @return пользователь или {@code null} - если пользователь не был найден
	 */
	UserModel checkUser(String aUsername, String aPassword) throws Exception;

	/**
	 * Возвращает пользователя по идентификатору
	 *
	 * @param aSuid идентификатор боевого поста
	 * @return сущность польщователя
	 */
	UserModel getUserBySubElSuid(Long aSuid);

	/**
	 * Возвращяет полный список пользователей
	 *
	 * @return список сущьностей пользователей
	 */
	List<UserModel> getServiceUserList();

	/**
	 * Возвращает пользовательскую фотографию
	 *
	 * @param aUserSuid идентификатор пользователя
	 * @return пользовательская фотография
	 */
	byte[] getUserImage(Long aUserSuid);

	/**
	 * Отключает пользователя от системы
	 *
	 * @param aUserSuid идентификатор пользователя
	 */
	void disableUser(Long aUserSuid);

	/**
	 * Возвращает пользовательскую фотографию
	 *
	 * @param aSubElSuid идентификатор пользователя
	 * @return пользовательская фотография
	 */
	byte[] getUserImageBySubElSuid(Long aSubElSuid);

	/**
	 * Возвращает идентификатор пользователя в сети по идентификатору элемента
	 * подчиненности
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 * @return идентификатор пользователя
	 */
	Long getUserSuidBySubElSuid(Long aSubElSuid);

}
