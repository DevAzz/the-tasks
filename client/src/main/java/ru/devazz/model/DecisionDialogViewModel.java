package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.TaskEntity;
import ru.sciencesquad.hqtasks.server.utils.TaskStatus;
import ru.sciencesquad.hqtasks.server.utils.TaskType;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Модель представления диалога принятия решения
 */
public class DecisionDialogViewModel extends PresentationModel<TaskServiceRemote, TaskEntity> {

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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		nameLabelProperty = new SimpleStringProperty(this, "nameLabelProperty", "");
		noteLabelProperty = new SimpleStringProperty(this, "noteLabelProperty", "");
		endDateProperty = new SimpleObjectProperty<>(this, "endDateProperty", null);
		documentStringProperty = new SimpleStringProperty(this, "documentStringProperty", "");

		nameLabelProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				task.setName(newValue);
			}

		});
		noteLabelProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				task.setNote(newValue);
			}

		});
		endDateProperty
				.addListener((ChangeListener<LocalDateTime>) (observable, oldValue, newValue) -> {
					if (null != newValue) {
						task.setEndDateTime(
								Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
					}
				});
	}

	/**
	 * Возвращает {@link#nameLabelProperty}
	 *
	 * @return the {@link#nameLabelProperty}
	 */
	public StringProperty getNameLabelProperty() {
		return nameLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#nameLabelProperty}
	 *
	 * @param nameLabelValue значение поля
	 */
	public void setNameLabelValue(String nameLabelValue) {
		this.nameLabelProperty.set(nameLabelValue);
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
	 * @param noteLabelValue значение поля
	 */
	public void setNoteLabelValue(String noteLabelValue) {
		this.noteLabelProperty.set(noteLabelValue);
	}

	/**
	 * Возвращает {@link#endDateProperty}
	 *
	 * @return the {@link#endDateProperty}
	 */
	public ObjectProperty<LocalDateTime> getEndDateProperty() {
		return endDateProperty;
	}

	/**
	 * Устанавливает значение полю {@link#endDateProperty}
	 *
	 * @param endDateValue значение поля
	 */
	public void setEndDateValue(LocalDateTime endDateValue) {
		this.endDateProperty.set(endDateValue);
	}

	/**
	 * Возвращает {@link#documentStringProperty}
	 *
	 * @return the {@link#documentStringProperty}
	 */
	public StringProperty getDocumentStringProperty() {
		return documentStringProperty;
	}

	/**
	 * Устанавливает значение полю {@link#documentStringProperty}
	 *
	 * @param documentStringProperty значение поля
	 */
	public void setDocumentStringPropertyValue(String documentString) {
		this.documentStringProperty.set(documentString);
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<TaskServiceRemote> getTypeService() {
		return TaskServiceRemote.class;
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
