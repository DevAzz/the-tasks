package ru.devazz.service.impl;

import ru.devazz.entity.HelpEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.repository.HelpRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.IHelpService;
import ru.devazz.service.IRoleService;
import ru.devazz.service.IUserService;

import java.util.List;

/**
 * Сервис работы с помощью
 */
public class HelpServiceBean extends AbstractEntityService<HelpEntity> implements IHelpService {

	/** Сервис задач */
	public IUserService userService;

	/** Сервис задач */
	public IRoleService roleService;

	@Override
	public List<HelpEntity> getAll(Long aUserSuid) {
		return repository.getAll();
	}

	protected HelpRepository createRepository() {
		return new HelpRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return null;
	}


}
