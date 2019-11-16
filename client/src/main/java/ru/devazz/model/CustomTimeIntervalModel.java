package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;

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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		fromTimeIntervalProperty = new SimpleStringProperty(this, "fromTimeIntervalProperty", "");
		toTimeIntervalProperty = new SimpleStringProperty(this, "toTimeIntervalProperty", "");
		disableSearchButton = new SimpleBooleanProperty(this, "disableSearchButton", true);

		fromTimeIntervalProperty
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					String start = (null != getToTimeIntervalProperty().get())
							? getToTimeIntervalProperty().get()
							: "";
					setDisableSearchButtonValue(newValue.isEmpty() || (start.isEmpty()));
				});
		toTimeIntervalProperty
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					String end = (null != getFromTimeIntervalProperty().get())
							? getFromTimeIntervalProperty().get()
							: "";
					setDisableSearchButtonValue(newValue.isEmpty() || (end.isEmpty()));
				});

	}

	/**
	 * Возвращает {@link#disableSearchButton}
	 *
	 * @return the {@link#disableSearchButton}
	 */
	public BooleanProperty getDisableSearchButton() {
		return disableSearchButton;
	}

	/**
	 * Устанавливает значение полю {@link#disableSearchButton}
	 *
	 * @param disableSearchButtonValue значение поля
	 */
	public void setDisableSearchButtonValue(Boolean disableSearchButtonValue) {
		this.disableSearchButton.set(disableSearchButtonValue);
	}

	/**
	 * Возвращает {@link#fromTimeIntervalProperty}
	 *
	 * @return the {@link#fromTimeIntervalProperty}
	 */
	public StringProperty getFromTimeIntervalProperty() {
		return fromTimeIntervalProperty;
	}

	/**
	 * Устанавливает значение полю {@link#fromTimeIntervalProperty}
	 *
	 * @param fromTimeIntervalPropertyValue значение поля
	 */
	public void setFromTimeIntervalProperty(String fromTimeIntervalPropertyValue) {
		this.fromTimeIntervalProperty.set(fromTimeIntervalPropertyValue);
	}

	/**
	 * Возвращает {@link#toTimeIntervalProperty}
	 *
	 * @return the {@link#toTimeIntervalProperty}
	 */
	public StringProperty getToTimeIntervalProperty() {
		return toTimeIntervalProperty;
	}

	/**
	 * Устанавливает значение полю {@link#toTimeIntervalProperty}
	 *
	 * @param toTimeIntervalProperty значение поля
	 */
	public void setToTimeIntervalPropertyValue(String toTimeIntervalPropertyValue) {
		this.toTimeIntervalProperty.set(toTimeIntervalPropertyValue);
	}

	/**
	 * Возвращает дату начала для фильтрации по заданному временному промежутку
	 *
	 * @return дата начала для фильтрации по заданному временному промежутку
	 * @throws ParseException в случае ошибки парсинга
	 */
	public Date getStartDate() throws ParseException {
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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
