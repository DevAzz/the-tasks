package ru.devazz.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.server.api.IHelpService;
import ru.devazz.server.api.model.HelpModel;

/**
 * Модель справки
 */
public class HelpViewModel extends PresentationModel<IHelpService, HelpModel> {

	/** Свойство основного текста */
	private StringProperty helpTextProperty;

	/** Свойство списка заголовков */
	private ListProperty<HelpModel> helpEntityListProperty;

	@Override
	protected void initModel() {
		loadEntities();
		helpEntityListProperty = new SimpleListProperty<>(getListEntities());
		helpTextProperty = new SimpleStringProperty(this, "helpTextProperty", "");

	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public ListProperty<HelpModel> getHelpEntityListProperty() {
		return helpEntityListProperty;
	}

	public StringProperty getHelpTextProperty() {
		return helpTextProperty;
	}

	/**
	 * Устанавливает значение полю selection
	 *
	 * @param selection значение поле
	 */
	public void setSelection(HelpModel selection) {
		if (null != selection) {
			// Для выбора заголовка
			getHelpTextProperty().set(selection.getHelpItemText());
		}
	}

	@Override
	public Class<IHelpService> getTypeService() {
		return IHelpService.class;
	}

}
