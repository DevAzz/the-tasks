package ru.devazz.server.api;

import ru.devazz.server.api.model.RoleModel;
import ru.devazz.server.api.model.enums.UserRoles;

/**
 * Общий интерфейс работы с ролями
 */
public interface IRoleService extends IEntityService<RoleModel> {

	/**
	 * Возвращает роль по экземпляру перечисления пользовательских ролей
	 *
	 * @param aUserRole экземпляр перечисления пользовательских ролей
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return роль
	 */
	RoleModel getRoleByUserRolesEnum(UserRoles aUserRole, Long aUserSuid);

	/**
	 * Проверяет привилегии текущего пользователя на соответствие заданной роли
	 *
	 * @param aRole роль
	 * @param aUserSuid идентификатор пользователя. (Временно)
	 * @return {@code true} - если текущий пользователь соответствует роли
	 */
	boolean checkUserPrivilege(UserRoles aRole, Long aUserSuid);

}
