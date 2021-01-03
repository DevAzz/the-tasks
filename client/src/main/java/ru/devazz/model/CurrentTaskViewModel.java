package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Alert.AlertType;
import ru.devazz.entities.DefaultTask;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.entities.Task;
import ru.devazz.server.IMessageListener;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.TaskPriority;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.TaskType;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Модель представления конкретной задачи
 */
public class CurrentTaskViewModel extends PresentationModel<ITaskService, TaskModel> {

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
	private ObjectProperty<ProgressBarColor> colorProgressBarProperty;

	/** Текстовое свойство текста наименования боевого поста автора задачи */
	private StringProperty authorTextProperty;

	/** Свойство текста подсказки индикатора прогресса */
	private StringProperty tooltipProgressBarTextProperty;

	/** Модель представления формы управления циклическим назначением */
	private CycleTaskViewModel cycleModel;

	/** Удаленные задачи */
	private final List<Task> deletedTasks = new ArrayList<>();

	/**
	 * Перечисление цветов индикатора прогресса
	 */
	public enum ProgressBarColor {

		RED, GREEN, YELLOW

	}

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

	@Override
	protected String getQueueName() {
		return QueueNameEnum.TASKS_QUEUE;
	}

	/**
	 * Добавляет слушатели изменения значений свойств основных параметров задачи
	 */
	private void addPropertyListeners() {
		if (createTaskFlag.get()) {
			titleLabelProperty
					.addListener((observable, oldValue, newValue) -> {
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
					.addListener((observable, oldValue, newValue) -> {
						task.setNote(newValue);
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			startDateProperty.addListener(
					(observable, oldValue, newValue) -> {
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
							task.setStartDateTime(newValue);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();

					});
			endDateProperty.addListener(
					(observable, oldValue, newValue) -> {
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
							task.setEndDateTime(newValue);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			executorStringProperty
					.addListener((observable, oldValue, newValue) -> {
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
					.addListener((observable, oldValue, newValue) -> {
						TaskPriority priority = TaskPriority.getPriorityBySuid(newValue);
						if (null != priority) {
							task.setPriority(priority);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			documentStringProperty
					.addListener((observable, oldValue, newValue) -> {
						if (!newValue.isEmpty()) {
							File file = new File(documentStringProperty.get());
							task.setDocumentName(file.getName());
							task.setDocument(file);
						}
						Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
						changeDetectThread.setDaemon(true);
						changeDetectThread.start();
					});
			desciprtionLabelProperty.addListener((observable, oldValue, newValue) -> {
				String descriptionValue = Optional.ofNullable(task.getDescription()).orElse("");
				if (!descriptionValue.equals(newValue)) {
					task.setDescription(newValue);
					Thread changeDetectThread = new Thread(new ChangeExistCheckRunnable());
					changeDetectThread.setDaemon(true);
					changeDetectThread.start();
				}
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
			TaskModel entity;
			try {
				entity = ProxyFactory.getInstance().getService(ITaskService.class)
						.get(task.getSuid());
				Task originalTask = EntityConverter.getInstatnce()
						.convertTaskModelToClientWrapTask(entity);
				setTask(originalTask);
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public void createTaskEntity() throws Exception {
		task.setStatus(TaskStatus.WORKING);
		if (null == task.getType()) {
			task.setType(TaskType.USUAL);
		}
		cycleModel.saveCyclicity();
		getService().add(EntityConverter.getInstatnce().convertClientWrapTaskToTaskEntity(task),
				true);
	}

	public Task getTask() {
		return task;
	}

	/**
	 * Инициализирует модель данных
	 */
	public void initTaskModel(Task initTask) {
		if (initTask != null) {
			TaskModel model = service.get(initTask.getSuid());
			setTask(Optional.ofNullable(model)
					.map(modelValue -> EntityConverter.getInstatnce().convertTaskModelToClientWrapTask(modelValue))
					.orElse(initTask));

			SubordinationElement currentUserSubEl;
			currentUserSubEl = Utils.getInstance().getCurrentUserSubEl();

			String title = (null != task.getName()) ? task.getName() : "";
			String note = (null != task.getNote()) ? task.getNote() : "";
			String desc = (null != task.getDescription()) ? task.getDescription() : "";
			String status = (null != task.getStatus()) ? task.getStatus().getName()
					: "Не установлен";
			String priority = (null != task.getPriority()) ? task.getPriority().getMenuSuid()
					: "Не установлен";
			double percent = (null != task.getExecPercent()) ? task.getExecPercent() : 0;
			LocalDateTime startDate = task.getStartDateTime();
			LocalDateTime endDate = task.getEndDateTime();
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

			getStartDateProperty().set(Optional.ofNullable(startDate).orElse(LocalDateTime.now()));
			getEndDateProperty().set(Optional.ofNullable(endDate).orElse(LocalDateTime.now()));

			getExecutorStringProperty().set(executor);
			setDocumentStringPropertyValue(path);
			setAuthorTextValue(author);
		}
	}

	public void setTask(Task aTask) {
		if (null != aTask) {
			this.task = aTask.copy();
			cycleModel.setTask(task);
			cycleModel.setEditModePropertyValue(getEditModeProperty().get());
		}
	}

	public StringProperty getTitleLabelProperty() {
		return titleLabelProperty;
	}

	public StringProperty getStatusLabelProperty() {
		return statusLabelProperty;
	}

	public StringProperty getPriorityLabelProperty() {
		return priorityLabelProperty;
	}

	public StringProperty getNoteLabelProperty() {
		return noteLabelProperty;
	}

	public StringProperty getDesciprtionLabelProperty() {
		return desciprtionLabelProperty;
	}

	public DoubleProperty getProgressProperty() {
		return progressProperty;
	}

	public BooleanProperty getCreateTaskFlag() {
		return createTaskFlag;
	}

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
			addJmsListener(getMessageListener());
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

	public Boolean getCreateFlagValue() {
		return createTaskFlag.getValue();
	}

	public BooleanProperty getReverseCreateFlag() {
		return reverseCreateFlag;
	}

	public BooleanProperty getOkButtonDisabledProperty() {
		return okButtonDisabled;
	}

	private void setOkButtonDisabledValue(Boolean aCreateOrFinishDisabled) {
		okButtonDisabled.setValue(aCreateOrFinishDisabled);
	}

	public ObjectProperty<LocalDateTime> getStartDateProperty() {
		return startDateProperty;
	}

	public ObjectProperty<LocalDateTime> getEndDateProperty() {
		return endDateProperty;
	}

	public StringProperty getExecutorStringProperty() {
		return executorStringProperty;
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	public StringProperty getDocumentStringProperty() {
		return documentStringProperty;
	}

	public void setDocumentStringPropertyValue(String documentString) {
		this.documentStringProperty.set(documentString);
	}

	public BooleanProperty getEditModeProperty() {
		return editModeProperty;
	}

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

	public void setSubElExecutor(SubordinationElement subElExecutor) {
		if ((Utils.getInstance().getCurrentUserSubEl().getRoleSuid() >= subElExecutor
				.getRoleSuid())
				&& !isSubordinate(Utils.getInstance().getCurrentUserSubEl(), subElExecutor)) {
			DialogUtils.getInstance().showAlertDialog("Невозможно задать исполнителя",
													  "Невозможно назначить исполнителем задачи выбранное должностное лицо",
													  AlertType.WARNING);
		} else {
			getExecutorStringProperty().set(subElExecutor.getName());
			task.setExecutor(subElExecutor);
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

	private IMessageListener getMessageListener() {
		return event -> Platform.runLater(() -> {
			IEntity entity = event.getEntity();
			if (entity instanceof TaskModel) {
				TaskModel taskEntity = (TaskModel) entity;
				Task task = EntityConverter.getInstatnce()
						.convertTaskModelToClientWrapTask(
								taskEntity);
				if (task.getSuid()
							.equals(this.task.getSuid())
					&& getOpenViewFlag().get()) {
					switch (event.getType()) {
						case "updated":
						case "done":
						case "rework":
						case "failed":
						case "closed":
						case "overdue":
							initTaskModel(task);
							break;
						case "deleted":
							if (!deletedTasks
									.contains(task)) {
								deletedTasks.add(task);
								setOpenFlagValue(false);
								DialogUtils.getInstance()
										.showAlertDialog(
												"Задача удалена",
												"Задача " +
												task.getName() +
												" была удалена",
												AlertType.INFORMATION);
								setOpenFlagValue(false);
							}
							break;
					}

				}
			}

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
			TaskModel entity = ProxyFactory.getInstance()
					.getService(ITaskService.class).get(task.getSuid());
			Task originalTask = EntityConverter.getInstatnce()
					.convertTaskModelToClientWrapTask(entity);
			if (null != originalTask) {
				setChangeExistValue(!originalTask.equals(task));
			}

		}

	}

	public BooleanProperty getChangeExistProperty() {
		return changeExistProperty;
	}

	public void setChangeExistValue(Boolean changeExistValue) {
		this.changeExistProperty.set(changeExistValue);
	}

	public StringProperty getOkButtonTextProperty() {
		return okButtonTextProperty;
	}

	private void setOkButtonTextValue(String okButtonTextValue) {
		this.okButtonTextProperty.set(okButtonTextValue);
	}

	public BooleanProperty getVisibleOkButtonProperty() {
		return visibleOkButtonProperty;
	}

	private void setVisibleOkButtonValue(Boolean visibleOkButtonValue) {
		this.visibleOkButtonProperty.set(visibleOkButtonValue);
	}

	public StringProperty getEditModeButtonTextProperty() {
		return editModeButtonTextProperty;
	}

	private void setEditModeButtonTextValue(String editModeButtonTextValue) {
		this.editModeButtonTextProperty.set(editModeButtonTextValue);
	}

	public BooleanProperty getVisibleEditModeButtonProperty() {
		return visibleEditModeButtonProperty;
	}

	public void setVisibleEditModeButtonValue(Boolean visibleEditModeButtonValue) {
		this.visibleEditModeButtonProperty.set(visibleEditModeButtonValue);
	}

	public BooleanProperty getVisibleDecisionButtonProperty() {
		return visibleDecisionButtonProperty;
	}

	private void setVisibleDecisionButtonValue(Boolean visibleDecisionButtonValue) {
		this.visibleDecisionButtonProperty.set(visibleDecisionButtonValue);
	}

	public BooleanProperty getVisibleDefaultTaskButtonProperty() {
		return visibleDefaultTaskButtonProperty;
	}

	private void setVisibleDefaultTaskButtonValue(Boolean visibleDefaultTaskButtonValue) {
		this.visibleDefaultTaskButtonProperty.set(visibleDefaultTaskButtonValue);
	}

	public BooleanProperty getVisibleSelectExecutorButtonProperty() {
		return visibleSelectExecutorButtonProperty;
	}

	private void setVisibleSelectExecutorButtonValue(Boolean visibleSelectExecutorButtonValue) {
		this.visibleSelectExecutorButtonProperty.set(visibleSelectExecutorButtonValue);
	}

	public ObjectProperty<ProgressBarColor> getColorProgressBarTextProperty() {
		return colorProgressBarProperty;
	}

	private void setColorProgressBarTextValue(ProgressBarColor aColor) {
		Platform.runLater(() -> this.colorProgressBarProperty.set(aColor));
	}

	public StringProperty getAuthorTextProperty() {
		return authorTextProperty;
	}

	private void setAuthorTextValue(String authorTextValue) {
		this.authorTextProperty.set(authorTextValue);
	}

	public StringProperty getTooltipProgressBarTextProperty() {
		return tooltipProgressBarTextProperty;
	}

	/**
	 * Проверка на исполнителя задачи.
	 *
	 * @return {@code true} - если текущий пользователь является исполнителем задачи
	 */
	private Boolean executorTaskCheck() {
		Long positionUserSuid = Utils.getInstance().getCurrentUser().getPositionSuid();
		return positionUserSuid.equals(task.getExecutor().getSuid());
	}

	/**
	 * Проверка на автора задачи.
	 *
	 * @return {@code true} - если текущий пользователь является автором задачи
	 */
	public Boolean authorTaskCheck() {
		boolean result = false;
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
						long startDate = task.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
						long endDate = task.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
						boolean exitCondition = new Date().getTime() <= (endDate - 10L);
						double diskret = ((endDate - startDate) / 10000);
						double sleepTime = diskret - 5;
						while (exitCondition) {
							startDate = task.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
							endDate = task.getEndDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
							double diskretNumber = (new Date().getTime() - startDate) / diskret;
							double result = 1 - (diskretNumber * 0.0001);
							progressProperty.set(Math.max(result, 0.01));

							setProgressBarColor(result);
							if (!getOpenViewFlag().get()) {
								break;
							}
							Thread.sleep((0 > sleepTime) ? 0 : (long) sleepTime);
						}

						if (!exitCondition) {
							progressProperty.set(1);
							setColorProgressBarTextValue(ProgressBarColor.RED);
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
					setColorProgressBarTextValue(ProgressBarColor.GREEN);
					break;
				case FAILD:
					progressProperty.set(1);
					setColorProgressBarTextValue(ProgressBarColor.RED);
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

		TimeUnitEnum(String... units) {
			this.units = units;
		}

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

			if (aCount >= 5 && aCount <= 20 || balance == 0 || balance >= 5) {
				result += aCount + " " + aTimeUnit.getUnits()[0] + " ";
			} else if (balance == 1) {
				result += aCount + " " + aTimeUnit.getUnits()[1] + " ";
			} else {
				result += aCount + " " + aTimeUnit.getUnits()[2] + " ";
			}
		}
		return result;

	}

	/**
	 * Возвращает текстовое представление оставшегося времени
	 *
	 * @return текстовое представление оставшегося времени
	 */
	private String getTimeLeft() {
		String result = "Осталось времени: ";
		try {
			TaskStatus status = task.getStatus();
			if (new Date().getTime() < task.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
				result = "Задача еще не началась";
			} else {
				if (!(TaskStatus.CLOSED.equals(status) || TaskStatus.FAILD.equals(status)
						|| TaskStatus.DONE.equals(status) || TaskStatus.OVERDUE.equals(status))) {
					long endDate = task.getEndDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
					int secInt = (int) ((endDate - new Date().getTime()) / 1000);
					int minInt = secInt / 60;
					int hoursInt = minInt / 60;
					int daysInt = hoursInt / 24;

					result += getTimeUnitForm(daysInt, TimeUnitEnum.DAY);
					result += getTimeUnitForm(hoursInt - (24 * daysInt), TimeUnitEnum.HOUR);
					result += getTimeUnitForm(minInt - (60 * hoursInt), TimeUnitEnum.MINUTE);
					result += getTimeUnitForm(secInt - (60 * minInt), TimeUnitEnum.SECOND);
				} else if (!TaskStatus.OVERDUE.equals(status)) {
					result = "Задача завершена";
				} else {
					result = "Задача просрочена";
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

			parser.applyPattern("dd.MM.yyyy hh:mm");
			task.setStartDateTime(new java.sql.Timestamp(calendarDefaultStartDate.getTime().getTime()).toLocalDateTime());
			task.setEndDateTime(new java.sql.Timestamp(calendarDefaultEndDate.getTime().getTime()).toLocalDateTime());
		}
	}

	/**
	 * Устанавливает значение свойства цвета индикатора оставшегося времени
	 *
	 * @param aResult значение оставшегося времени
	 */
	private void setProgressBarColor(double aResult) {
		if ((aResult > 0.5) && (!ProgressBarColor.GREEN.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(ProgressBarColor.GREEN);
		} else if ((aResult < 0.5) && (aResult > 0.25)
				&& (!ProgressBarColor.YELLOW.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(ProgressBarColor.YELLOW);
		} else if ((aResult < 0.25)
				&& (!ProgressBarColor.RED.equals(colorProgressBarProperty.get()))) {
			setColorProgressBarTextValue(ProgressBarColor.RED);
		}
	}

	public void setCycleModel(CycleTaskViewModel cycleModel) {
		this.cycleModel = cycleModel;
	}

}
