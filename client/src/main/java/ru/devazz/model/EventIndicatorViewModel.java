package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.events.EventServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.EventEntity;
import ru.sciencesquad.hqtasks.server.events.EventOccurEvent;
import ru.sciencesquad.hqtasks.server.events.ObjectEvent;
import ru.siencesquad.hqtasks.ui.entities.Event;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import java.util.List;

/**
 * Модель представления индикатора сбытий
 */
public class EventIndicatorViewModel extends PresentationModel<EventServiceRemote, EventEntity> {

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
		EJBProxyFactory.getInstance().addMessageListener(message -> {
			try {
				if (message instanceof ObjectMessage) {
					ObjectMessage objectMessage = (ObjectMessage) message;
					if (objectMessage.isBodyAssignableTo(ObjectEvent.class)) {
						ObjectEvent event = objectMessage.getBody(ObjectEvent.class);
						if (event instanceof EventOccurEvent) {
							EventEntity entity = (EventEntity) event.getEntity();
							Platform.runLater(() -> {
								try {
									listEvents.add(EntityConverter.getInstatnce()
											.convertEventEntityToClientWrapEvent(entity));
								} catch (NamingException e) {
									// TODO Логирование
									e.printStackTrace();
								}
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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<EventServiceRemote> getTypeService() {
		return EventServiceRemote.class;
	}

}
