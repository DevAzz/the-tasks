package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.utils.EntityConverter;

import java.io.File;

/**
 * Модель представления диалога завершения задачи
 */
public class TaskCompletionDialogViewModel
		extends PresentationModel<ITaskService, TaskModel> {

	/** Свойство текста поля наименование задачи */
	private StringProperty nameLabelProperty;

	/** Свойство текста поля файл */
	private StringProperty documentStringProperty;

	/** Задача */
	private Task task;

	@Override
	protected void initModel() {
		nameLabelProperty = new SimpleStringProperty(this, "nameLabelProperty", "");
		documentStringProperty = new SimpleStringProperty(this, "documentStringProperty", "");

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

	public StringProperty getDocumentStringProperty() {
		return documentStringProperty;
	}

	public void setDocumentStringPropertyValue(String documentString) {
		this.documentStringProperty.set(documentString);
		if (!documentString.isEmpty()) {
			File file = new File(documentStringProperty.get());
			task.setDocumentName(file.getName());
			task.setDocument(file);
		}
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		if (null != task) {
			this.task = task;

			String name = (null != task.getName()) ? task.getName() : "";
			String path = (null != task.getDocument()) ? task.getDocumentName() : "";

			setNameLabelValue(name);
			setDocumentStringPropertyValue(path);
		}
	}

	/**
	 * Выполнеят завершение задачи
	 *
	 * @throws Exception в случае ошибки
	 */
	public void completeTask() throws Exception {
		task.setStatus(TaskStatus.DONE);
		getService().update(EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task),
							true);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

}
