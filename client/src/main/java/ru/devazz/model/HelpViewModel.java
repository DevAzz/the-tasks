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

	/** Для выбора заголовка */
	private HelpModel selection;

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

	/**
	 * Возвращает {@link#helpEntityListProperty}
	 *
	 * @return the helpEntityListProperty
	 */
	public ListProperty<HelpModel> getHelpEntityListProperty() {
		return helpEntityListProperty;
	}

	/**
	 * Устанавливает значение полю helpEntityListProperty
	 *
	 * @param helpEntityListProperty значение поле
	 */
	public void setHelpEntityListProperty(ListProperty<HelpModel> helpEntityListProperty) {
		this.helpEntityListProperty = helpEntityListProperty;
	}

	/**
	 * Возвращает {@link#helpTextProperty}
	 *
	 * @return the helpTextProperty
	 */
	public StringProperty getHelpTextProperty() {
		return helpTextProperty;
	}

	/**
	 * Устанавливает значение полю helpTextProperty
	 *
	 * @param helpTextProperty значение поле
	 */
	public void setHelpTextProperty(StringProperty helpTextProperty) {
		this.helpTextProperty = helpTextProperty;
	}

	/**
	 * Возвращает {@link#selection}
	 *
	 * @return the selection
	 */
	public HelpModel getSelection() {
		return selection;
	}

	/**
	 * Устанавливает значение полю selection
	 *
	 * @param selection значение поле
	 */
	public void setSelection(HelpModel selection) {
		if (null != selection) {
			this.selection = selection;
			getHelpTextProperty().set(selection.getHelpItemText());
		}
	}

	@Override
	public Class<IHelpService> getTypeService() {
		// TODO Auto-generated method stub
		return IHelpService.class;
	}

}
