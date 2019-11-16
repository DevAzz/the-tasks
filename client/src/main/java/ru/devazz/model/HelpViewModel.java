package ru.devazz.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.sciencesquad.hqtasks.server.bean.help.HelpServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.HelpEntity;

/**
 * Модель справки
 */
public class HelpViewModel extends PresentationModel<HelpServiceRemote, HelpEntity> {

	/** Для выбора заголовка */
	private HelpEntity selection;

	/** Свойство основного текста */
	private StringProperty helpTextProperty;

	/** Свойство списка заголовков */
	private ListProperty<HelpEntity> helpEntityListProperty;

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
	public ListProperty<HelpEntity> getHelpEntityListProperty() {
		return helpEntityListProperty;
	}

	/**
	 * Устанавливает значение полю helpEntityListProperty
	 *
	 * @param helpEntityListProperty значение поле
	 */
	public void setHelpEntityListProperty(ListProperty<HelpEntity> helpEntityListProperty) {
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
	public HelpEntity getSelection() {
		return selection;
	}

	/**
	 * Устанавливает значение полю selection
	 *
	 * @param selection значение поле
	 */
	public void setSelection(HelpEntity selection) {
		if (null != selection) {
			this.selection = selection;
			getHelpTextProperty().set(selection.getHelpItemText());
		}
	}

	@Override
	public Class<HelpServiceRemote> getTypeService() {
		// TODO Auto-generated method stub
		return HelpServiceRemote.class;
	}

}
