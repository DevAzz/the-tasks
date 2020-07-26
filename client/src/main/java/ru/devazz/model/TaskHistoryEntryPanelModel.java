package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

import java.text.SimpleDateFormat;

/**
 * Модель представления панели исторической записи
 */
public class TaskHistoryEntryPanelModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста даты задачи */
	private StringProperty dateLabelProperty;

	/** Свойство текста лейбла заголовка */
	private StringProperty titleLabelProperty;

	/** Свойство текста лейбла описания */
	private StringProperty textLabelProperty;

	/** Свойство изображения пиктограммы типа исторической записи */
	private ObjectProperty<Image> imageProperty;

	/** Историческая запись */
	private ru.devazz.server.api.model.TaskHistoryModel entity;

	@Override
	protected void initModel() {
		titleLabelProperty = new SimpleStringProperty(this, "titleLabelProperty", "Заголовок");
		textLabelProperty = new SimpleStringProperty(this, "textLabelProperty", "Описание");
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "Дата");
		imageProperty = new SimpleObjectProperty<>(this, "imageProperty", null);

	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public ru.devazz.server.api.model.TaskHistoryModel getEntity() {
		return entity;
	}

	public void setEntity(ru.devazz.server.api.model.TaskHistoryModel aEntity) {
		this.entity = aEntity;

		setTextLabelProperty(aEntity.getText());
		setTitleLabelText(aEntity.getName());

		SimpleDateFormat parser = new SimpleDateFormat("HH:mm dd.MM.yyyy");

		String date = " " + parser.format(aEntity.getDate());
		setDateLabelPropertyValue(date);

		Image image;
		switch (aEntity.getHistoryType()) {
		case TASK_OVERDUE_DONE:
			image = new Image("/css/overdueDone.png");
			setImagePropertyValue(image);
			break;
		case TASK_CLOSED:
			image = new Image("/css/complitedAndLocked.png");
			setImagePropertyValue(image);
			break;
		case TASK_DONE:
			image = new Image("/css/complited.png");
			setImagePropertyValue(image);
			break;
		case TASK_FAILED:
			image = new Image("/css/faild.png");
			setImagePropertyValue(image);
			break;
		case TASK_OVERDUE:
			image = new Image("/css/timeoutcomplited.png");
			setImagePropertyValue(image);
			break;
		case TASK_REMAPPING:
			image = new Image("/css/remaping.png");
			setImagePropertyValue(image);
			break;
		case TASK_REWORK:
			image = new Image("/css/rework.png");
			setImagePropertyValue(image);
			break;
		case TASK_UPDATED:
			image = new Image("/css/refresh.png");
			setImagePropertyValue(image);
			break;
		}
	}

	public StringProperty getTitleLabelProperty() {
		return titleLabelProperty;
	}

	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}

	private void setImagePropertyValue(Image aImage) {
		this.imageProperty.set(aImage);
	}

	private void setTitleLabelText(String titleLabelText) {
		this.titleLabelProperty.set(titleLabelText);
	}

	public StringProperty getTextLabelProperty() {
		return textLabelProperty;
	}

	private void setTextLabelProperty(String textLabelText) {
		this.textLabelProperty.set(textLabelText);
	}

	public StringProperty getDateLabelProperty() {
		return dateLabelProperty;
	}

	private void setDateLabelPropertyValue(String dateLabelPropertyValue) {
		this.dateLabelProperty.set(dateLabelPropertyValue);
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
