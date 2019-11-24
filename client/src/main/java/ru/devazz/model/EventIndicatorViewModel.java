package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import ru.devazz.entities.Event;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.event.EventOccurEvent;
import ru.devazz.server.api.event.ObjectEvent;
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
	}

	/**
	 * Подключение к сервису рассылки уведомлений
	 */
	public void connectToJMSService() {
		ProxyFactory.getInstance().addMessageListener("eventIndicatorView", "eventsQueue",
														 message -> {
			try {
				if (message instanceof ActiveMQMessage) {
					ActiveMQMessage objectMessage = (ActiveMQMessage) message;
					if (objectMessage instanceof ActiveMQObjectMessage) {
						ObjectEvent event =
								(ObjectEvent) ((ActiveMQObjectMessage) objectMessage).getObject();
						if (event instanceof EventOccurEvent) {
							EventModel entity = (EventModel) event.getEntity();
							Platform.runLater(() -> {
								listEvents.add(EntityConverter.getInstatnce()
										.convertEventModelToClientWrapEvent(entity));
							});

						}
					}
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Удаляет события
	 *
	 * @param aDeleteItems список удаляемых событий
	 */
	public void deleteEvents(List<Event> aDeleteItems) {
		listEvents.removeAll(aDeleteItems);
	}

	/**
	 * Возвращает {@link#listProperty}
	 *
	 * @return the {@link#listProperty}
	 */
	public ListProperty<Event> getListProperty() {
		return listProperty;
	}

	/**
	 * Устанавливает значение полю {@link#listProperty}
	 *
	 * @param {@link#listProperty}
	 */
	public void setListProperty(ListProperty<Event> listProperty) {
		this.listProperty = listProperty;
	}

	/**
	 * Возвращает {@link#listEvents}
	 *
	 * @return the {@link#listEvents}
	 */
	public ObservableList<Event> getListEvents() {
		return listEvents;
	}

	/**
	 * Устанавливает значение полю {@link#listEvents}
	 *
	 * @param {@link#listEvents}
	 */
	public void setListEvents(ObservableList<Event> listEvents) {
		this.listEvents = listEvents;
	}

	@Override
	public Class<IEventService> getTypeService() {
		return IEventService.class;
	}

}
