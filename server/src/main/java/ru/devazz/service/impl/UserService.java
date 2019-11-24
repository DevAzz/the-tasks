package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.UserEntity;
import ru.devazz.repository.UserRepository;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.UserEvent;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.server.api.model.enums.JmsQueueName;
import ru.devazz.server.api.model.enums.SystemEventType;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.UserEntityConverter;
import ru.devazz.utils.Utils;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис работы с пользователями
 */
@Service
public class UserService extends AbstractEntityService<UserModel, UserEntity>
		implements IUserService {

	private JmsTemplate broker;

	private UserRepository repository;

	private UserEntityConverter converter;

	public UserService(JmsTemplate broker,
					   UserRepository repository,
					   UserEntityConverter converter) {
		super(repository, converter, broker);
		this.broker = broker;
		this.repository = repository;
		this.converter = converter;
	}

	@Override
	public UserModel checkUser(String aUsername, String aPassword) throws Exception {
		UserEntity result = null;
		List<UserEntity> users = null;
		try {
			result = repository.findByUserName(aUsername, Utils.getInstance().sha(aPassword));
			if (null != result) {

				if (result.getOnline()) {
					throw new Exception(
							"Пользователь с таким логином уже авторизирован");
				}

				users = repository.getUserBySubElSuid(result.getPositionSuid());
				for (UserEntity entity : users) {
					if ((!entity.equals(result)) && entity.getOnline()) {
						throw new Exception(
								"Пользователь на данной должности уже авторизирован");
					}
				}

				result.setOnline(true);
				repository.update(result);

				broker.convertAndSend(getQueueName(), getEventByEntity(
						SystemEventType.USER_ONLINE, converter.entityToModel(result)));
			}
		} catch (Exception e) {
			// В случае, если не было найдено пользователей
			// Не обрабатываем
			if (!(e instanceof NoResultException)) {
				throw e;
			}

		}
		return converter.entityToModel(result);
	}

	@Override
	public List<UserModel> getServiceUserList() {
		return repository.getServiceUserList().stream().map(converter::entityToModel).collect(
				Collectors.toList());
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return UserEvent.class;
	}

	@Override
	protected String getQueueName() {
		return JmsQueueName.USERS.getName();
	}

	@Override
	public UserModel getUserBySubElSuid(Long aSuid) {
		UserEntity entity = null;
		List<UserEntity> users = null;
		try {
			users = repository.getUserBySubElSuid(aSuid);
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
		return converter.entityToModel(entity);
	}

	@Override
	public byte[] getUserImage(Long aUserSuid) {
		return repository.getUserImage(aUserSuid);
	}

	@Override
	public void disableUser(Long aUserSuid) {
		UserModel user = get(aUserSuid);
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
		return repository.getUserImageBySubElSuid(aSubElSuid);
	}

	@Override
	public Long getUserSuidBySubElSuid(Long aSubElSuid) {
		return repository.getUserSuidBySubElSuid(aSubElSuid);
	}

}
