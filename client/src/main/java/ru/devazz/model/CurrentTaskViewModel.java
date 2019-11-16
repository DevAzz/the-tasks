package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert.AlertType;
import org.apache.activemq.artemis.jms.client.ActiveMQMessage;
import ru.devazz.entities.Task;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.TaskEntity;
import ru.sciencesquad.hqtasks.server.events.ObjectEvent;
import ru.sciencesquad.hqtasks.server.utils.TaskPriority;
import ru.sciencesquad.hqtasks.server.utils.TaskStatus;
import ru.sciencesquad.hqtasks.server.utils.TaskType;
import ru.siencesquad.hqtasks.ui.entities.DefaultTask;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Модель представления конкретной задачи
 */
public class CurrentTaskViewModel extends PresentationModel<TaskServiceRemote, TaskEntity> {

	/** Модель данных панели задачи */
	private Task task;

	/** Свойство текста лейбла заголовка */
	private StringProperty titleLabelProperty;

	/** Свойство текста лейбла статуса */
	private StringProperty statusLabelProperty;

	/** Свойство текста комбо выбора приоритета */
	private StringProperty priorityLabelProperty;

	/** Свойство текста лейбла примечания */
	private StringProperty noteLabelProperty;

	/** Свойство текста описания */
	private StringProperty desciprtionLabelProperty;

	/** Свойство прогресса задачи */
	private DoubleProperty progressProperty;

	/** Флаг создания задачи */
	private BooleanProperty createTaskFlag;

	/** Свойство, со значением обратным флагу сздания задачи */
	private BooleanProperty reverseCreateFlag;

	/** Свойство состояния доступности создания или завершения задачи */
	private BooleanProperty okButtonDisabled;

	/** Свойство текста даты начала */
	private ObjectProperty<LocalDateTime> startDateProperty;

	/** Свойство текста даты завершения */
	private ObjectProperty<LocalDateTime> endDateProperty;

	/** Свойство текста поля файл */
	private StringProperty documentStringProperty;

	/** Свойство текста поля исполнитель */
	private StringProperty executorStringProperty;

	/** Флаг редактирования задачи */
	private BooleanProperty editModeProperty;

	/** Флаг наличия изменений */
	private BooleanProperty changeExistProperty;

	/** Свойство текста кнопки принятия */
	private StringProperty okButtonTextProperty;

	/** Свойство текста кнопки режима редактирования */
	private StringProperty editModeButtonTextProperty;

	/** Свойство видимости кнопки принятия */
	private BooleanProperty visibleOkButtonProperty;

	/** Свойство видимости кнопки перехода в режим редактирования */
	private BooleanProperty visibleEditModeButtonProperty;

	/** Свойство видимости кнопки принятия решения */
	private BooleanProperty visibleDecisionButtonProperty;

	/** Свойство видимости кнопки выбора типовых задач */
	private BooleanProperty visibleDefaultTaskButtonProperty;

	/** Свойство видимости кнопки выбора исполнителя */
	private BooleanProperty visibleSelectExecutorButtonProperty;

	/** Текстовое свойство цвета прогресс бара */
	private ObjectProperty<PropegressBarColor> colorProgressBarProperty;

	/** Текстовое свойство текста наименования боевого поста автора задачи */
	private StringProperty authorTextProperty;

	/** Свойство текста подсказки индикатора прогресса */
	private StringProperty tooltipProgressBarTextProperty;

	/** Исполнитель задачи */
	private SubordinationElement subElExecutor;

	/** Модель представления формы управления циклическим назначением */
	private CycleTaskViewModel cycleModel;

	/** Удаленные задачи */
	private List<Task> deletedTasks = new ArrayList<>();

	/**
	 * Перечисление цветов индикатора прогресса
	 */
	public enum PropegressBarColor {

		RED, GREEN, YELLOW;

	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		titleLabelProperty = new SimpleStringProperty(this, "titleLabelProperty");
		statusLabelProperty = new SimpleStringProperty(this, "statusLabelProperty", "Статус");
		priorityLabelProperty = new SimpleStringProperty(this, "priorityLabelProperty",
				"Приоритет");
		desciprtionLabelProperty = new SimpleStringProperty(this, "desciprtionLabelProperty",
				"Описание задачи");
		noteLabelProperty = new SimpleStringProperty(this, "noteLabelProperty");
		progressProperty = new SimpleDoubleProperty(this, "progressProperty");
		createTaskFlag = new SimpleBooleanProperty(this, "createTaskFlag", false);
		reverseCreateFlag = new SimpleBooleanProperty(this, "reverseCreateFlag", true);
		okButtonDisabled = new SimpleBooleanProperty(this, "createOrFinishDisabled", false);
		startDateProperty = new SimpleObjectProperty<>(this, "startDateProperty");
		endDateProperty = new SimpleObjectProperty<>(this, "endDateProperty");
		documentStringProperty = new SimpleStringProperty(this, "documentStringProperty", "");
		executorStringProperty = new SimpleStringProperty(this, "executorStringProperty");
		changeExistProperty = new SimpleBooleanProperty(this, "changeExistProperty", false);
		createTaskFlag = new SimpleBooleanProperty(this, "createTaskFlag", true);
		editModeProperty = new SimpleBooleanProperty(this, "editModeProperty", false);
		okButtonTextProperty = new SimpleStringProperty(this, "okButtonTextProperty", "");
		visibleOkButtonProperty = new SimpleBooleanProperty(this, "visibleOkButtonProperty", false);
		editModeButtonTextProperty = new SimpleStringProperty(this, "editModeButtonTextProperty",
				"Режим редактирования");
		visibleEditModeButtonProperty = new SimpleBooleanProperty(this,
				"visibleEditModeButtonProperty", true);
		visibleDecisionButtonProperty = new SimpleBooleanProperty(this,
				"visibleDecisionButtonProperty", true);
		visibleDefaultTaskButtonProperty = new SimpleBooleanProperty(this,
				"visibleDefaultTaskButtonProperty", true);
		visibleSelectExecutorButtonProperty = new SimpleBooleanProperty(this,
				"visibleSelectExecutorButtonProperty", true);
		colorProgressBarProperty = new SimpleObjectProperty<>(this, "colorProgressBarProperty");
		authorTextProperty = new SimpleStringProperty(this, "authorTextProperty", "");
		tooltipProgressBarTextProperty = new SimpleStringProperty(this,
				"tooltipProgressBarTextProperty", "");

	}

	/**
	 * Добавляет слушатели изменения значений свойств основных параметров задачи
	 */
	public void addPropertyListeners() {
		if (createTaskFlag.get()) {
			titleLabelProperty
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						String exec = (null != getExecutorStringProperty().get())
								? getExecutorStringProperty().get()
								: "";
						setOkButtonDisabledValue(newValue.isEmpty()
								|| (null == getStartDateProperty().get())
								|| (null == getEndDateProperty().get()) || (exec.isEmpty()));
						task.setName(newValue);
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			noteLabelProperty
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						task.setNote(newValue);
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			startDateProperty.addListener(
					(ChangeListener<LocalDateTime>) (observable, oldValue, newValue) -> {
						String title = (null != getTitleLabelProperty().get())
								? getTitleLabelProperty().get()
								: "";
						String exec = (null != getExecutorStringProperty().get())
								? getExecutorStringProperty().get()
								: "";
						setOkButtonDisabledValue(
								(null == newValue) || (null == getEndDateProperty().get())
										|| (title.isEmpty()) || (exec.isEmpty()));
						if (null != newValue) {
							task.setStartDateTime(
									Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();

					});
			endDateProperty.addListener(
					(ChangeListener<LocalDateTime>) (observable, oldValue, newValue) -> {
						String title = (null != getTitleLabelProperty().get())
								? getTitleLabelProperty().get()
								: "";
						String exec = (null != getExecutorStringProperty().get())
								? getExecutorStringProperty().get()
								: "";
						setOkButtonDisabledValue(
								(null == newValue) || (null == getStartDateProperty().get())
										|| (title.isEmpty()) || (exec.isEmpty()));
						if (null != newValue) {
							task.setEndDateTime(
									Date.from(newValue.atZone(ZoneId.systemDefault()).toInstant()));
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			executorStringProperty
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						String title = (null != getTitleLabelProperty().get())
								? getTitleLabelProperty().get()
								: "";
						setOkButtonDisabledValue(newValue.isEmpty()
								|| (null == getStartDateProperty().get()) || (title.isEmpty())
								|| (null == getEndDateProperty().get()));
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			priorityLabelProperty
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						TaskPriority priority = TaskPriority.getPriorityBySuid(newValue);
						if (null != priority) {
							task.setPriority(priority);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			documentStringProperty
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						if (!newValue.isEmpty()) {
							File file = new File(documentStringProperty.get());
							task.setDocumentName(file.getName());
							task.setDocument(file);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
		}
	}

	/**
	 * Обновляет сущность задачи
	 */
	public void updateTaskEntity() {
		try {
			cycleModel.saveCyclicity();
			getService().update(
					EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task), true);
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Отменяет изменения
	 */
	public void revertChanges() {
		Thread thread = new Thread(() -> {
			TaskEntity entity;
			try {
				entity = EJBProxyFactory.getInstance().getService(TaskServiceRemote.class)
						.get(task.getSuid());
				Task originalTask = EntityConverter.getInstatnce()
						.convertTaskEntityToClientWrapTask(entity);
				setTask(originalTask);
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Создает задачу
	 *
	 * @throws ParseException в случае ошибки
	 */
	public void createTaskEntity() throws Exception {
		task.setStatus(TaskStatus.WORKING);
		if (null == task.getType()) {
			task.setType(TaskType.USUAL);
		}
		cycleModel.saveCyclicity();
		getService().add(EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task),
				true);
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
	 * Инициализирует модель данных
	 */
	public void initTaskModel() {
		if (null != task) {
			SubordinationElement currentUserSubEl = null;
			try {
				currentUserSubEl = Utils.getInstance().getCurrentUserSubEl();
			} catch (NamingException e) {
				// TODO Логирование
				e.printStackTrace();
			}

			String title = (null != task.getName()) ? task.getName() : "";
			String note = (null != task.getNote()) ? task.getNote() : "";
			String desc = (null != task.getDescription()) ? task.getDescription() : "";
			String status = (null != task.getStatus()) ? task.getStatus().getName()
					: "Не установлен";
			String priority = (null != task.getPriority()) ? task.getPriority().getMenuSuid()
					: "Не установлен";
			double percent = (null != task.getExecPercent()) ? task.getExecPercent() : 0;
			Date startDate = task.getStartDateTime();
			Date endDate = task.getEndDateTime();
			String executor = (null != task.getExecutor()) ? task.getExecutor().getName() : "";
			String author = (null != task.getAuthor()) ? task.getAuthor().getName()
					: ((null != currentUserSubEl) && getCreateFlagValue())
							? currentUserSubEl.getName()
							: "";
			String path = (null != task.getDocument()) ? task.getDocumentName() : "";

			getTitleLabelProperty().set(title);
			getNoteLabelProperty().set(note);
			getDesciprtionLabelProperty().set(desc);
			getStatusLabelProperty().set(status);
			getPriorityLabelProperty().set(priority);
			getProgressProperty().set(percent);
			if ((null != startDate) && (null != endDate)) {
				getStartDateProperty().set(
						LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()));
				getEndDateProperty()
						.set(LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault()));
			}
			getExecutorStringProperty().set(executor);
			setDocumentStringPropertyValue(path);
			setAuthorTextValue(author);
		}
	}

	/**
	 * Устанавливает значение полю {@link#task}
	 *
	 * @param {@link#task}
	 */
	public void setTask(Task aTask) {
		if (null != aTask) {
			this.task = aTask.copy();
			cycleModel.setTask(task);
			cycleModel.setEditModePropertyValue(getEditModeProperty().get());
		}
	}

	/**
	 * Возвращает {@link#titleLabelProperty}
	 *
	 * @return the {@link#titleLabelProperty}
	 */
	public StringProperty getTitleLabelProperty() {
		return titleLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#titleLabelProperty}
	 *
	 * @param {@link#titleLabelProperty}
	 */
	public void setTitleLabelProperty(StringProperty titleLabelProperty) {
		this.titleLabelProperty = titleLabelProperty;
	}

	/**
	 * Возвращает {@link#statusLabelProperty}
	 *
	 * @return the {@link#statusLabelProperty}
	 */
	public StringProperty getStatusLabelProperty() {
		return statusLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#statusLabelProperty}
	 *
	 * @param {@link#statusLabelProperty}
	 */
	public void setStatusLabelProperty(StringProperty statusLabelProperty) {
		this.statusLabelProperty = statusLabelProperty;
	}

	/**
	 * Возвращает {@link#priorityLabelProperty}
	 *
	 * @return the {@link#priorityLabelProperty}
	 */
	public StringProperty getPriorityLabelProperty() {
		return priorityLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#priorityLabelProperty}
	 *
	 * @param {@link#priorityLabelProperty}
	 */
	public void setPriorityLabelProperty(StringProperty priorityLabelProperty) {
		this.priorityLabelProperty = priorityLabelProperty;
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
	 * @param {@link#noteLabelProperty}
	 */
	public void setNoteLabelProperty(StringProperty noteLabelProperty) {
		this.noteLabelProperty = noteLabelProperty;
	}

	/**
	 * Возвращает {@link#desciprtionLabelProperty}
	 *
	 * @return the {@link#desciprtionLabelProperty}
	 */
	public StringProperty getDesciprtionLabelProperty() {
		return desciprtionLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#desciprtionLabelProperty}
	 *
	 * @param {@link#desciprtionLabelProperty}
	 */
	public void setDesciprtionLabelProperty(StringProperty desciprtionLabelProperty) {
		this.desciprtionLabelProperty = desciprtionLabelProperty;
	}

	/**
	 * Возвращает {@link#progressProperty}
	 *
	 * @return the {@link#progressProperty}
	 */
	public DoubleProperty getProgressProperty() {
		return progressProperty;
	}

	/**
	 * Устанавливает значение полю {@link#progressProperty}
	 *
	 * @param {@link#progressProperty}
	 */
	public void setProgressProperty(DoubleProperty progressProperty) {
		this.progressProperty = progressProperty;
	}

	/**
	 * Возвращает {@link#createTaskFlag}
	 *
	 * @return the {@link#createTaskFlag}
	 */
	public BooleanProperty getCreateTaskFlag() {
		return createTaskFlag;
	}

	/**
	 * Устанавливает значение полю {@link#createTaskFlag}
	 *
	 * @param aValue значение поля
	 */
	public void setCreateFlagValue(Boolean aValue) {
		createTaskFlag.setValue(aValue);
		reverseCreateFlag.setValue(!aValue);
		setVisibleDefaultTaskButtonValue(aValue);
		setVisibleSelectExecutorButtonValue(aValue);
		cycleModel.setEditModePropertyValue(aValue);
		if (aValue) {
			setVisibleOkButtonValue(true);
			setOkButtonTextValue("Создать");
			setVisibleDecisionButtonValue(false);

		} else {
			connectToJMSService();
			Boolean visibleOkButton = executorTaskCheck()
					&& (TaskStatus.REWORK.equals(task.getStatus())
							|| TaskStatus.WORKING.equals(task.getStatus())
							|| TaskStatus.OVERDUE.equals(task.getStatus()));
			setOkButtonTextValue("Завершить");
			setVisibleOkButtonValue(visibleOkButton);
			setVisibleDecisionButtonValue(
					authorTaskCheck() && TaskStatus.DONE.equals(task.getStatus()));
		}
		addPropertyListeners();

		ProgressUpdater updater = new ProgressUpdater();
		updater.updateProgress();
	}

	/**
	 * Возвращает значение поля {@link#createTaskFlag}
	 *
	 * @return значение поля {@link#createTaskFlag}
	 */
	public Boolean getCreateFlagValue() {
		return createTaskFlag.getValue();
	}

	/**
	 * Возвращает {@link#reverseCreateFlag}
	 *
	 * @return the {@link#reverseCreateFlag}
	 */
	public BooleanProperty getReverseCreateFlag() {
		return reverseCreateFlag;
	}

	/**
	 * Возвращает {@link#createOrFinishEnabled}
	 *
	 * @return the {@link#createOrFinishEnabled}
	 */
	public BooleanProperty getOkButtonDisabledProperty() {
		return okButtonDisabled;
	}

	/**
	 * Устанавливает значение полю {@link#createOrFinishEnabled}
	 *
	 * @param aCreateOrFinishDisabled значение поля
	 */
	public void setOkButtonDisabledValue(Boolean aCreateOrFinishDisabled) {
		okButtonDisabled.setValue(aCreateOrFinishDisabled);
	}

	/**
	 * Возвращает {@link#startDateProperty}
	 *
	 * @return the {@link#startDateProperty}
	 */
	public ObjectProperty<LocalDateTime> getStartDateProperty() {
		return startDateProperty;
	}

	/**
	 * Устанавливает значение полю {@link#startDateProperty}
	 *
	 * @param startDateProperty значение поля
	 */
	public void setStartDateProperty(ObjectProperty<LocalDateTime> startDateProperty) {
		this.startDateProperty = startDateProperty;
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
	 * @param endDateProperty значение поля
	 */
	public void setEndDateProperty(ObjectProperty<LocalDateTime> endDateProperty) {
		this.endDateProperty = endDateProperty;
	}

	/**
	 * Возвращает {@link#executorStringProperty}
	 *
	 * @return the {@link#executorStringProperty}
	 */
	public StringProperty getExecutorStringProperty() {
		return executorStringProperty;
	}

	/**
	 * Устанавливает значение полю {@link#executorStringProperty}
	 *
	 * @param executorStringProperty значение поля
	 */
	public void setExecutorStringProperty(StringProperty executorStringProperty) {
		this.executorStringProperty = executorStringProperty;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<TaskServiceRemote> getTypeService() {
		return TaskServiceRemote.class;
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
	 * Возвращает {@link#editTaskProperty}
	 *
	 * @return the {@link#editTaskProperty}
	 */
	public BooleanProperty getEditModeProperty() {
		return editModeProperty;
	}

	/**
	 * Устанавливает значение полю {@link#editTaskProperty}
	 *
	 * @param editTaskValue значение поля
	 */
	public void setEditModeValue(Boolean editTaskValue) {
		this.editModeProperty.set(editTaskValue);
		setVisibleOkButtonValue(editTaskValue);
		setCreateFlagValue(editTaskValue);
		cycleModel.setEditModePropertyValue(editTaskValue);
		if (editTaskValue) {
			setOkButtonTextValue("Сохранить");
			setEditModeButtonTextValue("Режим просмотра");
		} else {
			setEditModeButtonTextValue("Режим редактирования");
		}
	}

	/**
	 * Возвращает {@link#subElExecutor}
	 *
	 * @return the {@link#subElExecutor}
	 */
	public SubordinationElement getSubElExecutor() {
		return subElExecutor;
	}

	/**
	 * Устанавливает значение полю {@link#subElExecutor}
	 *
	 * @param subElExecutor значение поля
	 */
	public void setSubElExecutor(SubordinationElement subElExecutor) {
		try {
			if ((Utils.getInstance().getCurrentUserSubEl().getRoleSuid() >= subElExecutor
					.getRoleSuid())
					&& !isSubordinate(Utils.getInstance().getCurrentUserSubEl(), subElExecutor)) {
				DialogUtils.getInstance().showAlertDialog("Невозможно задать исполнителя",
						"Невозможно назначить исполнителем задачи выбранное должностное лицо",
						AlertType.WARNING);
			} else {
				this.subElExecutor = subElExecutor;
				getExecutorStringProperty().set(subElExecutor.getName());
				task.setExecutor(subElExecutor);
			}
		} catch (NamingException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Проверяет, является subElExecutor подчиненным aCurrentSubEl
	 *
	 * @param aCurrentSubEl текущий элемент подчиненности
	 * @param subElExecutor проверяемый элемент подчиненности
	 * @return {@code true} - в случае, если subElExecutor является подчиненным
	 *         aCurrentSubEl
	 */
	private boolean isSubordinate(SubordinationElement aCurrentSubEl,
			SubordinationElement subElExecutor) {
		boolean result = false;
		if (aCurrentSubEl.getChildren().contains(subElExecutor)) {
			result = true;
		} else {
			for (SubordinationElement element : aCurrentSubEl.getChildren()) {
				result = isSubordinate(element, subElExecutor);
				if (result) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Подключение к службе рассылки системных JMS сообщений
	 */
	public void connectToJMSService() {
		EJBProxyFactory.getInstance().addMessageListener(message -> {
			Platform.runLater(() -> {
				try {
					if (message instanceof ActiveMQMessage) {
						ActiveMQMessage objectMessage = (ActiveMQMessage) message;
						if (objectMessage.isBodyAssignableTo(ObjectEvent.class)) {
							ObjectEvent event = objectMessage.getBody(ObjectEvent.class);

							IEntity entity = event.getEntity();
							if (entity instanceof TaskEntity) {
								TaskEntity taskEntity = (TaskEntity) entity;
								Task task = EntityConverter.getInstatnce()
										.convertTaskEntityToClientWrapTask(taskEntity);
								if (task.getSuid().equals(this.task.getSuid())
										&& getOpenViewFlag().get()) {
									switch (event.getType()) {
									case "updated":
									case "done":
									case "rework":
									case "failed":
									case "closed":
									case "overdue":
										setTask(task);
										initTaskModel();
										break;
									case "deleted":
										if (!deletedTasks.contains(task)) {
											deletedTasks.add(task);
											setOpenFlagValue(false);
											DialogUtils.getInstance().showAlertDialog(
													"Задача удалена",
													"Задача " + task.getName() + " была удалена",
													AlertType.INFORMATION);
											setOpenFlagValue(false);
										}
										break;
									}

								}
							}
						}
					}
				} catch (JMSException e) {
					// TODO Логирование
					e.printStackTrace();
				}
			});

		});
	}

	/**
	 * Сравнивает оригинальный объект и текущую задачу
	 */
	private class ChangeExistCheckRunnable implements Runnable {

		/**
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			try {
				TaskEntity entity = EJBProxyFactory.getInstance()
						.getService(TaskServiceRemote.class).get(task.getSuid());
				Task originalTask = EntityConverter.getInstatnce()
						.convertTaskEntityToClientWrapTask(entity);
				if (null != originalTask) {
					setChangeExistValue(!originalTask.equals(task));
				}
			} catch (NamingException e) {
				// TODO Логирование
				e.printStackTrace();
			}

		}

	}

	/**
	 * Возвращает {@link#changeExistProperty}
	 *
	 * @return the {@link#changeExistProperty}
	 */
	public BooleanProperty getChangeExistProperty() {
		return changeExistProperty;
	}

	/**
	 * Устанавливает значение полю {@link#changeExistProperty}
	 *
	 * @param changeExistValue значение поля
	 */
	public void setChangeExistValue(Boolean changeExistValue) {
		this.changeExistProperty.set(changeExistValue);
	}

	/**
	 * Возвращает {@link#okButtonTextProperty}
	 *
	 * @return the {@link#okButtonTextProperty}
	 */
	public StringProperty getOkButtonTextProperty() {
		return okButtonTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#okButtonTextProperty}
	 *
	 * @param okButtonTextValue значение поля
	 */
	public void setOkButtonTextValue(String okButtonTextValue) {
		this.okButtonTextProperty.set(okButtonTextValue);
	}

	/**
	 * Возвращает {@link#visibleOkButtonProperty}
	 *
	 * @return the {@link#visibleOkButtonProperty}
	 */
	public BooleanProperty getVisibleOkButtonProperty() {
		return visibleOkButtonProperty;
	}

	/**
	 * Устанавливает значение полю {@link#visibleOkButtonProperty}
	 *
	 * @param visibleOkButtonValue значение поля
	 */
	public void setVisibleOkButtonValue(Boolean visibleOkButtonValue) {
		this.visibleOkButtonProperty.set(visibleOkButtonValue);
	}

	/**
	 * Возвращает {@link#editModeButtonTextProperty}
	 *
	 * @return the {@link#editModeButtonTextProperty}
	 */
	public StringProperty getEditModeButtonTextProperty() {
		return editModeButtonTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#editModeButtonTextProperty}
	 *
	 * @param editModeButtonTextValue значение поля
	 */
	public void setEditModeButtonTextValue(String editModeButtonTextValue) {
		this.editModeButtonTextProperty.set(editModeButtonTextValue);
	}

	/**
	 * Возвращает {@link#visibleEditModeButtonProperty}
	 *
	 * @return the {@link#visibleEditModeButtonProperty}
	 */
	public BooleanProperty getVisibleEditModeButtonProperty() {
		return visibleEditModeButtonProperty;
	}

	/**
	 * Устанавливает значение полю {@link#visibleEditModeButtonProperty}
	 *
	 * @param visibleEditModeButtonValue значение поля
	 */
	public void setVisibleEditModeButtonValue(Boolean visibleEditModeButtonValue) {
		this.visibleEditModeButtonProperty.set(visibleEditModeButtonValue);
	}

	/**
	 * Возвращает {@link#visibleDecisionButtonProperty}
	 *
	 * @return the {@link#visibleDecisionButtonProperty}
	 */
	public BooleanProperty getVisibleDecisionButtonProperty() {
		return visibleDecisionButtonProperty;
	}

	/**
	 * Устанавливает значение полю {@link#visibleDecisionButtonProperty}
	 *
	 * @param visibleDecisionButtonValue значение поля
	 */
	public void setVisibleDecisionButtonValue(Boolean visibleDecisionButtonValue) {
		this.visibleDecisionButtonProperty.set(visibleDecisionButtonValue);
	}

	/**
	 * Возвращает {@link#visibleDefaultTaskButtonProperty}
	 *
	 * @return the {@link#visibleDefaultTaskButtonProperty}
	 */
	public BooleanProperty getVisibleDefaultTaskButtonProperty() {
		return visibleDefaultTaskButtonProperty;
	}

	/**
	 * Устанавливает значение полю {@link#visibleDefaultTaskButtonProperty}
	 *
	 * @param visibleDefaultTaskButtonValue значение поля
	 */
	public void setVisibleDefaultTaskButtonValue(Boolean visibleDefaultTaskButtonValue) {
		this.visibleDefaultTaskButtonProperty.set(visibleDefaultTaskButtonValue);
	}

	/**
	 * Возвращает {@link#visibleSelectExecutorButtonProperty}
	 *
	 * @return the {@link#visibleSelectExecutorButtonProperty}
	 */
	public BooleanProperty getVisibleSelectExecutorButtonProperty() {
		return visibleSelectExecutorButtonProperty;
	}

	/**
	 * Устанавливает значение полю {@link#visibleSelectExecutorButtonProperty}
	 *
	 * @param visibleSelectExecutorButtonValue значение поля
	 */
	public void setVisibleSelectExecutorButtonValue(Boolean visibleSelectExecutorButtonValue) {
		this.visibleSelectExecutorButtonProperty.set(visibleSelectExecutorButtonValue);
	}

	/**
	 * Возвращает {@link#colorProgressBarTextProperty}
	 *
	 * @return the {@link#colorProgressBarTextProperty}
	 */
	public ObjectProperty<PropegressBarColor> getColorProgressBarTextProperty() {
		return colorProgressBarProperty;
	}

	/**
	 * Устанавливает значение полю {@link#colorProgressBarTextProperty}
	 *
	 * @param aColor значение поля
	 */
	public void setColorProgressBarTextValue(PropegressBarColor aColor) {
		Platform.runLater(() -> {
			this.colorProgressBarProperty.set(aColor);
		});
	}

	/**
	 * Возвращает {@link#authorTextProperty}
	 *
	 * @return the {@link#authorTextProperty}
	 */
	public StringProperty getAuthorTextProperty() {
		return authorTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#authorTextProperty}
	 *
	 * @param authorTextValue значение поля
	 */
	public void setAuthorTextValue(String authorTextValue) {
		this.authorTextProperty.set(authorTextValue);
	}

	/**
	 * Возвращает {@link#tooltipProgressBarTextProperty}
	 *
	 * @return the {@link#tooltipProgressBarTextProperty}
	 */
	public StringProperty getTooltipProgressBarTextProperty() {
		return tooltipProgressBarTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#tooltipProgressBarTextProperty}
	 *
	 * @param tooltipProgressBarTextValue значение поля
	 */
	public void setTooltipProgressBarTextValue(String tooltipProgressBarTextValue) {
		this.tooltipProgressBarTextProperty.set(tooltipProgressBarTextValue);
	}

	/**
	 * Проверка на исполнителя задачи.
	 *
	 * @return {@code true} - если текущий пользователь является исполнителем задачи
	 */
	public Boolean executorTaskCheck() {
		Long positionUserSuid = Utils.getInstance().getCurrentUser().getPositionSuid();
		return positionUserSuid.equals(task.getExecutor().getSuid());
	}

	/**
	 * Проверка на автора задачи.
	 *
	 * @return {@code true} - если текущий пользователь является автором задачи
	 */
	public Boolean authorTaskCheck() {
		Boolean result = false;
		if (null != task.getAuthor()) {
			Long positionUserSuid = Utils.getInstance().getCurrentUser().getPositionSuid();
			result = positionUserSuid.equals(task.getAuthor().getSuid());
		}
		return result;
	}

	/**
	 * Класс, который обновляет прогресс бар в зависимости от процента оставшегося
	 * времени
	 */
	private class ProgressUpdater {

		/**
		 * Рассчитывает процент времени выполнения и обновляет прогресс бар
		 */
		private void updateProgress() {
			if (!getCreateFlagValue() && !(TaskStatus.CLOSED.equals(task.getStatus())
					|| TaskStatus.FAILD.equals(task.getStatus())
					|| TaskStatus.DONE.equals(task.getStatus()))) {
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(1000L);
						long startDate = task.getStartDateTime().getTime();
						long endDate = task.getEndDateTime().getTime();
						Boolean exitCondition = new Date().getTime() <= (endDate - 10L);
						double diskret = ((endDate - startDate) / 10000);
						double sleepTime = diskret - 5;
						while (exitCondition) {
							startDate = task.getStartDateTime().getTime();
							endDate = task.getEndDateTime().getTime();
							double diskretNumber = (new Date().getTime() - startDate) / diskret;
							double result = 1 - (diskretNumber * 0.0001);
							progressProperty.set((result < 0.01) ? 0.01 : result);

							setProgressBarColor(result);
							if (!getOpenViewFlag().get()) {
								break;
							}
							Thread.sleep((0 > sleepTime) ? 0 : (long) sleepTime);
						}

						if (!exitCondition) {
							progressProperty.set(1);
							setColorProgressBarTextValue(PropegressBarColor.RED);
						}

					} catch (Exception e) {
						// TODO Логирование
						e.printStackTrace();
					}
				});

				thread.setDaemon(true);
				thread.start();
				Thread timeLeftThread = new Thread(() -> {
					try {
						Thread.sleep(1000L);
						while (true) {
							Thread.sleep(100L);
							Platform.runLater(
									() -> tooltipProgressBarTextProperty.set(getTimeLeft()));
							if (!getOpenViewFlag().get()) {
								break;
							}
							Thread.sleep(1000L);
						}
					} catch (Exception e) {
						// TODO Логирование
						e.printStackTrace();
					}
				});
				timeLeftThread.setDaemon(true);
				timeLeftThread.start();
			} else if (!getCreateFlagValue()) {
				switch (task.getStatus()) {
				case CLOSED:
				case DONE:
					progressProperty.set(1);
					setColorProgressBarTextValue(PropegressBarColor.GREEN);
					break;
				case FAILD:
					progressProperty.set(1);
					setColorProgressBarTextValue(PropegressBarColor.RED);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Типы временных едениц
	 */
	private enum TimeUnitEnum {

		DAY("дней", "день", "дня"),

		HOUR("часов", "час", "часа"),

		MINUTE("минут", "минута", "минуты"),

		SECOND("секунд", "секунда", "секунды");

		/** Массив форм еденицы */
		private String[] units;

		/**
		 * Конструктор
		 *
		 * @param units массив форм еденицы
		 */
		private TimeUnitEnum(String... units) {
			this.units = units;
		}

		/**
		 * Возвращает {@link#units}
		 *
		 * @return the {@link#units}
		 */
		public String[] getUnits() {
			return units;
		}

	}

	/**
	 * Возвращает текстовое представление еденицы времени заданного типа
	 *
	 * @param aCount количество юнитов
	 * @param aTimeUnit тип юнита
	 * @return текстовое представление
	 */
	private String getTimeUnitForm(int aCount, TimeUnitEnum aTimeUnit) {
		String result = "";
		if (aCount > 0) {
			int balance = aCount % 10;

			if (((aCount >= 5) && (aCount <= 20)) || (balance == 0)
					|| ((balance >= 5) && (balance <= 9))) {
				result += String.valueOf(aCount) + " " + aTimeUnit.getUnits()[0] + " ";
			} else if (balance == 1) {
				result += String.valueOf(aCount) + " " + aTimeUnit.getUnits()[1] + " ";
			} else {
				result += String.valueOf(aCount) + " " + aTimeUnit.getUnits()[2] + " ";
			}
		}
		return result;

	}

	/**
	 * Возвращает текстовое представление оставшегося времени
	 *
	 * @return текстовое представление оставшегося времени
	 */
	public String getTimeLeft() {
		String result = "Осталось времени: ";
		try {
			TaskStatus status = task.getStatus();
			if (new Date().getTime() < task.getStartDateTime().getTime()) {
				result = new String("Задача еще не началась");
			} else {
				if (!(TaskStatus.CLOSED.equals(status) || TaskStatus.FAILD.equals(status)
						|| TaskStatus.DONE.equals(status) || TaskStatus.OVERDUE.equals(status))) {
					long endDate = task.getEndDate().getTime();
					int secInt = (int) ((endDate - new Date().getTime()) / 1000);
					int minInt = secInt / 60;
					int hoursInt = minInt / 60;
					int daysInt = hoursInt / 24;

					result += getTimeUnitForm(daysInt, TimeUnitEnum.DAY);
					result += getTimeUnitForm(hoursInt - (24 * daysInt), TimeUnitEnum.HOUR);
					result += getTimeUnitForm(minInt - (60 * hoursInt), TimeUnitEnum.MINUTE);
					result += getTimeUnitForm(secInt - (60 * minInt), TimeUnitEnum.SECOND);
				} else if (!TaskStatus.OVERDUE.equals(status)) {
					result = new String("Задача завершена");
				} else {
					result = new String("Задача просрочена");
				}
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Осуществляет применение типовой задачи
	 *
	 * @param aTypedTask типовая задача
	 * @throws ParseException в случае ошибки парсинга
	 */
	public void selectTypedTask(DefaultTask aTypedTask) throws ParseException {
		if (null != aTypedTask) {
			SimpleDateFormat parser = new SimpleDateFormat("hh:mm");

			Calendar calendarTempStartDate = Calendar.getInstance();
			calendarTempStartDate.setTime(parser.parse(aTypedTask.getStartTime()));

			Calendar calendarTempEndDate = Calendar.getInstance();
			calendarTempEndDate.setTime(parser.parse(aTypedTask.getEndTime()));

			Calendar calendarDefaultStartDate = Calendar.getInstance();
			calendarDefaultStartDate.set(Calendar.HOUR, calendarTempStartDate.get(Calendar.HOUR));
			calendarDefaultStartDate.set(Calendar.MINUTE,
					calendarTempStartDate.get(Calendar.MINUTE));

			Calendar calendarDefaultEndDate = Calendar.getInstance();
			calendarDefaultEndDate.set(Calendar.HOUR, calendarTempEndDate.get(Calendar.HOUR));
			calendarDefaultEndDate.set(Calendar.MINUTE, calendarTempEndDate.get(Calendar.MINUTE));

			getTitleLabelProperty().set(Utils.getInstance().ellipsString(aTypedTask.getName(), 10));
			getNoteLabelProperty().set(aTypedTask.getNote());
			getPriorityLabelProperty().set(TaskPriority.EVERYDAY.getMenuSuid());
			task.setPriority(TaskPriority.EVERYDAY);
			task.setType(TaskType.DEFAULT);

			parser = new SimpleDateFormat("dd.MM.yyyy hh:mm");
			task.setStartDateTime(calendarDefaultStartDate.getTime());
			task.setEndDateTime(calendarDefaultEndDate.getTime());
		}
	}

	/**
	 * Устанавливает значение свойства цвета индикатора оставшегося времени
	 *
	 * @param aResult значение оставшегося времени
	 */
	private void setProgressBarColor(double aResult) {
		if ((aResult > 0.5) && (!PropegressBarColor.GREEN.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(PropegressBarColor.GREEN);
		} else if ((aResult < 0.5) && (aResult > 0.25)
				&& (!PropegressBarColor.YELLOW.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(PropegressBarColor.YELLOW);
		} else if ((aResult < 0.25)
				&& (!PropegressBarColor.RED.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(PropegressBarColor.RED);
		}
	}

	/**
	 * Возвращает {@link#cycleModel}
	 *
	 * @return the {@link#cycleModel}
	 */
	public CycleTaskViewModel getCycleModel() {
		return cycleModel;
	}

	/**
	 * Устанавливает значение полю {@link#cycleModel}
	 *
	 * @param cycleModel значение поля
	 */
	public void setCycleModel(CycleTaskViewModel cycleModel) {
		this.cycleModel = cycleModel;
	}

}
