package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.Event;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.IMessageListener;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.EventModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.EventType;
import ru.devazz.utils.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Модель представления журнала событий
 */
public class EventJournalModel extends PresentationModel<IEventService, EventModel> {

	/** Список событий */
	private ObservableList<Event> etalonData;

	/** Свойство списка таблицы событий */
	private ObjectProperty<ObservableList<Event>> dataProperty;

	/**
	 * Конструктор
	 */
	public EventJournalModel() {
		super();
		initModel();
	}

	@Override
	protected String getQueueName() {
		return QueueNameEnum.EVENT_QUEUE;
	}

	@Override
	protected void initModel() {
		etalonData = FXCollections.observableArrayList();
		dataProperty = new SimpleObjectProperty<>(etalonData);
		addJmsListener(getMessageListener());
	}

	private IMessageListener getMessageListener() {
		return event -> loadEntities();
	}

	/**
	 * Загружает записи
	 */
	@Override
	public void loadEntities() {
		Thread thread = new Thread(() -> {
			try {
				super.loadEntities();
				for (EventModel entity : new ArrayList<>(listDataModelEntities)) {
					Event event = EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(entity);
					if (!etalonData.contains(event)) {
						etalonData.add(event);
					}
				}
			} catch (Exception e) {
				// TODO логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Фильтрует события по дате
	 */
	public ObservableList<Event> changedDataByDate(String aList) {
		ObservableList<Event> tempData = FXCollections.observableArrayList();
		switch (aList) {
		case "dayFilterItem":
			LocalDateTime dateStartDay = Utils.getInstance().getStartDateForFilterDate();
			LocalDateTime dateEndDay = Utils.getInstance().getEndDateForFilterDate();
			for (EventModel event : listDataModelEntities) {
				LocalDateTime eventDate = event.getDate();
				long eventDateLong = eventDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

				if ((eventDateLong >= dateStartDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
						&& ((eventDateLong <= dateEndDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
				}
			}

			break;

		case "weekFilterItem":
			LocalDateTime dateStartWeek = Utils.getInstance().getStartDateForFilterWeek();
			LocalDateTime dateEndWeek = Utils.getInstance().getEndDateForFilterWeek();
			for (EventModel event : listDataModelEntities) {
				LocalDateTime eventDate = event.getDate();
				long eventDateLong = eventDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

				if ((eventDateLong >= dateStartWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
						&& ((eventDateLong <= dateEndWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
				}
			}
			break;

		case "monthFilterItem":
			LocalDateTime dateStartMonth = Utils.getInstance().getStartDateForFilterMonth();
			LocalDateTime dateEndMonth = Utils.getInstance().getEndDateForFilterMonth();
			for (EventModel event : listDataModelEntities) {
				LocalDateTime eventDate = event.getDate();
				long eventDateLong = eventDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

				if ((eventDateLong >= dateStartMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
						&& ((eventDateLong <= dateEndMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
				}
			}
			break;

		case "allTimeFilterItem":
			tempData.clear();
			for (EventModel event : listDataModelEntities) {
				tempData.add(EntityConverter.getInstatnce()
						.convertEventModelToClientWrapEvent(event));
			}

		default:
			break;
		}

		return tempData;
	}

	/**
	 * Фильтрует записи по суиду события
	 *
	 * @param aList список суидов
	 * @return ObservableList<Event> список отфиьтрованных событий
	 */
	public ObservableList<Event> changedDataByEventType(List<String> aList) {
		ObservableList<Event> tempData = FXCollections.observableArrayList();
		for (String note : aList) {
			for (EventModel event : listDataModelEntities) {
				Event eventWrap = EntityConverter.getInstatnce()
						.convertEventModelToClientWrapEvent(event);
				EventType type = eventWrap.getEventType();
				if ((null != note) && (null != type)
						&& note.equals(eventWrap.getEventType().getSuid())) {
					tempData.add(eventWrap);
				}
			}
		}

		return tempData;
	}

	/**
	 * Фильтрует записи по должности
	 *
	 * @param aList список выбранных должностей
	 * @return ObservableList<Event> отфильтрованный список событий
	 */
	public ObservableList<Event> changedDataByAuthors(List<SubordinationElement> aList) {
		ObservableList<Event> tempData = FXCollections.observableArrayList();
		for (EventModel event : listDataModelEntities) {
			for (SubordinationElement element : aList) {
				if (event.getAuthorSuid().equals(element.getSuid())) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
					break;
				}
			}
		}

		return tempData;
	}

	/**
	 * Фильтрует записи по должности
	 *
	 * @param aList список выбранных должностей
	 * @return ObservableList<Event> отфильтрованный список событий
	 */
	public ObservableList<Event> changedDataByExecutors(List<SubordinationElement> aList) {
		ObservableList<Event> tempData = FXCollections.observableArrayList();
		for (EventModel event : listDataModelEntities) {
			for (SubordinationElement element : aList) {
				if (event.getExecutorSuid().equals(element.getSuid())) {
					tempData.add((EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event)));
					break;
				}
			}
		}

		return tempData;
	}

	/**
	 * Возвращает список подразделений, по которым производится фильтрация
	 *
	 * @return список подразделений
	 */
	public ObservableList<SubordinationElement> getExecutors() {
		ObservableList<SubordinationElement> result = FXCollections.observableArrayList();
		for (Event event : etalonData) {
			if (!result.contains(event.getExecutor())) {
				result.add(event.getExecutor());
			}
		}
		return result;
	}

	/**
	 * Возвращает список подразделений, по которым производится фильтрация
	 *
	 * @return список подразделений
	 */
	public ObservableList<SubordinationElement> getAuthors() {
		ObservableList<SubordinationElement> result = FXCollections.observableArrayList();
		for (Event event : etalonData) {
			if (!result.contains(event.getAuthor())) {
				result.add(event.getAuthor());
			}
		}
		return result;
	}

	public ObjectProperty<ObservableList<Event>> getDataProperty() {
		return dataProperty;
	}

	public void setDataPropertyValue(ObservableList<Event> dataPropertyValue) {
		this.dataProperty.set(dataPropertyValue);
	}

	@Override
	public Class<IEventService> getTypeService() {
		return IEventService.class;
	}

	/**
	 * Возвращает весь список событий
	 *
	 * @return полный список событий
	 */
	public ObservableList<Event> getAllEntries() {
		ObservableList<Event> result = FXCollections.observableArrayList();
		for (EventModel entity : listDataModelEntities) {
			Event wrap = EntityConverter.getInstatnce()
					.convertEventModelToClientWrapEvent(entity);
			result.add(wrap);
		}
		return result;
	}

}
