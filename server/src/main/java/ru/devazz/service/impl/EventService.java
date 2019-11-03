package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.devazz.entity.EventEntity;
import ru.devazz.event.EventOccurEvent;
import ru.devazz.event.ObjectEvent;
import ru.devazz.repository.EventRepository;
import ru.devazz.service.AbstractEntityService;
import ru.devazz.service.IEventService;

import java.util.List;

/**
 * Реализация сервиса взаимодействия с событиями
 */
@Service
@AllArgsConstructor
public class EventService extends AbstractEntityService<EventEntity>
		implements IEventService {

	private JmsTemplate broker;

	@Override
	protected EventRepository createRepository() {
		return new EventRepository();
	}

	@Override
	protected Class<? extends ObjectEvent> getTypeEntityEvent() {
		return EventOccurEvent.class;
	}

	@Override
	protected JmsTemplate getBroker() {
		return broker;
	}


	@Override
	public List<EventEntity> getEventsByTaskSuid(Long aSuid) {
		return ((EventRepository) repository).getEventsByTaskSuid(aSuid);
	}

	@Override
	public void deleteEventsByTaskSuid(Long aSuid) {
		((EventRepository) repository).deleteEventsByTasksSuid(aSuid);
	}

}
