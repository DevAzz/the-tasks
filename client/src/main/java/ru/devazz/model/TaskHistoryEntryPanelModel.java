package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.TaskHistoryEntity;

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
	private TaskHistoryEntity entity;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		titleLabelProperty = new SimpleStringProperty(this, "titleLabelProperty", "Заголовок");
		textLabelProperty = new SimpleStringProperty(this, "textLabelProperty", "Описание");
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "Дата");
		imageProperty = new SimpleObjectProperty<>(this, "imageProperty", null);

	}

	/**
	 * Возвращает {@link#entity}
	 *
	 * @return the {@link#entity}
	 */
	public TaskHistoryEntity getEntity() {
		return entity;
	}

	/**
	 * Устанавливает значение полю {@link#entity}
	 *
	 * @param aEntity значение поля
	 */
	public void setEntity(TaskHistoryEntity aEntity) {
		this.entity = aEntity;

		setTextLabelProperty(aEntity.getText());
		setTitleLabelText(aEntity.getTitle());

		SimpleDateFormat parser = new SimpleDateFormat("HH:mm dd.MM.yyyy");

		String date = " " + parser.format(aEntity.getDate());
		setDateLabelPropertyValue(date);

		Image image = null;
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

	/**
	 * Возвращает {@link#titleLabelProperty}
	 *
	 * @return the {@link#titleLabelProperty}
	 */
	public StringProperty getTitleLabelProperty() {
		return titleLabelProperty;
	}

	/**
	 * Возвращает {@link#imageProperty}
	 *
	 * @return the {@link#imageProperty}
	 */
	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}

	/**
	 * Устанавливает значение полю {@link#imageProperty}
	 *
	 * @param aImage значение поля
	 */
	public void setImagePropertyValue(Image aImage) {
		this.imageProperty.set(aImage);
	}

	/**
	 * Устанавливает значение полю {@link#titleLabelProperty}
	 *
	 * @param {@link#titleLabelProperty}
	 */
	public void setTitleLabelText(String titleLabelText) {
		this.titleLabelProperty.set(titleLabelText);
	}

	/**
	 * Возвращает {@link#textLabelProperty}
	 *
	 * @return the {@link#textLabelProperty}
	 */
	public StringProperty getTextLabelProperty() {
		return textLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#textLabelProperty}
	 *
	 * @param {@link#textLabelProperty}
	 */
	public void setTextLabelProperty(String textLabelText) {
		this.textLabelProperty.set(textLabelText);
	}

	/**
	 * Возвращает {@link#dateLabelProperty}
	 *
	 * @return the {@link#dateLabelProperty}
	 */
	public StringProperty getDateLabelProperty() {
		return dateLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#dateLabelProperty}
	 *
	 * @param dateLabelPropertyValue значение поля
	 */
	public void setDateLabelPropertyValue(String dateLabelPropertyValue) {
		this.dateLabelProperty.set(dateLabelPropertyValue);
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
