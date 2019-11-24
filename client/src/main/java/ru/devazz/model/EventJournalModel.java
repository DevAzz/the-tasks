package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.Event;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.IEventService;
import ru.devazz.server.api.model.EventModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.EventType;
import ru.devazz.utils.Utils;

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
	 * Возвращает {@link#data}
	 *
	 * @return the {@link#data}
	 */
	public ObservableList<Event> getEtalonData() {
		return etalonData;
	}

	/**
	 * Устанавливает значение полю {@link#data}
	 *
	 * @param {@link#data}
	 */
	public void setEtalonData(ObservableList<Event> data) {
		this.etalonData = data;
	}

	/**
	 * Конструктор
	 */
	public EventJournalModel() {
		super();
		initModel();
	}

	@Override
	protected void initModel() {
		etalonData = FXCollections.observableArrayList();
		dataProperty = new SimpleObjectProperty<>(etalonData);
	}

	/**
	 * Подключение к сервису рассылки уведомлений
	 */
	public void connectToJMSService() {
		ProxyFactory.getInstance().addMessageListener("eventJournal", "eventsQueue", arg0 -> {
			loadEntities();
		});
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
	 * Задаёт значния даты события
	 *
	 * @param hour
	 * @param minute
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 */
	public Date initDate(int hour, int minute, int day, int month, int year) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		Date date = calendar.getTime();
		return date;
	}

	/**
	 * Фильтрует события по дате
	 *
	 * @param aList
	 * @return
	 */
	public ObservableList<Event> changedDataByDate(String aList) {
		ObservableList<Event> tempData = FXCollections.observableArrayList();
		switch (aList) {
		case "dayFilterItem":
			Date dateStartDay = Utils.getInstance().getStartDateForFilterDate();
			Date dateEndDay = Utils.getInstance().getEndDateForFilterDate();
			for (EventModel event : listDataModelEntities) {
				Date eventDate = event.getDate();
				long eventDateLong = eventDate.getTime();

				if ((eventDateLong >= dateStartDay.getTime())
						&& ((eventDateLong <= dateEndDay.getTime()))) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
				}
			}

			break;

		case "weekFilterItem":
			Date dateStartWeek = Utils.getInstance().getStartDateForFilterWeek();
			Date dateEndWeek = Utils.getInstance().getEndDateForFilterWeek();
			for (EventModel event : listDataModelEntities) {
				Date eventDate = event.getDate();
				long eventDateLong = eventDate.getTime();

				if ((eventDateLong >= dateStartWeek.getTime())
						&& ((eventDateLong <= dateEndWeek.getTime()))) {
					tempData.add(EntityConverter.getInstatnce()
							.convertEventModelToClientWrapEvent(event));
				}
			}
			break;

		case "monthFilterItem":
			Date dateStartMonth = Utils.getInstance().getStartDateForFilterMonth();
			Date dateEndMonth = Utils.getInstance().getEndDateForFilterMonth();
			for (EventModel event : listDataModelEntities) {
				Date eventDate = event.getDate();
				long eventDateLong = eventDate.getTime();

				if ((eventDateLong >= dateStartMonth.getTime())
						&& ((eventDateLong <= dateEndMonth.getTime()))) {
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

	/**
	 * Возвращает {@link#dataProperty}
	 *
	 * @return the {@link#dataProperty}
	 */
	public ObjectProperty<ObservableList<Event>> getDataProperty() {
		return dataProperty;
	}

	/**
	 * Устанавливает значение полю {@link#dataProperty}
	 *
	 * @param dataPropertyValue значение поля
	 */
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
