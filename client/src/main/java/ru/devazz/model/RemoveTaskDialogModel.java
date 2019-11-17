
package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.TaskModel;

import javax.jms.JMSException;

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
		setNameTaskValue(task.getName());
	}

	/**
	 * Возвращает {@link#nameTask}
	 *
	 * @return the {@link#nameTask}
	 */
	public StringProperty getNameTaskProperty() {
		return nameTask;
	}

	/**
	 * Устанавливает значение полю {@link#nameTask}
	 *
	 * @param nameTaskValue значение поля
	 */
	public void setNameTaskValue(String nameTaskValue) {
		this.nameTask.set(nameTaskValue);
	}

	/**
	 * Удаление задачи
	 *
	 * @throws JMSException в случае ошибки рассылки сообщений
	 */
	public void removeTask() throws JMSException {
		service.delete(task.getSuid(), true);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

}
