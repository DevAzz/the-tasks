package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.devazz.entity.*;
import ru.devazz.service.*;
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
	public List<UserEntity> searchUsersByName(String aName, Long aUserSuid) {
		IUserService service = getService(UserEntity.class);
		List<UserEntity> result = new ArrayList<>();
		for (UserEntity entity : service.getAll(aUserSuid)) {
			if ((null != entity.getName()) && (entity.getName().contains(aName))) {
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<UserEntity> searchUsersByPosition(String aPosition, Long aUserSuid) {
		IUserService service = getService(UserEntity.class);
		List<UserEntity> result = new ArrayList<>();
		for (UserEntity entity : service.getAll(aUserSuid)) {
			if ((null != entity.getPosition()) && (entity.getPosition().contains(aPosition))) {
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<SubordinationElementEntity> searchSubElsByName(String aName, Long aUserSuid) {
		ISubordinationElementService service = getService(SubordinationElementEntity.class);
		List<SubordinationElementEntity> result = new ArrayList<>();
		for (SubordinationElementEntity entity : service.getAll(aUserSuid)) {
			result.addAll(findSubEls(entity, aName));
		}
		return result;
	}

	/**
	 * Рекурсивная функция поиска по элементам подчиненности
	 *
	 * @param aEntity элемент подчиненности
	 * @param aName наименование искомого элемента
	 * @return список соответствий
	 */
	private Set<SubordinationElementEntity> findSubEls(SubordinationElementEntity aEntity,
			String aName) {
		Set<SubordinationElementEntity> result = new HashSet<>();
		if (aEntity.getName().contains(aName)) {
			result.add(aEntity);
		}
		for (SubordinationElementEntity entity : aEntity.getSubordinates()) {
			if (entity.getName().contains(aName)) {
				result.add(entity);
			}
			result.addAll(findSubEls(entity, aName));
		}
		return result;
	}

	@Override
	public List<TaskEntity> searchTasksByName(String aName, Long aUserSuid) {
		ITaskService service = getService(TaskEntity.class);
		List<TaskEntity> result = new ArrayList<>();
		for (TaskEntity entity : service.getAll()) {
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
	public List<TaskEntity> searchTasksByAuthor(Long aAuthorSubElSuid) {
		ITaskService taskService = getService(TaskEntity.class);
		return taskService.getAllTasksByAuthor(aAuthorSubElSuid);
	}

	@Override
	public List<TaskEntity> searchTasksByExecutor(Long aExecutorSubElSuid) {
		ITaskService taskService = getService(TaskEntity.class);
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

		if (aTypeEntity.equals(UserEntity.class)) {
			service = (T) userService;
		} else if (aTypeEntity.equals(RoleEntity.class)) {
			service = (T) roleService;
		} else if (aTypeEntity.equals(SubordinationElementEntity.class)) {
			service = (T) subElService;
		} else if (aTypeEntity.equals(TaskEntity.class)) {
			service = (T) tasksService;
		} else if (aTypeEntity.equals(EventEntity.class)) {
			service = (T) eventService;
		}
		return service;
	}

}
