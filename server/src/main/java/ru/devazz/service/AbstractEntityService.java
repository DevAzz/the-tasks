package ru.devazz.service;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import ru.devazz.repository.AbstractRepository;
import ru.devazz.server.api.IEntityService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.enums.JmsQueueName;
import ru.devazz.server.api.model.enums.SystemEventType;
import ru.devazz.service.impl.converters.IEntityConverter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Абстрактный сервис
 */
@AllArgsConstructor
public abstract class AbstractEntityService<M extends IEntity, E extends IEntity> implements IEntityService<M> {

	/** Абстрактный репозиторий */
	private AbstractRepository<E> repository;

	private IEntityConverter<M, E> converter;

	private final JmsTemplate broker;

	@Override
	public M add(M aEntity, Boolean aNeedPublishEvent) {
		E createdEntity = repository.add(converter.modelToEntity(aEntity));
		if (aNeedPublishEvent) {
			broker.convertAndSend(JmsQueueName.DEFAULT.getName(),
									   (getEventByEntity(SystemEventType.CREATE,
														 converter.entityToModel(createdEntity))));
		}
		return converter.entityToModel(createdEntity);
	}

	@Override
	public void delete(Long aSuid, Boolean aNeedPublishEvent) {
		E deletedEntity = repository.get(aSuid);
		repository.delete(aSuid);
		if (aNeedPublishEvent) {
			broker.convertAndSend(JmsQueueName.DEFAULT.getName(),
									   getEventByEntity(SystemEventType.DELETE,
														converter.entityToModel(deletedEntity)));
		}
	}

	@Override
	public M get(Long aSuid) {
		return converter.entityToModel(repository.get(aSuid));
	}

	@Override
	public void update(M aEntity, Boolean aNeedPublishEvent) {
		repository.update(converter.modelToEntity(aEntity));
		if (aNeedPublishEvent) {
			broker.convertAndSend(JmsQueueName.DEFAULT.getName(),
									   getEventByEntity(SystemEventType.UPDATE, aEntity));
		}
	}

	@Override
	public List<M> getAll(Long aUserSuid) {
		return repository.getAll().stream().map(converter::entityToModel).collect(Collectors.toList());
	}

	@Override
	public ObjectEvent getEventByEntity(SystemEventType aType, M aEntity) {
		ObjectEvent event = null;
		try {
			Class<? extends ObjectEvent> typeEntityEvent = getTypeEntityEvent();
			event = typeEntityEvent.newInstance();
			event.setType(aType.getName());
			event.setEntity(aEntity);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Логирование
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
}
