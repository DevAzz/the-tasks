package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.TaskEntity;
import ru.sciencesquad.hqtasks.server.utils.TaskStatus;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;

import java.io.File;

/**
 * Модель представления диалога завершения задачи
 */
public class TaskCompletionDialogViewModel
		extends PresentationModel<TaskServiceRemote, TaskEntity> {

	/** Свойство текста поля наименование задачи */
	private StringProperty nameLabelProperty;

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
		documentStringProperty = new SimpleStringProperty(this, "documentStringProperty", "");

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
		if (!documentString.isEmpty()) {
			File file = new File(documentStringProperty.get());
			task.setDocumentName(file.getName());
			task.setDocument(file);
		}
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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<TaskServiceRemote> getTypeService() {
		return TaskServiceRemote.class;
	}

}
