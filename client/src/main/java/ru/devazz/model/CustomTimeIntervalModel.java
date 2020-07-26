package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Модель представления компонента выбора временного интервала
 */
public class CustomTimeIntervalModel extends PresentationModel<ICommonService, IEntity> {

	/** Парсер даты */
	private static final SimpleDateFormat PARSER = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	/**
	 * Свойство текста поля ввода начальной даты фильтрации по временному промежутку
	 */
	private StringProperty fromTimeIntervalProperty;

	/**
	 * Свойство текста поля ввода конечной даты фильтрации по временному промежутку
	 */
	private StringProperty toTimeIntervalProperty;

	/** Свойство недоступности кнопки поиска */
	private BooleanProperty disableSearchButton;

	@Override
	protected void initModel() {
		fromTimeIntervalProperty = new SimpleStringProperty(this, "fromTimeIntervalProperty", "");
		toTimeIntervalProperty = new SimpleStringProperty(this, "toTimeIntervalProperty", "");
		disableSearchButton = new SimpleBooleanProperty(this, "disableSearchButton", true);

		fromTimeIntervalProperty
				.addListener((observable, oldValue, newValue) -> {
					String start = (null != getToTimeIntervalProperty().get())
							? getToTimeIntervalProperty().get()
							: "";
					setDisableSearchButtonValue(newValue.isEmpty() || (start.isEmpty()));
				});
		toTimeIntervalProperty
				.addListener((observable, oldValue, newValue) -> {
					String end = (null != getFromTimeIntervalProperty().get())
							? getFromTimeIntervalProperty().get()
							: "";
					setDisableSearchButtonValue(newValue.isEmpty() || (end.isEmpty()));
				});

	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public BooleanProperty getDisableSearchButton() {
		return disableSearchButton;
	}

	private void setDisableSearchButtonValue(Boolean disableSearchButtonValue) {
		this.disableSearchButton.set(disableSearchButtonValue);
	}

	public StringProperty getFromTimeIntervalProperty() {
		return fromTimeIntervalProperty;
	}

	public StringProperty getToTimeIntervalProperty() {
		return toTimeIntervalProperty;
	}

	/**
	 * Возвращает дату начала для фильтрации по заданному временному промежутку
	 *
	 * @return дата начала для фильтрации по заданному временному промежутку
	 * @throws ParseException в случае ошибки парсинга
	 */
	Date getStartDate() throws ParseException {
		Date result = null;
		String dateFrom = fromTimeIntervalProperty.get();
		if (!dateFrom.isEmpty()) {
			result = PARSER.parse(dateFrom);
		}
		return result;
	}

	/**
	 * Возвращает дату конца для фильтрации по заданному временному промежутку
	 *
	 * @return дата конца для фильтрации по заданному временному промежутку
	 * @throws ParseException в случае ошибки парсинга
	 */
	public Date getEndDate() throws ParseException {
		Date result = null;
		String dateTo = toTimeIntervalProperty.get();
		if (!dateTo.isEmpty()) {
			result = PARSER.parse(dateTo);
		}
		return result;
	}

	/**
	 * Очищает поля ввода дат
	 */
	public void clearDateFields() {
		toTimeIntervalProperty.set("");
		fromTimeIntervalProperty.set("");
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
