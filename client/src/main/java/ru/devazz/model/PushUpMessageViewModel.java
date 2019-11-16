package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.siencesquad.hqtasks.ui.entities.Task;

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

	/**
	 * Возвращает {@link#captionProperity}
	 *
	 * @return the {@link#captionProperity}
	 */
	public StringProperty getCaptionProperity() {
		return captionProperity;
	}

	/**
	 * Устанавливает значение полю {@link#captionProperity}
	 *
	 * @param captionProperity значение поля
	 */
	public void setCaptionProperity(StringProperty captionProperity) {
		this.captionProperity = captionProperity;
	}

	/**
	 * Возвращает {@link#textProperity}
	 *
	 * @return the {@link#textProperity}
	 */
	public StringProperty getTextProperity() {
		return textProperity;
	}

	/**
	 * Устанавливает значение полю {@link#textProperity}
	 *
	 * @param textProperity значение поля
	 */
	public void setTextProperity(StringProperty textProperity) {
		this.textProperity = textProperity;
	}

	/**
	 * Возвращает {@link#iconProperity}
	 *
	 * @return the {@link#iconProperity}
	 */
	public StringProperty getIconProperity() {
		return iconProperity;
	}

	/**
	 * Устанавливает значение полю {@link#iconProperity}
	 *
	 * @param iconProperity значение поля
	 */
	public void setIconProperity(StringProperty iconProperity) {
		this.iconProperity = iconProperity;
	}

	/**
	 * Возвращает {@link#task}
	 *
	 * @return the {@link#task}
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Устанавливает значение полю {@link#task}
	 *
	 * @param task значение поля
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

	/**
	 * @return the windowWidth
	 */
	public double getWindowWidth() {
		return windowWidth;
	}

	/**
	 * @param windowWidth the windowWidth to set
	 */
	public void setWindowWidth(double windowWidth) {
		this.windowWidth = windowWidth;
	}

}
