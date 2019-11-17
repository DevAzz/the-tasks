package ru.devazz.model.dialogmodel;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.DefaultTask;
import ru.devazz.model.PresentationModel;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.DefaultTaskModel;
import ru.devazz.utils.EntityConverter;

public class DefaultTaskDialogModel extends PresentationModel<ITaskService,
		DefaultTaskModel> {

	/** Список типовых задач */
	private ObservableList<DefaultTask> defaultTaskList;

	/** Проперити списка типовых задач */
	private SimpleListProperty<DefaultTask> defaultTaskListProperity;

	/** Сущьность типовой задачи */
	private DefaultTask selectedDefaultTask;

	@Override
	protected void initModel() {
		// TODO Auto-generated method stub
		defaultTaskList = FXCollections.observableArrayList();
		defaultTaskListProperity = new SimpleListProperty<>(defaultTaskList);
	}

	/**
	 * Загрузка всех типовых задач
	 *
	 * @throws Exception
	 */
	public void loadAllDefaultTasks() throws Exception {
		defaultTaskList.clear();
		for (DefaultTaskModel defaultTaskEntity : super.getService().getDefaultTaskAll()) {
			defaultTaskList.add(
					EntityConverter
							.getInstatnce().convertDefaultTaskModelToClientWrapDefaultTask(defaultTaskEntity));
		}
	}

	/**
	 * Загрузка типовых задач по SUID должности
	 *
	 * @param SUID - SUID должности
	 * @throws Exception
	 */
	public void loadDefaultTasksBySub(Long SUID) throws Exception {
		defaultTaskList.clear();
		for (DefaultTaskModel defaultTaskEntity : super.getService().getDefaultTaskBySub(SUID)) {
			defaultTaskList.add(
					EntityConverter.getInstatnce().convertDefaultTaskModelToClientWrapDefaultTask(defaultTaskEntity));
		}
	}

	/**
	 * Возвращяет DefaultTaskProperty
	 *
	 * @return DefaultTaskProperty
	 */
	public SimpleListProperty<DefaultTask> getDefaultTaskListProperity() {
		return defaultTaskListProperity;
	}

	/**
	 * @return the selectedDefaultTask
	 */
	public DefaultTask getSelectedDefaultTask() {
		return selectedDefaultTask;
	}

	/**
	 * @param selectedDefaultTask the selectedDefaultTask to set
	 */
	public void setSelectedDefaultTask(DefaultTask selectedDefaultTask) {
		this.selectedDefaultTask = selectedDefaultTask;
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

}
