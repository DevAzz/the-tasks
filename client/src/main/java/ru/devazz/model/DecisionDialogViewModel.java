package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.TaskType;
import ru.devazz.utils.EntityConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Модель представления диалога принятия решения
 */
public class DecisionDialogViewModel extends PresentationModel<ITaskService, TaskModel> {

	/** Свойство текста поля наименование задачи */
	private StringProperty nameLabelProperty;

	/** Свойство текста лейбла примечания */
	private StringProperty noteLabelProperty;

	/** Свойство текста даты завершения */
	private ObjectProperty<LocalDateTime> endDateProperty;

	/** Свойство текста поля файл */
	private StringProperty documentStringProperty;

	/** Задача */
	private Task task;

	@Override
	protected void initModel() {
		nameLabelProperty = new SimpleStringProperty(this, "nameLabelProperty", "");
		noteLabelProperty = new SimpleStringProperty(this, "noteLabelProperty", "");
		endDateProperty = new SimpleObjectProperty<>(this, "endDateProperty", null);
		documentStringProperty = new SimpleStringProperty(this, "documentStringProperty", "");

		nameLabelProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				task.setName(newValue);
			}

		});
		noteLabelProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				task.setNote(newValue);
			}

		});
		endDateProperty
				.addListener((observable, oldValue, newValue) -> {
					if (null != newValue) {
						task.setEndDateTime(
								Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
					}
				});
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public StringProperty getNameLabelProperty() {
		return nameLabelProperty;
	}

	private void setNameLabelValue(String nameLabelValue) {
		this.nameLabelProperty.set(nameLabelValue);
	}

	public StringProperty getNoteLabelProperty() {
		return noteLabelProperty;
	}

	private void setNoteLabelValue(String noteLabelValue) {
		this.noteLabelProperty.set(noteLabelValue);
	}

	public ObjectProperty<LocalDateTime> getEndDateProperty() {
		return endDateProperty;
	}

	private void setEndDateValue(LocalDateTime endDateValue) {
		this.endDateProperty.set(endDateValue);
	}

	public StringProperty getDocumentStringProperty() {
		return documentStringProperty;
	}

	private void setDocumentStringPropertyValue(String documentString) {
		this.documentStringProperty.set(documentString);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		if (null != task) {
			this.task = task;

			String name = (null != task.getName()) ? task.getName() : "";
			String note = (null != task.getNote()) ? task.getNote() : "";
			Date endDate = task.getEndDateTime();
			String path = (null != task.getDocument()) ? task.getDocumentName() : "";

			setNameLabelValue(name);
			setNoteLabelValue(note);
			if (null != endDate) {
				setEndDateValue(
						LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault()));
			}
			setDocumentStringPropertyValue(path);
		}
	}

	/**
	 * Вовзращает представление даты завершения задачи в виде {@link LocalDateTime}
	 *
	 * @return представление даты завершения задачи
	 */
	public LocalDateTime getTaskEndLocalDateTime() {
		return LocalDateTime.ofInstant(task.getEndDateTime().toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Отклоняет задачу
	 *
	 * @throws Exception в случае ошибки
	 */
	public void rejectTask() throws Exception {
		if (null != task) {
			task.setStatus(TaskStatus.FAILD);
			task.setType(TaskType.ARCHIVAL);
			getService().update(
					EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task), true);
		}
	}

	/**
	 * Принимает задачу
	 *
	 * @throws Exception в случае ошибки
	 */
	public void acceptTask() throws Exception {
		if (null != task) {
			task.setStatus(TaskStatus.CLOSED);
			task.setType(TaskType.ARCHIVAL);
			getService().update(
					EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task), true);
		}
	}

	/**
	 * Отправляет задачу на доработку
	 *
	 * @throws Exception в случае ошибки
	 */
	public void sendToReworkTask() throws Exception {
		if (null != task) {
			task.setStatus(TaskStatus.REWORK);
			getService().update(
					EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task), true);
		}
	}

}
