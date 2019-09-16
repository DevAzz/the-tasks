package ru.devazz.service.impl;

import ru.devazz.entity.UserEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.event.UserEvent;
import ru.devazz.repository.UserRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.ITaskService;
import ru.devazz.service.IUserService;
import ru.devazz.utils.Utils;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * Сервис работы с пользователями
 */
public class UserServiceBean extends AbstractEntityService<UserEntity>
		implements IUserService {

	/** Сервис задач */
	public ITaskService taskService;

	@Override
	public UserEntity checkUser(String aUsername, String aPassword) throws Exception {
		UserEntity result = null;
		List<UserEntity> users = null;
		try {
			result = getRepository().findByUserName(aUsername, Utils.getInstance().sha(aPassword));
			if (null != result) {

				if (result.getOnline()) {
					throw new Exception(
							"Пользователь с таким логином уже авторизирован");
				}

				users = getRepository().getUserBySubElSuid(result.getPositionSuid());
				for (UserEntity entity : users) {
					if ((!entity.equals(result)) && entity.getOnline()) {
						throw new Exception(
								"Пользователь на данном боевом посте уже авторизирован");
					}
				}

				result.setOnline(true);
				getRepository().update(result);

				//TODO публикация сообщения
//				publisher.sendEvent(getEventByEntity(SystemEventType.USER_ONLINE, result));
			}
		} catch (Exception e) {
			// В случае, если не было найдено пользователей
			// Не обрабатываем
			if (!(e instanceof NoResultException)) {
				throw e;
			}

		}
		return result;
	}

	@Override
	public List<UserEntity> getServiceUserList() {
		return ((UserRepository) repository).getServiceUserList();
	}

	@Override
	protected UserRepository createRepository() {
		return new UserRepository();
	}

	@Override
	protected UserRepository getRepository() {
		return (UserRepository) repository;
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return UserEvent.class;
	}

	@Override
	public UserEntity getUserBySubElSuid(Long aSuid) {
		UserEntity entity = null;
		List<UserEntity> users = null;
		try {
			users = ((UserRepository) repository).getUserBySubElSuid(aSuid);
			for (UserEntity user : users) {
				if (user.getOnline()) {
					entity = user;
				}
			}

			if ((null == entity) && (!users.isEmpty())) {
				entity = users.get(0);
			}
		} catch (NoResultException e) {
			// TODO Логирование
			// Не обрабатываем
		}
		return entity;
	}

	@Override
	public byte[] getUserImage(Long aUserSuid) {
		return getRepository().getUserImage(aUserSuid);
	}

	@Override
	public void disableUser(Long aUserSuid) {
		UserEntity user = get(aUserSuid);
		if (null != user) {
			user.setOnline(false);
			try {
				update(user, true);
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}

	}

	@Override
	public byte[] getUserImageBySubElSuid(Long aSubElSuid) {
		return getRepository().getUserImageBySubElSuid(aSubElSuid);
	}

	@Override
	public Long getUserSuidBySubElSuid(Long aSubElSuid) {
		return getRepository().getUserSuidBySubElSuid(aSubElSuid);
	}

}
