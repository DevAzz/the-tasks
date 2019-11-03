package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.devazz.entity.RoleEntity;
import ru.devazz.entity.UserEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.event.RoleEvent;
import ru.devazz.repository.AbstractRepository;
import ru.devazz.repository.RoleRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.IRoleService;
import ru.devazz.service.IUserService;
import ru.devazz.utils.UserRoles;

/**
 * Реализация сервиса взаимодействия с ролями пользователей
 */
@Service
@AllArgsConstructor
public class RoleService extends AbstractEntityService<RoleEntity>
		implements IRoleService {

	/** Сервис работы с пользователями */
	private IUserService userService;

	@Override
	protected AbstractRepository<RoleEntity> createRepository() {
		return new RoleRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return RoleEvent.class;
	}

	@Override
	public RoleEntity getRoleByUserRolesEnum(UserRoles aUserRole, Long aUserSuid) {
		RoleEntity result = null;
		for (RoleEntity role : getAll(aUserSuid)) {
			if (aUserRole.getName().equals(role.getName())) {
				result = role;
			}
		}
		return result;
	}

	@Override
	public boolean checkUserPrivilege(UserRoles aRole, Long aUserSuid) {
		boolean result = false;
		UserEntity user = userService.get(aUserSuid);
		if (null != user) {
			Long roleSuid = user.getIdrole();
			UserRoles userRole = UserRoles.getUserRoleByName(get(roleSuid).getName());
			result = (userRole.getPositionIndex() > aRole.getPositionIndex());
		}
		return result;
	}

}
