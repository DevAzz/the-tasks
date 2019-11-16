package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.HelpEntity;
import ru.devazz.repository.HelpRepository;
import ru.devazz.server.api.IHelpService;
import ru.devazz.server.api.IRoleService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.model.HelpModel;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.HelpEntityConverter;

/**
 * Сервис работы с помощью
 */
@Service
public class HelpService extends AbstractEntityService<HelpModel, HelpEntity> implements
		IHelpService {

	/** Сервис задач */
	private IUserService userService;

	/** Сервис задач */
	private IRoleService roleService;

	/** Брокер сообщений */
	private JmsTemplate broker;

	private HelpRepository repository;

	private HelpEntityConverter converter;

	public HelpService(HelpRepository repository, HelpEntityConverter converter,
					   JmsTemplate broker, IUserService userService,
					   IRoleService roleService) {
		super(repository, converter, broker);
		this.userService = userService;
		this.roleService = roleService;
		this.broker = broker;
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return null;
	}


}
