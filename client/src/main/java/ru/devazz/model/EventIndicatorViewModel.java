package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import ru.devazz.entities.Event;
import ru.devazz.server.IMessageListener;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.event.EventOccurEvent;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.EventModel;
import ru.devazz.utils.EntityConverter;

import javax.jms.JMSException;
import java.util.List;

/**
 * Модель представления индикатора сбытий
 */
public class EventIndicatorViewModel extends PresentationModel<IEventService, EventModel> {

	/** Свойства списка событий */
	private ListProperty<Event> listProperty;

	/** Список событий */
	private ObservableList<Event> listEvents;

	@Override
	protected void initModel() {
		listEvents = FXCollections.observableArrayList();
		listProperty = new SimpleListProperty<>(listEvents);
		addJmsListener(getMessageListener());
	}

	@Override
	protected String getQueueName() {
		return QueueNameEnum.EVENT_QUEUE;
	}

	private IMessageListener getMessageListener() {
		return event -> {
			if (event instanceof EventOccurEvent) {
				EventModel entity = (EventModel) event
						.getEntity();
				Platform.runLater(() -> listEvents.add(EntityConverter
									   .getInstatnce()
									   .convertEventModelToClientWrapEvent(entity)));

			}
		};
	}

	/**
	 * Удаляет события
	 *
	 * @param aDeleteItems список удаляемых событий
	 */
	public void deleteEvents(List<Event> aDeleteItems) {
		listEvents.removeAll(aDeleteItems);
	}

	public ListProperty<Event> getListProperty() {
		return listProperty;
	}

	public ObservableList<Event> getListEvents() {
		return listEvents;
	}

	@Override
	public Class<IEventService> getTypeService() {
		return IEventService.class;
	}

}
