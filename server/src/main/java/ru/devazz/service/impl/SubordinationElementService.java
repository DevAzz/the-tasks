package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.repository.SubordinationElementRepository;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.SubElEvent;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.SubElEntityConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса работы с элементами дерева подчиненности
 */
@Service
public class SubordinationElementService
		extends AbstractEntityService<SubordinationElementModel, SubordinationElementEntity>
		implements ISubordinationElementService {

	/** Сервис пользователей */
	private IUserService userService;

	private JmsTemplate broker;

	private SubordinationElementRepository repository;

	private SubElEntityConverter converter;

	public SubordinationElementService(JmsTemplate broker,
									   IUserService userService,
									   SubordinationElementRepository repository,
									   SubElEntityConverter converter) {
		super(repository, converter, broker);
		this.userService = userService;
		this.broker = broker;
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	public List<SubordinationElementModel> getAll(Long aUserSuid) {
		List<SubordinationElementModel> result = new ArrayList<>();
		SubordinationElementModel root = null;
		UserModel currentUser = userService.get(aUserSuid);
		if (null != currentUser) {
			// Устанавливаем корневой элемент в коллекции
			List<SubordinationElementModel> subdivisions = getSubElByUserRole(
					currentUser.getIdRole());
			for (SubordinationElementModel entity : subdivisions) {
				if (entity.getSuid().equals(currentUser.getPositionSuid())) {
					root = entity;
				}
			}
			if (null != root) {
				root.setRootElement(true);
				result.add(root);
			}
		}
		return result;
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return SubElEvent.class;
	}

	@Override
	public List<SubordinationElementModel> getSubElByUserRole(Long aRoleSuid) {
		return repository.getSubElsByUserRole(aRoleSuid).stream().map(converter::entityToModel).collect(
				Collectors.toList());
	}

	@Override
	public List<SubordinationElementModel> getTotalSubElList() {
		List<SubordinationElementModel> result = new ArrayList<>();
		for (SubordinationElementEntity entity : repository.getAll()) {
			SubordinationElementModel copy = converter.entityToModel(entity);
			copy.setSubordinates(new ArrayList<>());
			result.add(copy);
		}
		return result;
	}

}
