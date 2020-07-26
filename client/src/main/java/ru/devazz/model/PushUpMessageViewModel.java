package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

public class PushUpMessageViewModel extends PresentationModel<ICommonService, IEntity> {

	/** свойство заголовка сообщения */
	private StringProperty captionProperity;

	/** совойство текста сообщения */
	private StringProperty textProperity;

	/** совойство иконки сообщения */
	private StringProperty iconProperity;

	/** Ширина экрана */
	private double windowWidth;

	/** Задача */
	private Task task;

	@Override
	protected void initModel() {
		captionProperity = new SimpleStringProperty(this, "captionProperity", "caption");
		textProperity = new SimpleStringProperty(this, "textProperity", "text");
		iconProperity = new SimpleStringProperty(this, "iconProperity", "icon");
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public StringProperty getCaptionProperity() {
		return captionProperity;
	}

	public StringProperty getTextProperity() {
		return textProperity;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

	public double getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
	}

}
