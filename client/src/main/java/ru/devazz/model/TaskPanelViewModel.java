package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

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

	@Override
	protected void initModel() {
		titleLabelProperty = new SimpleStringProperty(this, "titleLabelProperty", "Заголовок");
		noteLabelProperty = new SimpleStringProperty(this, "noteLabelProperty", "Описание");
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "Дата");
		authorLabelProperty = new SimpleStringProperty(this, "authorLabelProperty", "Автор задачи");
	}

	@Override
	protected String getQueueName() {
		return null;
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

		DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

		String date = "Дата начала: " + parser.format(task.getStartedDate()) + "      ";
		setDateLabelPropertyValue(date);
	}

	public StringProperty getTitleLabelProperty() {
		return titleLabelProperty;
	}

	private void setTitleLabelText(String titleLabelText) {
		this.titleLabelProperty.set(titleLabelText);
	}

	public StringProperty getNoteLabelProperty() {
		return noteLabelProperty;
	}

	private void setNoteLabelProperty(String noteLabelText) {
		this.noteLabelProperty.set(noteLabelText);
	}

	public StringProperty getDateLabelProperty() {
		return dateLabelProperty;
	}

	private void setDateLabelPropertyValue(String dateLabelPropertyValue) {
		this.dateLabelProperty.set(dateLabelPropertyValue);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	public StringProperty getAuthorLabelProperty() {
		return authorLabelProperty;
	}

	private void setAuthorLabelValue(String authorLabelValue) {
		this.authorLabelProperty.set(authorLabelValue);
	}

}
