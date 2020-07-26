package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.ExtSearchRes;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

/**
 * Модель предсатвления панелей результатов расширенного поиска
 */
public class ExtSearchResModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста лейбла заголовка */
	private StringProperty resLabelProperty;

	/** Результат поиска */
	private ExtSearchRes result;

	@Override
	protected void initModel() {
		resLabelProperty = new SimpleStringProperty(this, "resLabelProperty", "Заголовок");
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public StringProperty getResLabelProperty() {
		return resLabelProperty;
	}

	public ExtSearchRes getResult() {
		return result;
	}

	public void setResult(ExtSearchRes result) {
		this.result = result;
		setResLabelValue(result.getName());
	}

	private void setResLabelValue(String resLabelValue) {
		this.resLabelProperty.set(resLabelValue);
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
