package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.devazz.entity.*;
import ru.devazz.server.api.*;
import ru.devazz.server.api.model.*;
import ru.devazz.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Реалиазция сервиса поиска сущностей
 */
@Service
@AllArgsConstructor
public class SearchService implements ISearchService {

	/** Сервис пользователей */
	private IUserService userService;

	/** Сервис ролей */
	private IRoleService roleService;

	/** Сервис дерева подчиненности */
	private ISubordinationElementService subElService;

	/** Сервис работы с задачами */
	private ITaskService tasksService;

	/** Сервис событий */
	private IEventService eventService;

	@Override
	public List<UserModel> searchUsersByName(String aName, Long aUserSuid) {
		IUserService service = getService(UserModel.class);
		List<UserModel> result = new ArrayList<>();
		for (UserModel entity : service.getAll(aUserSuid)) {
			if ((null != entity.getName()) && (entity.getName().contains(aName))) {
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<UserModel> searchUsersByPosition(String aPosition, Long aUserSuid) {
		IUserService service = getService(UserModel.class);
		List<UserModel> result = new ArrayList<>();
		for (UserModel entity : service.getAll(aUserSuid)) {
			if ((null != entity.getPosition()) && (entity.getPosition().contains(aPosition))) {
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<SubordinationElementModel> searchSubElsByName(String aName, Long aUserSuid) {
		ISubordinationElementService service = getService(SubordinationElementModel.class);
		List<SubordinationElementModel> result = new ArrayList<>();
		for (SubordinationElementModel entity : service.getAll(aUserSuid)) {
			result.addAll(findSubEls(entity, aName));
		}
		return result;
	}

	/**
	 * Рекурсивная функция поиска по элементам подчиненности
	 *
	 * @param aModel элемент подчиненности
	 * @param aName наименование искомого элемента
	 * @return список соответствий
	 */
	private Set<SubordinationElementModel> findSubEls(SubordinationElementModel aModel,
			String aName) {
		Set<SubordinationElementModel> result = new HashSet<>();
		if (aModel.getName().contains(aName)) {
			result.add(aModel);
		}
		for (SubordinationElementModel entity : aModel.getSubordinates()) {
			if (entity.getName().contains(aName)) {
				result.add(entity);
			}
			result.addAll(findSubEls(entity, aName));
		}
		return result;
	}

	@Override
	public List<TaskModel> searchTasksByName(String aName, Long aUserSuid) {
		ITaskService service = getService(TaskModel.class);
		List<TaskModel> result = new ArrayList<>();
		for (TaskModel entity : service.getAll()) {
			String taskName = Utils.getInstance().fromBase64(entity.getName());
			try {
				String taskNameUTF8 = new String(taskName.getBytes(), StandardCharsets.UTF_8);
				String taskNameCp1251 = new String(taskName.getBytes(), "windows-1251");
				if (taskNameUTF8.contains(aName) || taskNameCp1251.contains(aName)) {
					result.add(entity);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public List<TaskModel> searchTasksByAuthor(Long aAuthorSubElSuid) {
		ITaskService taskService = getService(TaskModel.class);
		return taskService.getAllTasksByAuthor(aAuthorSubElSuid);
	}

	@Override
	public List<TaskModel> searchTasksByExecutor(Long aExecutorSubElSuid) {
		ITaskService taskService = getService(TaskModel.class);
		return taskService.getAllTasksByExecutor(aExecutorSubElSuid);
	}

	/**
	 * Возвращает сервис по типу сущности
	 *
	 * @param aTypeEntity тип сущности
	 * @return сервис работы с сущностями
	 */
	@SuppressWarnings("unchecked")
	private <T extends IEntityService<? extends IEntity>> T getService(
			Class<? extends IEntity> aTypeEntity) {
		T service = null;

		if (aTypeEntity.equals(UserModel.class)) {
			service = (T) userService;
		} else if (aTypeEntity.equals(RoleModel.class)) {
			service = (T) roleService;
		} else if (aTypeEntity.equals(SubordinationElementModel.class)) {
			service = (T) subElService;
		} else if (aTypeEntity.equals(TaskModel.class)) {
			service = (T) tasksService;
		} else if (aTypeEntity.equals(EventModel.class)) {
			service = (T) eventService;
		}
		return service;
	}

}
