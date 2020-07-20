package ru.devazz.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import ru.devazz.entities.Task;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.TaskEvent;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.server.api.model.enums.TasksViewType;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.PushUpTypes;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;
import ru.devazz.view.RootView;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Модель представления рабочего стола
 */
public class WorkbenchViewModel extends PresentationModel<ITaskService, TaskModel> {

	/** Свойство текста заголовка */
	private StringProperty titleProperty;

	/** Слушатель перехода к задаче */
	private RootView.GoOverTaskListener goOverTaskListener;

	/** Идентификатор должности, выбранной в дереве подчиненности */
	private Long positionSuid;

	/**
	 * Карта соответствия типов панелей задач и моделей представления панелей задач
	 */
	private Map<TasksViewType, TasksViewModel> modelsMap = new HashMap<>();

	@Override
	protected void initModel() {
		titleProperty = new SimpleStringProperty(this, "titleProperty", "Задачи: ");
	}

	public StringProperty getTitleProperty() {
		return titleProperty;
	}

	public void setTitleValue(String aValue) {
		titleProperty.set(aValue);
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	/**
	 * Добавляет модель представления панели задач в карту
	 *
	 * @param aType тип панели
	 * @param aModel модель
	 */
	public void putModel(TasksViewType aType, TasksViewModel aModel) {
		modelsMap.put(aType, aModel);
	}

	/**
	 * Принудительно обновляет представление панели задач
	 *
	 * @param aViewTypes типы панелей задач
	 */
	private void refreshView(String aEventType, TaskModel aTask, TasksViewType... aViewTypes) {
		for (TasksViewType viewType : aViewTypes) {
			TasksViewModel viewModel = modelsMap.get(viewType);
			if (null != viewModel) {
				viewModel.updateTaskList(aEventType, aTask);
			}
		}

	}

	public void setGoOverTaskListener(RootView.GoOverTaskListener goOverTaskListener) {
		this.goOverTaskListener = goOverTaskListener;
	}

	/**
	 * Подключение к службе рассылки системных JMS сообщений
	 */
	public void connectToJMSService() {
		ProxyFactory.getInstance().addMessageListener("workbanchViewModel", "tasksQueue", message -> {
			try {
				if (message instanceof ActiveMQMessage) {
					UserModel user = Utils.getInstance().getCurrentUser();
					ActiveMQMessage objectMessage = (ActiveMQMessage) message;
					if (objectMessage instanceof ActiveMQTextMessage) {
						String text = ((ActiveMQTextMessage) objectMessage).getText();
						TaskEvent event = new ObjectMapper().readValue(text, TaskEvent.class);
						IEntity entity = event.getEntity();
						if (entity instanceof TaskModel) {
							TaskModel taskEntity = (TaskModel) entity;
							Task task = EntityConverter.getInstatnce()
									.convertTaskModelToClientWrapTask(taskEntity);

							refreshView(event.getType(), taskEntity, TasksViewType.values());
							switch (event.getType()) {
							case "created":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Назначение задачи",
											"Вам назначена новая задача: " + task.getName(),
																		 PushUpTypes.NEW_PUSH, goOverTaskListener);
								}

								break;
							case "time_left_over":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Истечение времени задачи",
											"Время, отведенное на Задачу " + task.getName()
													+ " истекает!",
											PushUpTypes.TIME_LEFT_OVER_PUSH, goOverTaskListener);
								}
								break;
							case "overdue":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Истечение времени задачи",
											"Время, отведенное на Задачу " + task.getName()
													+ " истекло.",
											PushUpTypes.OVERDUE_PUSH, goOverTaskListener);
								}
								break;
							case "deleted":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									DialogUtils.getInstance().showPushUp("Удаление задачи",
											"Задача " + task.getName() + " удалена.",
											PushUpTypes.DELETED_PUSH, goOverTaskListener);

								}
								break;
							case "done":
								if (task.getAuthor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Выполнение задачи",
											"Задача " + task.getName() + " выполнена",
											PushUpTypes.DONE_PUSH, goOverTaskListener);

								}
								break;
							case "rework":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Выполнение задачи",
											"Задача " + task.getName()
													+ " отправлена на доработку.",
											PushUpTypes.REWORK_PUSH, goOverTaskListener);

								}
								break;
							case "closed":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Принятие задачи",
											"Задача " + task.getName() + " принята.",
											PushUpTypes.CLOSED_PUSH, goOverTaskListener);
								}
								break;
							case "failed":
								if (task.getExecutor().getSuid().equals(user.getPositionSuid())) {
									goOverTaskListener.setTask(task);
									DialogUtils.getInstance().showPushUp("Выполнение",
											"Задача " + task.getName() + " отклонена.",
											PushUpTypes.FAILED_PUSH, goOverTaskListener);
								}
								break;
							case "updated":
								break;
							}

						}
					}
				}
			} catch (Exception e) {
				// TODO логирование
				e.printStackTrace();
			}
		});

	}

	public Long getPositionSuid() {
		return positionSuid;
	}

	public void setPositionSuid(Long positionSuid) {
		this.positionSuid = positionSuid;
	}

	@Override
	public void setOpenFlagValue(Boolean aFlag) {
		super.setOpenFlagValue(aFlag);
		for (Entry<TasksViewType, TasksViewModel> entry : modelsMap.entrySet()) {
			entry.getValue().setOpenFlagValue(aFlag);
		}
	}

}
