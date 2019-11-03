package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
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
@Service
@AllArgsConstructor
public class HelpService extends AbstractEntityService<HelpEntity> implements IHelpService {

	/** Сервис задач */
	public IUserService userService;

	/** Сервис задач */
	public IRoleService roleService;

	private JmsTemplate broker;

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

	@Override
	protected JmsTemplate getBroker() {
		return broker;
	}


}
