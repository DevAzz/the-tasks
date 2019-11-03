package ru.devazz.service;

import org.springframework.jms.core.JmsTemplate;
import ru.devazz.entity.IEntity;
import ru.devazz.event.ObjectEvent;
import ru.devazz.repository.AbstractRepository;
import ru.devazz.utils.JmsQueueName;
import ru.devazz.utils.SystemEventType;

import java.util.List;

/**
 * Абстрактный сервис
 */
public abstract class AbstractEntityService<T extends IEntity> implements IEntityService<T> {

	/** Репозиторий элементов дерева подчиненности */
	protected AbstractRepository<T> repository;

	/**
	 * Конструктор
	 */
	public AbstractEntityService() {
		super();
		repository = createRepository();
	}

	@Override
	public T add(T aEntity, Boolean aNeedPublishEvent) {
		T createdEntity = repository.add(aEntity);
		if (aNeedPublishEvent) {
			getBroker().convertAndSend(JmsQueueName.DEFAULT.getName(),
									   (getEventByEntity(SystemEventType.CREATE, createdEntity)));
		}
		return createdEntity;
	}

	@Override
	public void delete(Long aSuid, Boolean aNeedPublishEvent) {
		T deletedEntity = repository.get(aSuid);
		repository.delete(aSuid);
		if (aNeedPublishEvent) {
			getBroker().convertAndSend(JmsQueueName.DEFAULT.getName(),
									   getEventByEntity(SystemEventType.DELETE, deletedEntity));
		}
	}

	@Override
	public T get(Long aSuid) {
		return repository.get(aSuid);
	}

	@Override
	public void update(T aEntity, Boolean aNeedPublishEvent) {
		repository.update(aEntity);
		if (aNeedPublishEvent) {
			getBroker().convertAndSend(JmsQueueName.DEFAULT.getName(),
									   getEventByEntity(SystemEventType.UPDATE, aEntity));
		}
	}

	@Override
	public List<T> getAll(Long aUserSuid) {
		return repository.getAll();
	}

	/**
	 * Создает репозиторий
	 *
	 * @return репозиторий {@link@A}
	 */
	protected abstract AbstractRepository<T> createRepository();

	protected AbstractRepository<T> getRepository() {
		return repository;
	}

	@Override
	public ObjectEvent getEventByEntity(SystemEventType aType, T aEntity) {
		ObjectEvent event = null;
		try {
			Class<? extends ObjectEvent> typeEntityEvent = getTypeEntityEvent();
			event = typeEntityEvent.newInstance();
			event.setType(aType.getName());
			event.setEntity(aEntity);
		} catch (InstantiationException | IllegalAccessException e) {
			// Логирование
			e.printStackTrace();
		}
		return event;
	}

	/**
	 * Возвращает тип события
	 *
	 * @return тип события
	 */
	protected abstract Class<? extends ObjectEvent> getTypeEntityEvent();

	protected abstract JmsTemplate getBroker();

}
