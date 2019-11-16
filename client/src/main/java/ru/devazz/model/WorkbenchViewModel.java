package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.activemq.artemis.jms.client.ActiveMQMessage;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.TaskEntity;
import ru.sciencesquad.hqtasks.server.datamodel.UserEntity;
import ru.sciencesquad.hqtasks.server.events.ObjectEvent;
import ru.sciencesquad.hqtasks.server.utils.TasksViewType;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.PushUpTypes;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;
import ru.siencesquad.hqtasks.ui.view.RootView.GoOverTaskListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Модель представления рабочего стола
 */
public class WorkbenchViewModel extends PresentationModel<TaskServiceRemote, TaskEntity> {

	/** Свойство текста заголовка */
	private StringProperty titleProperty;

	/** Слушатель перехода к задаче */
	private GoOverTaskListener goOverTaskListener;

	/** Идентификатор должности, выбранной в дереве подчиненности */
	private Long positionSuid;

	/**
	 * Карта соответствия типов панелей задач и моделей представления панелей задач
	 */
	private Map<TasksViewType, TasksViewModel> modelsMap = new HashMap<>();

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		titleProperty = new SimpleStringProperty(this, "titleProperty", "Задачи: ");
	}

	/**
	 * Возвращает {@link #titleProperty}
	 *
	 * @return {@link #titleProperty}
	 */
	public StringProperty getTitleProperty() {
		return titleProperty;
	}

	public void setTitleProperty(StringProperty titleProperty) {
		this.titleProperty = titleProperty;
	}

	public void setTitleValue(String aValue) {
		titleProperty.set(aValue);
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<TaskServiceRemote> getTypeService() {
		return TaskServiceRemote.class;
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
	public void refreshView(String aEventType, TaskEntity aTask, TasksViewType... aViewTypes) {
		for (TasksViewType viewType : aViewTypes) {
			TasksViewModel viewModel = modelsMap.get(viewType);
			if (null != viewModel) {
				viewModel.updateTaskList(aEventType, aTask);
			}
		}

	}

	/**
	 * Устанавливает значение полю {@link#goOverTaskListener}
	 *
	 * @param goOverTaskListener значение поля
	 */
	public void setGoOverTaskListener(GoOverTaskListener goOverTaskListener) {
		this.goOverTaskListener = goOverTaskListener;
	}

	/**
	 * Подключение к службе рассылки системных JMS сообщений
	 */
	public void connectToJMSService() {
		EJBProxyFactory.getInstance().addMessageListener(message -> {
			try {
				if (message instanceof ActiveMQMessage) {
					UserEntity user = Utils.getInstance().getCurrentUser();
					ActiveMQMessage objectMessage = (ActiveMQMessage) message;
					if (objectMessage.isBodyAssignableTo(ObjectEvent.class)) {
						ObjectEvent event = objectMessage.getBody(ObjectEvent.class);
						IEntity entity = event.getEntity();
						if (entity instanceof TaskEntity) {
							TaskEntity taskEntity = (TaskEntity) entity;
							Task task = EntityConverter.getInstatnce()
									.convertTaskEntityToClientWrapTask(taskEntity);

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

	/**
	 * Возвращает {@link#positionSuid}
	 *
	 * @return the {@link#positionSuid}
	 */
	public Long getPositionSuid() {
		return positionSuid;
	}

	/**
	 * Устанавливает значение полю {@link#positionSuid}
	 *
	 * @param positionSuid значение поля
	 */
	public void setPositionSuid(Long positionSuid) {
		this.positionSuid = positionSuid;
	}

	/**
	 * Устанавливает значение полю {@link#openViewFlag}
	 *
	 * @param aFlag значение поля
	 */
	@Override
	public void setOpenFlagValue(Boolean aFlag) {
		super.setOpenFlagValue(aFlag);
		for (Entry<TasksViewType, TasksViewModel> entry : modelsMap.entrySet()) {
			entry.getValue().setOpenFlagValue(aFlag);
		}
	}

}
