package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import jfxtras.scene.control.LocalDateTimeTextField;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.SubordinationElementEntity;
import ru.sciencesquad.hqtasks.server.utils.SortType;
import ru.sciencesquad.hqtasks.server.utils.TaskPriority;
import ru.sciencesquad.hqtasks.server.utils.TaskStatus;
import ru.siencesquad.hqtasks.ui.entities.DefaultTask;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.model.CurrentTaskViewModel;
import ru.siencesquad.hqtasks.ui.model.CurrentTaskViewModel.PropegressBarColor;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.DesktopOpen;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Предсталение конкретной задачи
 */
public class CurrentTaskView extends AbstractView<CurrentTaskViewModel> {

	/** Панель вкладок представления */
	@FXML
	private TabPane currentTaskTabPane;

	/** Лейбл заголовок */
	@FXML
	private TextField title;

	/** Лейбл статус */
	@FXML
	private Label status;

	/** Комбо выбора приоритета */
	@FXML
	private ComboBox<TaskPriority> priority;

	/** Лейбл примечание */
	@FXML
	private TextField note;

	/** Лейбл описание */
	@FXML
	private HTMLEditor description;

	/** Кнопка просмотра/прикрепления подтверждающего документа */
	@FXML
	private Button buttonDoc;

	/** Кнопка завершения задачи */
	@FXML
	private Button okButton;

	/** Кнопка отмены создания задачи */
	@FXML
	private Button buttonCancelCreateTask;

	/** Иконка статуса */
	@FXML
	private ImageView statusImage;

	/** Иконка приоритета */
	@FXML
	private ImageView priorityImage;

	/** Прогресс бар */
	@FXML
	private ProgressBar progressBar;

	/** Панель описания задачи */
	@FXML
	private TitledPane taskDescriptionTitledPane;

	/** Панель настройки циклического назначения */
	@FXML
	private TitledPane cycleTitledPane;

	/** Панель дополнительных параметров */
	@FXML
	private TitledPane extendedTitledPane;

	/** Панель истории задачи */
	@FXML
	private TitledPane historyTitledPane;

	/** Дата начала */
	@FXML
	private LocalDateTimeTextField startDate;

	/** Дата завершения */
	@FXML
	private LocalDateTimeTextField endDate;

	/** Кнопка выбора типовой задачи */
	@FXML
	private Button selectTypedTask;

	/** Кнопка принятия решения по задаче */
	@FXML
	private Button decisionButton;

	/** Компонент кнопок создания и отмены создания задачи */
	@FXML
	private ButtonBar createTaskButtonBar;

	/** Контейнер вкладок */
	@FXML
	private Accordion accordion;

	/** Подсказка кнопки выбора/добавления документа */
	@FXML
	private Tooltip tooltipDocument;

	/** Выбор исполнителя */
	@FXML
	private Button selectExec;

	/** Поле "Исполнитель задачи" */
	@FXML
	private TextField executorTextField;

	/** Поле "Документ" */
	@FXML
	private TextField documentTextField;

	/** Кнопка входа в режим редактирования задачи */
	@FXML
	private Button editModeButton;

	/** Текстовое поле "Автор" */
	@FXML
	private TextField authorTextField;

	/** Общий контейнер вкладок */
	private TabPane commonTabPane;

	/** Представление формы циклического назначения задач */
	private CycleTaskView cycleView;

	/** Лейбл оставшегося времени для задачи */
	@FXML
	private Label timeLeftTaskLabel;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		bind();

		// Установка знчений комобо-боксу выбора приоритета
		ObservableList<TaskPriority> values = FXCollections.observableArrayList();
		values.addAll(TaskPriority.values());
		priority.setItems(values);

		priority.setPromptText(TaskPriority.CRITICAL.getName());
		priority.setConverter(new StringConverter<TaskPriority>() {

			/**
			 * @see javafx.util.StringConverter#toString(Object)
			 */
			@Override
			public String toString(TaskPriority object) {
				return object.getName();
			}

			/**
			 * @see javafx.util.StringConverter#fromString(String)
			 */
			@Override
			public TaskPriority fromString(String string) {
				return null;
			}
		});

		// Установка значения полю "Описание"
		description.setHtmlText(model.getDesciprtionLabelProperty().get());

		// Установка открытой панели
		accordion.setExpandedPane(taskDescriptionTitledPane);

		timeLeftTaskLabel.setFont(new Font(15));

		model.getOpenViewFlag()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (!newValue) {
						closeTabView();
					}

				});

	}

	/**
	 * Инициализация представления управления цикилическим назначением
	 */
	public void initCycleView() {
		try {
			cycleView = Utils.getInstance().loadView(CycleTaskView.class);
			model.setCycleModel(cycleView.getModel());
			if (null != cycleView) {
				cycleTitledPane.setContent(cycleView.getRootPane());
			}
		} catch (Exception e) {
			// TODO Логироание
			e.printStackTrace();
		}
	}

	/**
	 * Связывание компонентов с моделью
	 */
	private void bind() {

		// Видимость компонентов
		Bindings.bindBidirectional(buttonCancelCreateTask.visibleProperty(),
				model.getCreateTaskFlag());
		Bindings.bindBidirectional(editModeButton.visibleProperty(),
				model.getVisibleEditModeButtonProperty());
		Bindings.bindBidirectional(okButton.visibleProperty(), model.getVisibleOkButtonProperty());
		Bindings.bindBidirectional(decisionButton.visibleProperty(),
				model.getVisibleDecisionButtonProperty());
		Bindings.bindBidirectional(selectTypedTask.visibleProperty(),
				model.getVisibleDefaultTaskButtonProperty());
		Bindings.bindBidirectional(selectExec.visibleProperty(),
				model.getVisibleSelectExecutorButtonProperty());

		// Содержание компонентов
		Bindings.bindBidirectional(title.textProperty(), model.getTitleLabelProperty());
		Bindings.bindBidirectional(status.textProperty(), model.getStatusLabelProperty());
		Bindings.bindBidirectional(note.textProperty(), model.getNoteLabelProperty());
		Bindings.bindBidirectional(progressBar.progressProperty(), model.getProgressProperty());
		Bindings.bindBidirectional(startDate.localDateTimeProperty(), model.getStartDateProperty());
		Bindings.bindBidirectional(endDate.localDateTimeProperty(), model.getEndDateProperty());
		Bindings.bindBidirectional(executorTextField.textProperty(),
				model.getExecutorStringProperty());
		Bindings.bindBidirectional(documentTextField.textProperty(),
				model.getDocumentStringProperty());
		Bindings.bindBidirectional(okButton.textProperty(), model.getOkButtonTextProperty());
		Bindings.bindBidirectional(editModeButton.textProperty(),
				model.getEditModeButtonTextProperty());
		Bindings.bindBidirectional(authorTextField.textProperty(), model.getAuthorTextProperty());
		Bindings.bindBidirectional(timeLeftTaskLabel.textProperty(),
				model.getTooltipProgressBarTextProperty());

		// Доступность компонентов
		Bindings.bindBidirectional(startDate.mouseTransparentProperty(),
				model.getReverseCreateFlag());
		Bindings.bindBidirectional(endDate.mouseTransparentProperty(),
				model.getReverseCreateFlag());
		Bindings.bindBidirectional(description.mouseTransparentProperty(),
				model.getReverseCreateFlag());
		Bindings.bindBidirectional(selectTypedTask.disableProperty(), model.getReverseCreateFlag());
		Bindings.bindBidirectional(priority.mouseTransparentProperty(),
				model.getReverseCreateFlag());
		Bindings.bindBidirectional(title.editableProperty(), model.getCreateTaskFlag());
		Bindings.bindBidirectional(note.editableProperty(), model.getCreateTaskFlag());
		Bindings.bindBidirectional(okButton.disableProperty(), model.getOkButtonDisabledProperty());
		Bindings.bindBidirectional(selectExec.disableProperty(), model.getReverseCreateFlag());
		Bindings.bindBidirectional(executorTextField.editableProperty(), model.getCreateTaskFlag());

		priority.setOnAction(event -> {
			TaskPriority selectedItem = priority.getSelectionModel().getSelectedItem();
			if (null != selectedItem) {
				setImagePriority(selectedItem);
				model.getPriorityLabelProperty().set(selectedItem.getMenuSuid());
			}
		});
		model.getStatusLabelProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					setStatusImage(model.getTask().getStatus());
				});
		model.getCreateTaskFlag()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (!newValue) {
						tooltipDocument.setText("Посмотреть документ");
						description.getStyleClass().add("htmlEditorViewMode");
						// Установка стиля кнопки документа создания задачи
						buttonDoc.getStyleClass().clear();
						buttonDoc.getStyleClass().add("fileShowBut");
					} else {
						description.getStyleClass().remove("htmlEditorViewMode");
						// Установка стиля кнопки документа просмотра задачи
						buttonDoc.getStyleClass().clear();
						buttonDoc.getStyleClass().add("fileAddBut");
					}
				});

		model.getTitleLabelProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					String errStyle = "textFieldErr";
					if (newValue.isEmpty()) {
						title.getStyleClass().add(errStyle);
					} else {
						title.getStyleClass().remove(errStyle);
					}
				});

		startDate.getStyleClass().add("dateTimeTextFieldErr");
		endDate.getStyleClass().add("dateTimeTextFieldErr");
		model.getStartDateProperty()
				.addListener((ChangeListener<LocalDateTime>) (observable, oldValue, newValue) -> {
					String errStyle = "dateTimeTextFieldErr";
					String normal = "dateTimeTextFieldNormal";
					if (null == newValue) {
						startDate.getStyleClass().add(errStyle);
						for (String value : new ArrayList<>(startDate.getStyleClass())) {
							if (value.equals(normal)) {
								startDate.getStyleClass().remove(value);
							}
						}
						startDate.getStyleClass().remove(normal);
					} else {
						startDate.getStyleClass().remove(errStyle);
						startDate.getStyleClass().add(normal);
						for (String value : startDate.getStyleClass()) {
							if (value.equals(errStyle)) {
								startDate.getStyleClass().remove(errStyle);
							}
						}
					}
				});

		model.getEndDateProperty()
				.addListener((ChangeListener<LocalDateTime>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						String errStyle = "dateTimeTextFieldErr";
						String normal = "dateTimeTextFieldNormal";

						if (null == newValue) {
							endDate.getStyleClass().add(errStyle);
							for (String value : new ArrayList<>(endDate.getStyleClass())) {
								if (value.equals(normal)) {
									endDate.getStyleClass().remove(value);
								}
							}
							endDate.getStyleClass().remove(normal);
						} else {
							endDate.getStyleClass().remove(errStyle);
							endDate.getStyleClass().add(normal);
							for (String value : endDate.getStyleClass()) {
								if (value.equals(errStyle)) {
									endDate.getStyleClass().remove(errStyle);
								}
							}
						}

					});

				});

		model.getExecutorStringProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					String errStyle = "textFieldErr";
					if (newValue.isEmpty()) {
						executorTextField.getStyleClass().add(errStyle);
						selectTypedTask.setOpacity(0.4);
						selectTypedTask.setMouseTransparent(true);
					} else {
						executorTextField.getStyleClass().remove(errStyle);
						selectTypedTask.setOpacity(1);
						selectTypedTask.setMouseTransparent(false);
					}
				});

		model.getDesciprtionLabelProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> description.setHtmlText(newValue));

				});

		model.getPriorityLabelProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if ((null != newValue) && (!newValue.isEmpty())) {
						priority.getSelectionModel()
								.select(TaskPriority.getPriorityBySuid(newValue));
					}
				});

		buttonCancelCreateTask.setOnMouseClicked(event -> {
			closeTabView();
		});
		buttonDoc.setOnMouseClicked(e -> {
			if (model.getCreateFlagValue()) {
				// Отправка документа на сервер
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Выбор документа");
				File file = fileChooser.showOpenDialog(stage);
				if (null != file) {
					if (Utils.getInstance().isCorrectFileType(file.getAbsolutePath())) {
						model.setDocumentStringPropertyValue(file.getAbsolutePath());

					} else {
						DialogUtils.getInstance().showAlertDialog("Ошибка выбора файла",
								"Выбран исполняемый файл. \n\rДобавление исполняемых файлов запрещено",
								AlertType.WARNING);
					}
				}
			} else {
				if (null != model.getTask().getDocument()) {
					new DesktopOpen("file:///" + model.getTask().getDocument().getAbsolutePath());
				} else {
					DialogUtils.getInstance().showAlertDialog("Невозможно открыть документ",
							"Документ не был прикреплен", AlertType.ERROR);
				}
			}
		});

		// Вызов диалога типовых задач
		selectTypedTask.setOnMouseClicked(e -> {
			Consumer<? super DefaultTask> consumer = t -> {
				try {
					model.selectTypedTask(t);
					priority.getSelectionModel().select(TaskPriority.EVERYDAY);

					startDate.setLocalDateTime(
							LocalDateTime.ofInstant(model.getTask().getStartDateTime().toInstant(),
									ZoneId.systemDefault()));
					endDate.setLocalDateTime(LocalDateTime.ofInstant(
							model.getTask().getEndDateTime().toInstant(), ZoneId.systemDefault()));
				} catch (ParseException e1) {
					// Логирование
					e1.printStackTrace();
				}

			};

			DialogUtils.getInstance().showSelectDefaultTaskDialog(stage, consumer,
					model.getTask().getExecutor().getSuid());

		});

		decisionButton.setOnMouseClicked(event -> {
			try {
				Consumer<Task> consumer = t -> {
					closeTabView();
				};
				DialogUtils.getInstance().showDecisionDialog(getStage(), consumer, model.getTask());

			} catch (IOException e1) {
				// TODO Логирование
				e1.printStackTrace();
			}

		});

		model.getOpenViewFlag()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (!newValue && isViewOpen()) {
						closeTabView();
					}
				});

		// добавляет признак того, что содержание представления было изменено
		model.getChangeExistProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						if (null != getTab()) {
							if (newValue) {
								getTab().setText("* " + getTab().getText());
							} else {
								getTab().setText(model.getTask().getName());
							}
						}
					});

				});
		model.getColorProgressBarTextProperty().addListener(
				(ChangeListener<PropegressBarColor>) (observable, oldValue, newValue) -> {
					switch (newValue) {
					case RED:
						progressBar.getStyleClass().remove("progressBarGreen");
						progressBar.getStyleClass().remove("progressBarYellow");
						progressBar.getStyleClass().add("progressBarRed");
						break;
					case GREEN:
						progressBar.getStyleClass().remove("progressBarRed");
						progressBar.getStyleClass().remove("progressBarYellow");
						progressBar.getStyleClass().add("progressBarGreen");
						break;
					case YELLOW:
						progressBar.getStyleClass().remove("progressBarRed");
						progressBar.getStyleClass().remove("progressBarGreen");
						progressBar.getStyleClass().add("progressBarYellow");
						break;
					}

				});
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#getTabPane()
	 */
	@Override
	public TabPane getTabPane() {
		return currentTaskTabPane;
	}

	/**
	 * Возвращает вкладку панели
	 *
	 * @return вкладка панели
	 */
	@Override
	public Tab getTab() {
		Tab tab = (!currentTaskTabPane.getTabs().isEmpty()) ? currentTaskTabPane.getTabs().get(0)
				: null;
		return tab;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected CurrentTaskViewModel createPresentaionModel() {
		return new CurrentTaskViewModel();
	}

	/**
	 * Инициализирует контент представления
	 */
	public void initContent() {
		Task task = model.getTask();
		setStatusImage(task.getStatus());
		setImagePriority(task.getPriority());
		if (!model.getCreateFlagValue()) {
			model.setVisibleEditModeButtonValue(model.authorTaskCheck()
					&& !(TaskStatus.CLOSED.equals(model.getTask().getStatus())
							|| TaskStatus.FAILD.equals(model.getTask().getStatus())));
			if (TaskStatus.CLOSED.equals(model.getTask().getStatus())
					|| TaskStatus.FAILD.equals(model.getTask().getStatus())
					|| TaskStatus.DONE.equals(model.getTask().getStatus())) {
				timeLeftTaskLabel.setText("Задача завершена");
			}
			try {
				TaskHistoryView historyView = Utils.getInstance().loadView(TaskHistoryView.class);
				historyView.getModel().setTask(task);
				historyView.getModel().setInitialSortType(SortType.SORT_BY_DATE_FIRST_OLD);
				historyView.loadPageEntries(0);
				historyTitledPane.setContent(historyView.getRootPane());
			} catch (IOException e) {
				// TODO логирование
				e.printStackTrace();
			}
		} else {
			model.setVisibleEditModeButtonValue(false);
		}
	}

	/**
	 * Устанавливает миниатюру статуса
	 *
	 * @param aStatus статус
	 */
	private void setStatusImage(TaskStatus aStatus) {
		if (null != aStatus) {
			switch (aStatus) {
			case DONE:
				statusImage.setImage(new Image("/css/complited.png"));
				break;
			case CLOSED:
				statusImage.setImage(new Image("/css/complitedAndLocked.png"));
				break;
			case FAILD:
				statusImage.setImage(new Image("/css/faild.png"));
				break;
			case OVERDUE:
				statusImage.setImage(new Image("/css/timeoutcomplited.png"));
				break;
			case REWORK:
				statusImage.setImage(new Image("/css/rework.png"));
				break;
			case WORKING:
				statusImage.setImage(new Image("/css/inwork.png"));
				break;
			default:
				break;
			}
		}
	}

	/**
	 *
	 * Устанавливает миниатюру приоритета
	 *
	 * @param aPriority приоритет
	 */
	private void setImagePriority(TaskPriority aPriority) {
		if (null != aPriority) {
			switch (aPriority) {
			case CRITICAL:
				priorityImage.setImage(new Image("/css/priority4.png"));
				break;
			case MAJOR:
				priorityImage.setImage(new Image("/css/priority3.png"));
				break;
			case MINOR:
				priorityImage.setImage(new Image("/css/priority2.png"));
				break;
			case TRIVIAL:
				priorityImage.setImage(new Image("/css/priority1.png"));
				break;
			case EVERYDAY:
				priorityImage.setImage(new Image("/css/everyDay.png"));
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Создать или завершить задачу
	 */
	@FXML
	private void createOrFinishTask() {
		if (model.getCreateFlagValue()) {
			boolean endBeforeStart = endDate.getLocalDateTime()
					.isBefore(startDate.getLocalDateTime())
					|| endDate.getLocalDateTime().isEqual(startDate.getLocalDateTime());
			if (endBeforeStart && model.getCreateFlagValue()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Невозможно создать задачу");
				alert.setHeaderText("Дата завершения не может быть раньше даты начала");
				alert.show();
			} else {
				if (!model.getEditModeProperty().get()) {
					try {
						model.getTask().setDescription(description.getHtmlText());
						model.createTaskEntity();
						fireUpdate();
					} catch (Exception e) {
						// TODO Логирование
						e.printStackTrace();
					}
				} else {
					// Установка описания
					model.getTask().setDescription(description.getHtmlText());

					// Установка статуса
					try {
						TaskStatus status = model.getTask().getStatus();
						if ((new Date().getTime() < model.getTask().getEndDate().getTime())
								&& !(TaskStatus.REWORK.equals(status))) {
							model.getTask().setStatus(TaskStatus.WORKING);
						}
						model.updateTaskEntity();
						model.setChangeExistValue(false);
					} catch (ParseException e) {
						// TODO Логирование
						e.printStackTrace();
					}

				}
				closeCurrentTask();
			}
		} else {
			// Завершение задачи
			try {
				Consumer<Task> consumer = t -> {
					closeTabView();
				};
				DialogUtils.getInstance().showCompletionTaskDialog(getStage(), consumer,
						model.getTask());

			} catch (IOException e1) {
				// TODO Логирование
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Клик по кнопке перехода в режим редактирования
	 */
	@FXML
	private void editModeButtonClick() {
		Boolean editMode = !model.getEditModeProperty().get();
		if (!editMode) {
			Consumer<ButtonType> consumer = t -> {
				if (t.getButtonData().isDefaultButton()) {
					model.updateTaskEntity();
					model.setEditModeValue(editMode);
					cycleView.setEditMode(editMode);
					model.setChangeExistValue(false);
				} else {
					model.revertChanges();
				}
			};
			if (model.getChangeExistProperty().get()) {
				DialogUtils.getInstance().showChoiceDialog("Задача была изменена",
						"Сохранить изменения?", consumer);
			}
		}
		model.setEditModeValue(editMode);
		cycleView.setEditMode(editMode);
	}

	/**
	 * Возвращает {@link#commonTabPane}
	 *
	 * @return the {@link#commonTabPane}
	 */
	public TabPane getCommonTabPane() {
		return commonTabPane;
	}

	/**
	 * Устанавливает значение полю {@link#commonTabPane}
	 *
	 * @param commonTabPane значение поля
	 */
	public void setCommonTabPane(TabPane commonTabPane) {
		this.commonTabPane = commonTabPane;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#closeTabView()
	 */
	@Override
	@FXML
	protected void closeTabView() {
		if (model.getChangeExistProperty().get() && !model.getCreateFlagValue()) {
			Consumer<ButtonType> consumer = t -> {
				if (t.getButtonData().isDefaultButton()) {
					model.updateTaskEntity();
					model.setChangeExistValue(false);
					closeCurrentTask();
				} else {
					model.revertChanges();
					model.setChangeExistValue(false);
					closeCurrentTask();
				}
			};
			DialogUtils.getInstance().showChoiceDialog("Задача была изменена",
					"Сохранить изменения?", consumer);
		} else {
			closeCurrentTask();
		}
	}

	/**
	 * Закрывает текущую открытую задачу
	 */
	private void closeCurrentTask() {
		if (null != commonTabPane) {
			commonTabPane.getTabs().remove(getTab());
		}
		super.closeTabView();
	}

	/**
	 * Выбор исполнителя
	 */
	@FXML
	public void selectExecutor() {
		SubordinatioElementServiceRemote subElsService;
		try {
			subElsService = EJBProxyFactory.getInstance()
					.getService(SubordinatioElementServiceRemote.class);

			ObservableList<SubordinationElement> listSubEls = FXCollections.observableArrayList();

			Consumer<? super List<SubordinationElement>> consumer = t -> {
				SubordinationElement element = t.get(0);
				if (null != element) {
					model.setSubElExecutor(element);
				}
			};

			for (SubordinationElementEntity entity : subElsService
					.getAll(Utils.getInstance().getCurrentUser().getIduser())) {
				listSubEls
						.add(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity));
			}

			DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, false, false);
		} catch (NamingException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((model.getTask() == null) ? 0 : model.getTask().hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CurrentTaskView other = (CurrentTaskView) obj;
		if (model.getTask() == null) {
			if (other.model.getTask() != null) {
				return false;
			}
		} else if (!model.getTask().equals(other.model.getTask())) {
			return false;
		}
		return true;
	}

}
