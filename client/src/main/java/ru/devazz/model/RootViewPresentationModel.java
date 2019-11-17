package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Модель корнегвого представления
 */
public class RootViewPresentationModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста */
	private StringProperty searchBoxTextProperty;

	private StringProperty dateTimeTextProperty;

	/**
	 * Конструктор
	 */
	public RootViewPresentationModel() {
		super();
		/* Часы */
		Thread timeThread = new Thread(() -> {
			while (true) {
				try {
					Date date = new Date();
					SimpleDateFormat formatForTimeNow = new SimpleDateFormat("HH:mm:ss  d.MM.Y");
					Platform.runLater(
							() -> dateTimeTextProperty.set(formatForTimeNow.format(date)));
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timeThread.setDaemon(true);
		timeThread.setName("TimeThread");
		timeThread.start();
	}

	/**
	 * Возвращает {@link#searchBoxTextProperty}
	 *
	 * @return the {@link#searchBoxTextProperty}
	 */
	public StringProperty getSearchBoxTextProperty() {
		return searchBoxTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#searchBoxTextProperty}
	 *
	 * @param searchBoxTextProperty значение поля
	 */
	public void setSearchBoxTextProperty(StringProperty searchBoxTextProperty) {
		this.searchBoxTextProperty = searchBoxTextProperty;
	}

	/**
	 * Возвращает {@link#dateTimeTextProperty}
	 *
	 * @return the {@link#dateTimeTextProperty}
	 */
	public StringProperty getDateTimeTextProperty() {
		return dateTimeTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#dateTimeTextProperty}
	 *
	 * @param dateTimeTextProperty значение поля
	 */
	public void setDateTimeTextProperty(StringProperty dateTimeTextProperty) {
		this.dateTimeTextProperty = dateTimeTextProperty;
	}

	@Override
	protected void initModel() {
		searchBoxTextProperty = new SimpleStringProperty(this, "searchBoxTextProperty", "");
		dateTimeTextProperty = new SimpleStringProperty(this, "dateTimeTextProperty", "время и дата");
	}

	@Override
	public Class<ICommonService> getTypeService() {
		// TODO Auto-generated method stub
		return null;
	}

}
