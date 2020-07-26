package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.EventEntity;
import ru.devazz.repository.EventRepository;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.event.EventOccurEvent;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.EventModel;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.impl.converters.EventEntityConverter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса взаимодействия с событиями
 */
@Service
public class EventService extends AbstractEntityService<EventModel, EventEntity>
		implements IEventService {

	private final JmsTemplate broker;

	private final EventEntityConverter converter;

	private final EventRepository repository;

	public EventService(EventEntityConverter converter,
			EventRepository repository, JmsTemplate broker) {
		super(repository, converter, broker);
		this.broker = broker;
		this.converter = converter;
		this.repository = repository;
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return EventOccurEvent.class;
	}

	@Override
	protected String getQueueName() {
		return QueueNameEnum.EVENT_QUEUE;
	}

	@Override
	public List<EventModel> getEventsByTaskSuid(Long aSuid) {
		return repository.getEventsByTaskSuid(aSuid).stream().map(converter::entityToModel).collect(
				Collectors.toList());
	}

	@Override
	public void deleteEventsByTaskSuid(Long aSuid) {
		repository.deleteEventsByTasksSuid(aSuid);
	}

}
