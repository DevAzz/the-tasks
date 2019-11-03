package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.entity.UserEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.event.SubElEvent;
import ru.devazz.repository.SubordinationElementRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.ISubordinationElementService;
import ru.devazz.service.IUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса работы с элементами дерева подчиненности
 */
@Service
@AllArgsConstructor
public class SubordinationElementService
		extends AbstractEntityService<SubordinationElementEntity>
		implements ISubordinationElementService {

	/** Сервис пользователей */
	private IUserService userService;

	@Override
	public List<SubordinationElementEntity> getAll(Long aUserSuid) {
		List<SubordinationElementEntity> result = new ArrayList<>();
		SubordinationElementEntity root = null;
		UserEntity currentUser = userService.get(aUserSuid);
		if (null != currentUser) {
			// Устанавливаем корневой элемент в коллекции
			List<SubordinationElementEntity> subdivisions = getSubElByUserRole(
					currentUser.getIdrole());
			for (SubordinationElementEntity entity : subdivisions) {
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
	protected SubordinationElementRepository createRepository() {
		return new SubordinationElementRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return SubElEvent.class;
	}

	@Override
	public List<SubordinationElementEntity> getSubElByUserRole(Long aRoleSuid) {
		return getRepository().getSubElsByUserRole(aRoleSuid);
	}

	@Override
	protected SubordinationElementRepository getRepository() {
		return (SubordinationElementRepository) repository;
	}

	@Override
	public List<SubordinationElementEntity> getTotalSubElList() {
		List<SubordinationElementEntity> result = new ArrayList<>();
		for (SubordinationElementEntity entity : repository.getAll()) {
			SubordinationElementEntity copy = entity.copy();
			copy.setSubordinates(new ArrayList<>());
			result.add(copy);
		}
		return result;
	}

}
