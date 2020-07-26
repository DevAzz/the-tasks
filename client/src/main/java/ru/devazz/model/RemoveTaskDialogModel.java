
package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;

/**
 * Модель представления диалога удаления задачи
 */
public class RemoveTaskDialogModel extends PresentationModel<ITaskService, TaskModel> {

	/** Удаляемая задча */
	private Task task;

	/** Текстовое свойство наименования задачи */
	private StringProperty nameTask;

	@Override
	protected void initModel() {
		nameTask = new SimpleStringProperty(this, "nameTask", "");
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
		setNameTaskValue(task.getName());
	}

	public StringProperty getNameTaskProperty() {
		return nameTask;
	}

	private void setNameTaskValue(String nameTaskValue) {
		this.nameTask.set(nameTaskValue);
	}

	/**
	 * Удаление задачи
	 *
	 */
	public void removeTask() {
		service.delete(task.getSuid(), true);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

}
