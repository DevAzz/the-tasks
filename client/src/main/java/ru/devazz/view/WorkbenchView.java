package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.entities.Task;
import ru.devazz.interfaces.SelectionListener;
import ru.devazz.model.WorkbenchViewModel;
import ru.devazz.server.api.model.enums.TasksViewType;
import ru.devazz.server.api.model.enums.UserRoles;
import ru.devazz.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Представление рабочего стола
 */
public class WorkbenchView extends AbstractView<WorkbenchViewModel> {

	/** Контейнер вкладок представления рабочего стола */
	@FXML
	private TabPane workbenchTabPane;

	/** Вкладка рабочоего стола */
	@FXML
	private Tab workbenchTab;

	/** Вкладка "Назначенные мне" */
	@FXML
	private TitledPane inTitledPane;

	/** Вкладка "Назначенные мной" */
	@FXML
	private TitledPane outTitledPane;

	/** Вкладка "Архив" */
	@FXML
	private TitledPane archiveTitledPane;

	/** Вкладка "Задачи" */
	@FXML
	private TitledPane currentTitledPane;

	/** Вкладка "Повседневные задачи" */
	@FXML
	private TitledPane everyDayTitledPane;

	/** Аккордион */
	@FXML
	private Accordion accordion;

	/** Заголовок выбранных задач */
	@FXML
	private Label titleCurrentLabel;

	/** Кнопка выбора текущих задач */
	@FXML
	private Button currentTaskButton;

	/** Кнопка выбора входядщих задач */
	@FXML
	private Button inTaskButton;

	/** Кнопка выбора исходящих задач */
	@FXML
	private Button outTaskButton;

	/** Кнопка выбора архивных задач */
	@FXML
	private Button archiveTaskButton;

	/** Кнопка выбора повседневных задач */
	@FXML
	private Button everydayButton;

	/** Контейнер кнопок */
	@FXML
	private ButtonBar workbenchButtonBar;

	/** Панель задач "Задачи - Входящие" */
	private TasksView currentTasksViewIn;

	/** Панель задач "Задачи -исходящие" */
	private TasksView currentTasksViewOut;

	/** Панель задач "Задачи - Повседневные - входящие" */
	private TasksView currentTasksViewEverydayIn;

	/** Панель задач "Задачи - Повседневные - исходящие" */
	private TasksView currentTasksViewEverydayOut;

	/** Панель задач "Задачи - архивные" */
	private TasksView currentTasksViewArchive;

	/** Панель задач "Повседневыне задачи - входящие" */
	private TasksView everydayIn;

	/** Панель задач "Повседневные - исходящие" */
	private TasksView everydayOut;

	/** Панель задач "Назначенные мне" */
	private TasksView inTasksView;

	/** Панель задач "Назначенные мной" */
	private TasksView outTaskView;

	/** Панель задач "Архив" */
	private TasksView archiveTasksView;

	/** Выбранная задача */
	private Task selectedTask;

	/** Идентификатор элемента подчиненности, выбраный в дереве */
	private Long positionSuid;

	/** Обработчик клика по дереву подчиненности */
	private SelectionListener subTreeEventHandler = aOjbect -> {
		SubordinationElement element = (SubordinationElement) aOjbect;
		positionSuid = element.getSuid();
		setCurrentTasksTitle(element.getName());

		TasksView view = getActiveView();

		view.setPositionSuid(positionSuid);
		view.load(currentTasksViewIn.getCurrentPageNumber());
	};

	@Override
	public void initialize() {
		Bindings.bindBidirectional(titleCurrentLabel.textProperty(), model.getTitleProperty());
		Bindings.bindBidirectional(currentTaskButton.textProperty(), model.getTitleProperty());
	}

	/**
	 * Возвращает активное представление задач
	 *
	 * @return представление задач
	 */
	private TasksView getActiveView() {
		TasksView result = null;
		for (TasksView view : getTaskViews()) {
			if (view.getModel().getIsActive()) {
				result = view;
				break;
			}
		}
		return result;
	}

	/**
	 * Инициализация представлений задач
	 */
	public void initViews() {
		try {
			Boolean access = Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT);
			Boolean isUserRMCHead = Utils.getInstance().checkRoleEquals(UserRoles.RMC_HEAD);

			if (!isUserRMCHead) {
				inTasksView = initTaskView(inTitledPane, TasksViewType.IN);
				inTasksView.load(0);
				model.putModel(TasksViewType.IN, inTasksView.getModel());
			}

			if (access) {
				outTaskView = initTaskView(outTitledPane, TasksViewType.OUT);
				outTaskView.load(0);
				model.putModel(TasksViewType.OUT, outTaskView.getModel());
			}

			if (!isUserRMCHead) {
				archiveTasksView = initTaskView(archiveTitledPane, TasksViewType.ARCHIVE);
				archiveTasksView.load(0);
				model.putModel(TasksViewType.ARCHIVE, archiveTasksView.getModel());
			}

			if (!isUserRMCHead) {
				everydayIn = initTaskView(everyDayTitledPane, TasksViewType.EVERYDAY_IN);
				everydayIn.load(0);
				model.putModel(TasksViewType.EVERYDAY_IN, everydayIn.getModel());

				everydayOut = initTaskView(everyDayTitledPane, TasksViewType.EVERYDAY_OUT);
				everydayOut.load(0);
				model.putModel(TasksViewType.EVERYDAY_OUT, everydayOut.getModel());
			}

			if (access) {
				currentTasksViewIn = initTaskView(currentTitledPane, TasksViewType.CURRENT_IN);
				currentTasksViewIn.setPositionSuid(model.getPositionSuid());
				currentTasksViewIn.load(0);
				model.putModel(TasksViewType.CURRENT_IN, currentTasksViewIn.getModel());

				currentTasksViewOut = initTaskView(currentTitledPane, TasksViewType.CURRENT_OUT);
				currentTasksViewOut.setPositionSuid(model.getPositionSuid());
				currentTasksViewOut.load(0);
				model.putModel(TasksViewType.CURRENT_OUT, currentTasksViewOut.getModel());

				currentTasksViewArchive = initTaskView(currentTitledPane,
						TasksViewType.CURRENT_ARCHIVE);
				currentTasksViewArchive.setPositionSuid(model.getPositionSuid());
				currentTasksViewArchive.load(0);
				model.putModel(TasksViewType.CURRENT_ARCHIVE, currentTasksViewArchive.getModel());

				currentTasksViewEverydayIn = initTaskView(currentTitledPane,
						TasksViewType.CURRENT_EVERYDAY_IN);
				currentTasksViewEverydayIn.setPositionSuid(model.getPositionSuid());
				currentTasksViewEverydayIn.load(0);
				model.putModel(TasksViewType.CURRENT_EVERYDAY_IN,
						currentTasksViewEverydayIn.getModel());

				currentTasksViewEverydayOut = initTaskView(currentTitledPane,
						TasksViewType.CURRENT_EVERYDAY_OUT);
				currentTasksViewEverydayOut.setPositionSuid(model.getPositionSuid());
				currentTasksViewEverydayOut.load(0);
				model.putModel(TasksViewType.CURRENT_EVERYDAY_OUT,
						currentTasksViewEverydayOut.getModel());

				openTitlePane(currentTitledPane);
				Platform.runLater(() -> currentTaskButton.requestFocus());
			}

			for (TasksView view : getTaskViews()) {
				if (null != view) {
					setActiveView(view, true);
					break;
				}
			}
			initTitledPanes();
			for (Tab tab : getTabs()) {
				tab.selectedProperty()
						.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
							if (newValue && (null != tab) && (null != tab.getId())) {
								switch (tab.getId()) {
								case "currentIn":
									setActiveView(currentTasksViewIn, true);
									break;
								case "currentOut":
									setActiveView(currentTasksViewOut, true);
									break;
								case "currentEveryday":
									setActiveView(currentTasksViewEverydayIn, true);
									break;
								case "currentEveryDayIn":
									setActiveView(currentTasksViewEverydayIn, true);
									break;
								case "currentEveryDayOut":
									setActiveView(currentTasksViewEverydayOut, true);
									break;
								case "currentArchive":
									setActiveView(currentTasksViewArchive, true);
									break;
								case "everydayIn":
									setActiveView(everydayIn, true);
									break;
								case "everydayOut":
									setActiveView(everydayOut, true);
									break;
								default:
									break;
								}
							}

						});
			}

		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Инициализация панелей задач
	 */
	private void initTitledPanes() {
		if (null != currentTasksViewIn) {
			currentTaskButton.setOnMouseClicked(event -> {
				openTitlePane(currentTitledPane);
			});
		} else {
			accordion.getPanes().remove(currentTitledPane);
			workbenchButtonBar.getButtons().remove(currentTaskButton);
		}
		if (null != inTasksView) {
			inTaskButton.setOnMouseClicked(event -> {
				openTitlePane(inTitledPane);
			});
		} else {
			accordion.getPanes().remove(inTitledPane);
			workbenchButtonBar.getButtons().remove(inTaskButton);
		}
		if (null != outTaskView) {
			outTaskButton.setOnMouseClicked(event -> {
				openTitlePane(outTitledPane);
			});
		} else {
			accordion.getPanes().remove(outTitledPane);
			workbenchButtonBar.getButtons().remove(outTaskButton);
		}

		if (null != archiveTasksView) {
			archiveTaskButton.setOnMouseClicked(event -> {
				openTitlePane(archiveTitledPane);
			});
		} else {
			accordion.getPanes().remove(archiveTitledPane);
			workbenchButtonBar.getButtons().remove(archiveTaskButton);
		}
		if (null != everydayIn) {
			everydayButton.setOnMouseClicked(event -> {
				openTitlePane(everyDayTitledPane);
			});
		} else {
			accordion.getPanes().remove(everyDayTitledPane);
			workbenchButtonBar.getButtons().remove(everydayButton);
		}
		if (null != inTitledPane) {
			inTitledPane.expandedProperty()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						if (newValue) {
							setActiveView(inTasksView, true);
						}
					});
			inTitledPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				inTaskButton.requestFocus();
			});
		}
		if (null != everyDayTitledPane) {
			everyDayTitledPane.expandedProperty()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						if (newValue) {
							setActiveView(everydayIn, true);
						}
					});
			everyDayTitledPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				everydayButton.requestFocus();
			});
		}
		if (null != outTitledPane) {
			outTitledPane.expandedProperty()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						if (newValue) {
							setActiveView(outTaskView, true);
						}
					});
			outTitledPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				outTaskButton.requestFocus();
			});
		}
		if (null != currentTitledPane) {
			currentTitledPane.expandedProperty()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						if (newValue) {
							setActiveView(currentTasksViewIn, true);
						}
					});
			currentTitledPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				currentTaskButton.requestFocus();
			});
		}
		if (null != archiveTitledPane) {
			archiveTitledPane.expandedProperty()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						if (newValue) {
							setActiveView(archiveTasksView, true);
						}
					});
			archiveTitledPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				archiveTaskButton.requestFocus();
			});
		}
	}

	/**
	 * Устанавливает параметр активности в представление
	 *
	 * @param aView представление задач
	 * @param aActive параметр активности представления
	 */
	private void setActiveView(TasksView aView, Boolean aActive) {
		aView.setPositionSuid(positionSuid);
		aView.getModel().setIsActive(aActive);
		for (TasksView view : getTaskViews()) {
			if ((null != aView) && (null != view)
					&& !aView.getTypeView().equals(view.getTypeView())) {
				view.getModel().setIsActive(!aActive);
			}
		}
	}

	/**
	 * Возвращает список вкладок
	 *
	 * @return список вкладок
	 */
	private List<Tab> getTabs() {
		List<Tab> list = new ArrayList<>();
		for (TitledPane pane : getTitledPanes()) {
			if ((null != pane) && (pane.getContent() instanceof ScrollPane)) {
				ScrollPane scrollPane = (ScrollPane) pane.getContent();
				if (scrollPane.getContent() instanceof TabPane) {
					TabPane tabPane = (TabPane) scrollPane.getContent();
					for (Tab tab : tabPane.getTabs()) {
						boolean doesNotContainsTabPane = true;
						AnchorPane tabContent = (AnchorPane) tab.getContent();
						if (null != tabContent) {
							for (Node node : tabContent.getChildren()) {
								doesNotContainsTabPane &= !(node instanceof TabPane);
							}
							if (doesNotContainsTabPane) {
								list.add(tab);
							} else {
								if (null != tab.getId()) {
									list.add(tab);
								}
								TabPane inTabPane = null;
								for (Node node : ((AnchorPane) tab.getContent()).getChildren()) {
									if (node instanceof TabPane) {
										inTabPane = (TabPane) node;
										break;
									}
								}
								if (null != inTabPane) {
									for (Tab inTab : inTabPane.getTabs()) {
										list.add(inTab);
									}
								}
							}
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * Возвращает список именованых панелей
	 *
	 * @return список именованых панелей
	 */
	private List<TitledPane> getTitledPanes() {
		List<TitledPane> list = new ArrayList<>();
		list.add(archiveTitledPane);
		list.add(currentTitledPane);
		list.add(everyDayTitledPane);
		list.add(inTitledPane);
		list.add(outTitledPane);
		return list;
	}

	/**
	 * Возвращает список представлений задач
	 *
	 * @return список представлений задач
	 */
	private List<TasksView> getTaskViews() {
		List<TasksView> list = new ArrayList<>();
		list.add(currentTasksViewIn);
		list.add(currentTasksViewOut);
		list.add(currentTasksViewEverydayIn);
		list.add(currentTasksViewEverydayOut);
		list.add(currentTasksViewArchive);
		list.add(inTasksView);
		list.add(outTaskView);
		list.add(everydayIn);
		list.add(everydayOut);
		list.add(archiveTasksView);
		return list;
	}

	/**
	 * Установка значения заголовку вкладки выбранных задач
	 *
	 * @param aTitle заголовок
	 */
	public void setCurrentTasksTitle(String aTitle) {
		model.setTitleValue("Задачи: " + aTitle);
	}

	/**
	 * Инициализирует панель задач
	 *
	 * @param aPane панель
	 * @throws IOException в случае ошибки загрузки представления
	 */
	private TasksView initTaskView(TitledPane aPane, TasksViewType aTypeView) throws IOException {
		TasksView viewTasks = Utils.getInstance().loadView(TasksView.class);
		viewTasks.setTypeView(aTypeView);
		viewTasks.addSelectionListener((object) -> {
			selectedTask = (Task) object;
		});
		ScrollPane pane = (ScrollPane) aPane.getContent();
		TabPane tabPane = null;
		switch (aTypeView) {
		case ARCHIVE:
			pane.setContent(viewTasks.getRootPane());
			break;
		case IN:
			pane.setContent(viewTasks.getRootPane());
			break;
		case OUT:
			pane.setContent(viewTasks.getRootPane());
			break;
		case CURRENT_IN:
			tabPane = (TabPane) pane.getContent();
			tabPane.getTabs().get(0).setContent(viewTasks.getRootPane());
			break;
		case CURRENT_OUT:
			tabPane = (TabPane) pane.getContent();
			tabPane.getTabs().get(1).setContent(viewTasks.getRootPane());
			break;
		case EVERYDAY_IN:
			tabPane = (TabPane) pane.getContent();
			tabPane.getTabs().get(0).setContent(viewTasks.getRootPane());
			break;
		case EVERYDAY_OUT:
			tabPane = (TabPane) pane.getContent();
			tabPane.getTabs().get(1).setContent(viewTasks.getRootPane());
			break;
		case CURRENT_ARCHIVE:
			tabPane = (TabPane) pane.getContent();
			tabPane.getTabs().get(3).setContent(viewTasks.getRootPane());
			break;
		case CURRENT_EVERYDAY_IN:
			tabPane = (TabPane) pane.getContent();
			AnchorPane anchorIn = (AnchorPane) tabPane.getTabs().get(2).getContent();
			TabPane currentEverydayInTabPane = (TabPane) anchorIn.getChildren().get(1);
			currentEverydayInTabPane.getTabs().get(0).setContent(viewTasks.getRootPane());
			break;
		case CURRENT_EVERYDAY_OUT:
			tabPane = (TabPane) pane.getContent();
			AnchorPane anchorOut = (AnchorPane) tabPane.getTabs().get(2).getContent();
			TabPane currentEverydayOutTabPane = (TabPane) anchorOut.getChildren().get(1);
			currentEverydayOutTabPane.getTabs().get(1).setContent(viewTasks.getRootPane());
			break;
		}

		viewTasks.setPrefTaskWidth(aPane.prefWidthProperty());
		aPane.setPrefWidth(aPane.getWidth());
		aPane.widthProperty()
				.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
					aPane.prefWidthProperty().set(aPane.getWidth() - 20);
				});

		return viewTasks;
	}

	@Override
	protected WorkbenchViewModel createPresentaionModel() {
		return new WorkbenchViewModel();
	}

	/**
	 * Возвращает панель вкладок
	 *
	 * @return панель вкладок
	 */
	@Override
	public TabPane getTabPane() {
		return workbenchTabPane;
	}

	/**
	 * Возвращает основную вкладку представления
	 *
	 * @return основная вкладка
	 */
	@Override
	public Tab getTab() {
		return workbenchTab;
	}

	/**
	 * Устанавливает значение полю {@link#customTimeIntervalModeHandler}
	 *
	 * @param customTimeIntervalModeHandler значение поля
	 */
	public void setCustomTimeIntervalModeHandler(
			EventHandler<ActionEvent> customTimeIntervalModeHandler) {
		if (null != inTasksView) {
			inTasksView.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != outTaskView) {
			outTaskView.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != archiveTasksView) {
			archiveTasksView.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != currentTasksViewIn) {
			currentTasksViewIn.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != currentTasksViewOut) {
			currentTasksViewOut.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != currentTasksViewArchive) {
			currentTasksViewArchive.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != currentTasksViewEverydayIn) {
			currentTasksViewEverydayIn
					.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != currentTasksViewEverydayOut) {
			currentTasksViewEverydayOut
					.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != everydayIn) {
			everydayIn.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
		if (null != everydayOut) {
			everydayOut.setCustomTimeIntervalModeHandler(customTimeIntervalModeHandler);
		}
	}

	/**
	 * Устанавливает значение полю {@link#openTaskHandler}
	 *
	 * @param openTaskHandler значение поля
	 */
	public void setOpenTaskHandler(EventHandler<MouseEvent> openTaskHandler) {
		if (null != inTasksView) {
			inTasksView.addOpenTaskHandler(openTaskHandler);
		}
		if (null != outTaskView) {
			outTaskView.addOpenTaskHandler(openTaskHandler);
		}
		if (null != archiveTasksView) {
			archiveTasksView.addOpenTaskHandler(openTaskHandler);
		}
		if (null != currentTasksViewIn) {
			currentTasksViewIn.addOpenTaskHandler(openTaskHandler);
		}
		if (null != currentTasksViewOut) {
			currentTasksViewOut.addOpenTaskHandler(openTaskHandler);
		}
		if (null != currentTasksViewArchive) {
			currentTasksViewArchive.addOpenTaskHandler(openTaskHandler);
		}
		if (null != currentTasksViewEverydayIn) {
			currentTasksViewEverydayIn.addOpenTaskHandler(openTaskHandler);
		}
		if (null != currentTasksViewEverydayOut) {
			currentTasksViewEverydayOut.addOpenTaskHandler(openTaskHandler);
		}
		if (null != everydayIn) {
			everydayIn.addOpenTaskHandler(openTaskHandler);
		}
		if (null != everydayOut) {
			everydayOut.addOpenTaskHandler(openTaskHandler);
		}
	}

	/**
	 * Возвращает {@link#selectedTask}
	 *
	 * @return the {@link#selectedTask}
	 */
	public Task getSelectedTask() {
		return selectedTask;
	}

	/**
	 * Открывает панель задач
	 *
	 * @param aPane панель задач
	 */
	public void openTitlePane(TitledPane aPane) {
		if (null != aPane) {
			accordion.setExpandedPane(aPane);
		}
	}

	@Override
	public void refresh() {
		inTasksView.refresh();
		if (null != outTaskView) {
			outTaskView.refresh();
		}
		if (null != archiveTasksView) {
			archiveTasksView.refresh();
		}
		if (null != currentTasksViewIn) {
			currentTasksViewIn.refresh();
		}
		if (null != currentTasksViewOut) {
			currentTasksViewOut.refresh();
		}
		if (null != currentTasksViewArchive) {
			currentTasksViewArchive.refresh();
		}
		if (null != currentTasksViewEverydayIn) {
			currentTasksViewEverydayIn.refresh();
		}
		if (null != currentTasksViewEverydayOut) {
			currentTasksViewEverydayOut.refresh();
		}
		if (null != everydayIn) {
			everydayIn.refresh();
		}
		if (null != everydayOut) {
			everydayOut.refresh();
		}
	}

	/**
	 * Возвращает {@link#subTreeEventHandler}
	 *
	 * @return the {@link#subTreeEventHandler}
	 */
	public SelectionListener getSubTreeEventHandler() {
		return subTreeEventHandler;
	}

	@Override
	public void setStage(Stage aStage) {
		super.setStage(aStage);
		if (null != inTasksView) {
			inTasksView.setStage(aStage);
		}
		if (null != outTaskView) {
			outTaskView.setStage(aStage);
		}
		if (null != archiveTasksView) {
			archiveTasksView.setStage(aStage);
		}
		if (null != currentTasksViewIn) {
			currentTasksViewIn.setStage(aStage);
		}
		if (null != currentTasksViewOut) {
			currentTasksViewOut.setStage(aStage);
		}
		if (null != currentTasksViewArchive) {
			currentTasksViewArchive.setStage(aStage);
		}
		if (null != currentTasksViewEverydayIn) {
			currentTasksViewEverydayIn.setStage(aStage);
		}
		if (null != currentTasksViewEverydayOut) {
			currentTasksViewEverydayOut.setStage(aStage);
		}
		if (null != everydayIn) {
			everydayIn.setStage(aStage);
		}
		if (null != everydayOut) {
			everydayOut.setStage(aStage);
		}
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

}
