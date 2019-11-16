package ru.devazz.model.dialogmodel;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.DefaultTaskEntity;
import ru.siencesquad.hqtasks.ui.entities.DefaultTask;
import ru.siencesquad.hqtasks.ui.model.PresentationModel;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;

public class DefaultTaskDialogModel extends PresentationModel<TaskServiceRemote, DefaultTaskEntity> {

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
		for (DefaultTaskEntity defaultTaskEntity : super.getService().getDefaultTaskAll()) {
			defaultTaskList.add(
					EntityConverter.getInstatnce().convertDefaultTaskEntityToClientWrapDefaultTask(defaultTaskEntity));
		}
	}

	/**
	 * Загрузка типовых задач по SUID боевого поста
	 *
	 * @param SUID - SUID боевого поста
	 * @throws Exception
	 */
	public void loadDefaultTasksBySub(Long SUID) throws Exception {
		defaultTaskList.clear();
		for (DefaultTaskEntity defaultTaskEntity : super.getService().getDefaultTaskBySub(SUID)) {
			defaultTaskList.add(
					EntityConverter.getInstatnce().convertDefaultTaskEntityToClientWrapDefaultTask(defaultTaskEntity));
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
	public Class<TaskServiceRemote> getTypeService() {
		return TaskServiceRemote.class;
	}

}
