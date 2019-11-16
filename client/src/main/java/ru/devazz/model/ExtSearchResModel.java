package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.siencesquad.hqtasks.ui.entities.ExtSearchRes;

/**
 * Модель предсатвления панелей результатов расширенного поиска
 */
public class ExtSearchResModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста лейбла заголовка */
	private StringProperty resLabelProperty;

	/** Результат поиска */
	private ExtSearchRes result;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		resLabelProperty = new SimpleStringProperty(this, "resLabelProperty", "Заголовок");
	}

	/**
	 * Возвращает {@link#resLabelProperty}
	 *
	 * @return the {@link#resLabelProperty}
	 */
	public StringProperty getResLabelProperty() {
		return resLabelProperty;
	}

	/**
	 * Возвращает {@link#result}
	 *
	 * @return the {@link#result}
	 */
	public ExtSearchRes getResult() {
		return result;
	}

	/**
	 * Устанавливает значение полю {@link#result}
	 *
	 * @param result значение поля
	 */
	public void setResult(ExtSearchRes result) {
		this.result = result;
		setResLabelValue(result.getName());
	}

	/**
	 * Устанавливает значение полю {@link#resLabelProperty}
	 *
	 * @param resLabelValue значение поля
	 */
	public void setResLabelValue(String resLabelValue) {
		this.resLabelProperty.set(resLabelValue);
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		// TODO Auto-generated method stub
		return null;
	}

}
