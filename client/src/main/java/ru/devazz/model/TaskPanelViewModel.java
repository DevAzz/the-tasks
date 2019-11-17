package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.TaskStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Модель представления панели задачи
 */
public class TaskPanelViewModel extends PresentationModel<ITaskService, TaskModel> {

	/** Модель данных панели задачи */
	private Task task;

	/** Свойство текста лейбла заголовка */
	private StringProperty titleLabelProperty;

	/** Свойство текста лейбла описания */
	private StringProperty noteLabelProperty;

	/** Свойство текста даты задачи */
	private StringProperty dateLabelProperty;

	/** Свойство текста автора задачи */
	private StringProperty authorLabelProperty;

	/** Статус задачи */
	private TaskStatus taskStatus;

	@Override
	protected void initModel() {
		titleLabelProperty = new SimpleStringProperty(this, "titleLabelProperty", "Заголовок");
		noteLabelProperty = new SimpleStringProperty(this, "noteLabelProperty", "Описание");
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "Дата");
		authorLabelProperty = new SimpleStringProperty(this, "authorLabelProperty", "Автор задачи");
	}

	/**
	 * Возвращает {@link #task}
	 *
	 * @return {@link #task}
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Устанавливает значение полю {@link #task}
	 *
	 * @param task значение поля
	 * @throws ParseException в случае ошибки парсинга даты
	 */
	public void setTask(Task task) throws ParseException {
		this.task = task;

		setNoteLabelProperty(task.getNote());
		setTitleLabelText(task.getName());
		setAuthorLabelValue((null != task.getAuthor()) ? task.getAuthor().getName() : "");

		SimpleDateFormat parser = new SimpleDateFormat("HH:mm dd.MM.yyyy");

		String date = "Дата начала: " + parser.format(task.getStartedDate()) + "      ";
		setDateLabelPropertyValue(date);
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
	 * Устанавливает значение полю {@link#titleLabelProperty}
	 *
	 * @param {@link#titleLabelProperty}
	 */
	public void setTitleLabelText(String titleLabelText) {
		this.titleLabelProperty.set(titleLabelText);
	}

	/**
	 * Возвращает {@link#noteLabelProperty}
	 *
	 * @return the {@link#noteLabelProperty}
	 */
	public StringProperty getNoteLabelProperty() {
		return noteLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#noteLabelProperty}
	 *
	 * @param {@link#noteLabelProperty}
	 */
	public void setNoteLabelProperty(String noteLabelText) {
		this.noteLabelProperty.set(noteLabelText);
	}

	/**
	 * Возвращает {@link#taskStatus}
	 *
	 * @return the {@link#taskStatus}
	 */
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	/**
	 * Устанавливает значение полю {@link#taskStatus}
	 *
	 * @param {@link#taskStatus}
	 */
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
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

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	/**
	 * Возвращает {@link#authorLabelProperty}
	 *
	 * @return the {@link#authorLabelProperty}
	 */
	public StringProperty getAuthorLabelProperty() {
		return authorLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#authorLabelProperty}
	 *
	 * @param authorLabelValue значение поля
	 */
	public void setAuthorLabelValue(String authorLabelValue) {
		this.authorLabelProperty.set(authorLabelValue);
	}

}
