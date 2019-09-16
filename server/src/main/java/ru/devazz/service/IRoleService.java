package ru.devazz.service;

import ru.devazz.entity.RoleEntity;
import ru.devazz.utils.UserRoles;

/**
 * Общий интерфейс работы с ролями
 */
public interface IRoleService extends IEntityService<RoleEntity> {

	/**
	 * Возвращает роль по экземпляру перечисления пользовательских ролей
	 *
	 * @param aUserRole экземпляр перечисления пользовательских ролей
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return роль
	 */
	RoleEntity getRoleByUserRolesEnum(UserRoles aUserRole, Long aUserSuid);

	/**
	 * Проверяет привилегии текущего пользователя на соответствие заданной роли
	 *
	 * @param aRole роль
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return {@code true} - если текущий пользователь соответствует роли
	 */
	boolean checkUserPrivilege(UserRoles aRole, Long aUserSuid);

}
