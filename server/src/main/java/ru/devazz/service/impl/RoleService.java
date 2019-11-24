package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.RoleEntity;
import ru.devazz.entity.UserEntity;
import ru.devazz.repository.AbstractRepository;
import ru.devazz.repository.RoleRepository;
import ru.devazz.server.api.IRoleService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.RoleEvent;
import ru.devazz.server.api.model.RoleModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.server.api.model.enums.UserRoles;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.IEntityConverter;
import ru.devazz.service.impl.converters.RoleEntityConverter;

/**
 * Реализация сервиса взаимодействия с ролями пользователей
 */
@Service
public class RoleService extends AbstractEntityService<RoleModel, RoleEntity>
		implements IRoleService {

	/** Сервис работы с пользователями */
	private IUserService userService;

	private JmsTemplate broker;

	private RoleEntityConverter converter;

	private RoleRepository repository;

	public RoleService(IUserService userService, JmsTemplate broker, RoleEntityConverter converter,
					   RoleRepository repository) {
		super(repository, converter, broker);
		this.userService = userService;
		this.broker = broker;
		this.converter = converter;
		this.repository = repository;
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return RoleEvent.class;
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	@Override
	public RoleModel getRoleByUserRolesEnum(UserRoles aUserRole, Long aUserSuid) {
		RoleModel result = null;
		for (RoleModel role : getAll(aUserSuid)) {
			if (aUserRole.getName().equals(role.getName())) {
				result = role;
			}
		}
		return result;
	}

	@Override
	public boolean checkUserPrivilege(UserRoles aRole, Long aUserSuid) {
		return true;
	}

}
