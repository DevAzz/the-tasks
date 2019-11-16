package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.sciencesquad.hqtasks.server.utils.TaskType;
import ru.sciencesquad.hqtasks.server.utils.TasksViewType;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.model.TasksViewModel;
import ru.siencesquad.hqtasks.ui.model.TasksViewModel.TaskBackGroundColor;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;
import ru.siencesquad.hqtasks.ui.widgets.CustomTimeIntervalView;
import ru.siencesquad.hqtasks.ui.widgets.PageSettingsView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Представление списка задач
 */
public class TasksView extends AbstractView<TasksViewModel> {

	/** Корневой композит */
	@FXML
	private AnchorPane rootPane;

	/** Корневой композит */
	@FXML
	private AnchorPane buttonPane;

	/** Кнопка "Удалить задачу" */
	@FXML
	private Button deleteTaskButton;

	/** Кнопка "Открыть задачу" */
	@FXML
	private Button openTaskButton;

	/** Свойство ширины рожительского композита */
	private DoubleProperty prefTaskWidth;

	/** Меню "Фильтр по дате" */
	@FXML
	private Menu dateFilterMenu;

	/** Меню "Фильтр по приоритету" */
	@FXML
	private Menu priorityFilterMenu;

	/** Меню "Фильтр по статусу задачи" */
	@FXML
	private Menu statusFilterMenu;

	/** Пункт меню - "Отобразить все задачи" */
	@FXML
	private MenuItem showAll;

	/** Пункт меню фильтра по дате "Выбрать промежуток времени" */
	@FXML
	private CustomMenuItem timeIntervalFilterItem;

	/** Меню сортировки по приоритету */
	@FXML
	private Menu sortByPriorityMenu;

	/** Меню сортировки по приоритету */
	@FXML
	private Menu sortByDateMenu;

	/** Пункт меню сортировки по приоритету "По убыванию" */
	@FXML
	private CheckMenuItem sortByPriorityDescending;

	/** Пункт меню сортировки по приоритету "По возрастанию" */
	@FXML
	private CheckMenuItem sortByPriorityAscending;

	/** Пункт меню сортировки по дате "Сначала новые" */
	@FXML
	private CheckMenuItem sortByDateFirstNewItem;

	/** Пункт меню сортировки по дате "Сначала старые" */
	@FXML
	private CheckMenuItem sortByDateFirstOldItem;

	/** Пункт меню настройки записей "Подсветка записей" */
	@FXML
	private CheckMenuItem illumnationEnableCheckMenuItem;

	/** Скролл панель представления задач */
	@FXML
	private ScrollPane scrollPaneTasksView;

	/** Лейбл процесса загрузки задач */
	@FXML
	private Label loadTasksTitle;

	/** Компонент паджинации */
	@FXML
	private Pagination pagination;

	/** Выбранная задача */
	private Task selectedTask;

	/** Карта страниц задач */
	private Map<Integer, ScrollPane> pageMap = new HashMap<>();

	/** Тип представления */
	private TasksViewType typeView;

	/** Обработчик открытия задачи */
	private EventHandler<MouseEvent> openTaskHandler;

	/** Пункт меню вызова настроек паджинации */
	@FXML
	private CustomMenuItem pageSettingsMenuItem;

	/** Меню активных фильтров */
	@FXML
	private Menu activeFiltersMenu;

	/** Номер страницы удаленной записи */
	private int deletedTaskPageNumber;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(pagination.pageCountProperty(), model.getPageCountProperty());
		Bindings.bindBidirectional(pagination.currentPageIndexProperty(),
				model.getCurrentPageProperty());
		Bindings.bindBidirectional(sortByPriorityDescending.selectedProperty(),
				model.getSortByPriorityDescendingProperty());
		Bindings.bindBidirectional(sortByPriorityAscending.selectedProperty(),
				model.getSortByPriorityAscendingProperty());
		Bindings.bindBidirectional(sortByDateFirstNewItem.selectedProperty(),
				model.getSortByDateFirstNewItemProperty());
		Bindings.bindBidirectional(sortByDateFirstOldItem.selectedProperty(),
				model.getSortByDateFirstOldItemProperty());
		Bindings.bindBidirectional(illumnationEnableCheckMenuItem.selectedProperty(),
				model.getIllEnableCheckProperty());

		bindMenu(dateFilterMenu);
		bindMenu(statusFilterMenu);
		bindMenu(priorityFilterMenu);
		bindSortMenu(sortByDateMenu);
		bindSortMenu(sortByPriorityMenu);

		pagination.setPageFactory(param -> createPage(param));
		pagination.currentPageIndexProperty()
				.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
					load(newValue.intValue());
				});

		deleteTaskButton.setOnMouseClicked(event -> {
			if (null != selectedTask) {
				Boolean result = false;
				try {
					deletedTaskPageNumber = model.getNumberPageByTask(selectedTask);
					result = DialogUtils.getInstance().showRemoveTaskDialog(stage, selectedTask);
				} catch (IOException e) {
					// TODO Логирование
					e.printStackTrace();
				} finally {
					if (result) {
						selectedTask = null;
						fireSelect(null);
					}
				}
			} else {
				DialogUtils.getInstance().showAlertDialog("Невозможно удалить задачу",
						"Задача уже удалена или недоступна", AlertType.INFORMATION);
			}
		});

		try {
			CustomTimeIntervalView customTimeView = Utils.getInstance()
					.loadView(CustomTimeIntervalView.class);
			model.setCustomTimeIntervalModel(customTimeView.getModel());
			customTimeView.setSearchRunnable(model.createSearchRunnable());

			timeIntervalFilterItem.setContent(customTimeView.getCustomFilterDatePanel());
			timeIntervalFilterItem.setHideOnClick(false);
		} catch (IOException e) {
			// TODO Логирование
			e.printStackTrace();
		}

		model.getVisibleTasks().addListener((ListChangeListener<Task>) c -> {
			Platform.runLater(() -> {
				int oldPageCount = pagination.getPageCount();
				int newPageCount = model.getCountPages();
				int currentPageIndex = pagination.getCurrentPageIndex();
				if (oldPageCount < newPageCount) {
					pagination.setPageCount(newPageCount);
					pagination.setCurrentPageIndex(currentPageIndex);
				} else if (oldPageCount > newPageCount) {
					pagination.setPageCount(newPageCount);
					if (currentPageIndex == newPageCount) {
						currentPageIndex--;
						currentPageIndex = (0 > currentPageIndex) ? 0 : currentPageIndex;
					}
					pagination.setCurrentPageIndex(currentPageIndex);
				}

				changeTasks(c);
			});
		});
		initPageSettingsView();

		// Слушатель изменения списка наименований активных фильтров
		model.getNameActiveFiltersList().addListener((ListChangeListener<String>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (String value : c.getAddedSubList()) {
						if ("Фильтры по приоритету:".equals(value)
								|| "Фильтры по статусу:".equals(value)
								|| "Фильтры по дате:".equals(value)) {
							activeFiltersMenu.getItems().add(new SeparatorMenuItem());
						}
						MenuItem item = new MenuItem(value);
						if (!activeFiltersMenu.getItems().contains(item)) {
							activeFiltersMenu.getItems().add(item);
						}

					}
				} else if (c.wasRemoved()) {
					activeFiltersMenu.getItems().clear();
				}
			}

		});
	}

	/**
	 * Инициализирует форму настроек паджинации
	 */
	private void initPageSettingsView() {
		try {
			PageSettingsView pageSettingsView = Utils.getInstance()
					.loadView(PageSettingsView.class);
			Map<String, Integer> defaultValues = new HashMap<>();
			defaultValues.put(PageSettingsView.MIN_PAGES_COUNT_KEY, 1);
			defaultValues.put(PageSettingsView.MAX_PAGES_COUNT_KEY, 20);
			defaultValues.put(PageSettingsView.MIN_PAGE_ENTRIES_COUNT_KEY, 1);
			defaultValues.put(PageSettingsView.MAX_PAGE_ENTRIES_COUNT_KEY, 50);
			defaultValues.put(PageSettingsView.DEFAULT_PAGES_COUNT, 10);
			defaultValues.put(PageSettingsView.DEFAULT_PAGES_ENTRIES_COUNT, 5);
			pageSettingsMenuItem.setContent(pageSettingsView.getRootPane());
			pageSettingsView.initContent(defaultValues, () -> {
				Integer countPages = pageSettingsView.getModel().getCountPagesProperty().get();
				Integer countPageEntries = pageSettingsView.getModel().getCountPageEntriesProperty()
						.get();
				pagination.setMaxPageIndicatorCount(countPages);
				if (countPageEntries != model.getCountPageEntries()) {
					model.setCountPageEntries(countPageEntries);
					load(pagination.getCurrentPageIndex());
				}

			});

		} catch (IOException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Возвращаяет карту элементов меню
	 *
	 * @param aMenu меню
	 * @return карта выделения пунктов меню
	 */
	private Map<String, Boolean> getSelectedMenuItems(Menu aMenu) {
		Map<String, Boolean> result = new HashMap<>();
		for (MenuItem item : aMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				result.put(checkItem.getId(), checkItem.isSelected());
			}
		}
		return result;
	}

	/**
	 * Связывание меню с моделью представления
	 *
	 * @param aMenu меню
	 */
	private void bindMenu(Menu aMenu) {
		for (MenuItem item : aMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				checkItem.setOnAction(
						event -> model.bindFilter(aMenu.getId(), getSelectedMenuItems(aMenu)));
			}
		}
	}

	/**
	 * Свзявание меню сортировки
	 *
	 * @param aMenu меню
	 */
	private void bindSortMenu(Menu aMenu) {
		for (MenuItem item : aMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				checkItem.setOnAction(event -> model.sort(checkItem.getId()));
			}
		}
	}

	/**
	 * Создает страницу задач
	 *
	 * @param aPageNumber номер страницы
	 * @return корневой компонент страницы задач (ScrollPane)
	 */
	private Node createPage(Integer aPageNumber) {
		if (null == pageMap.get(aPageNumber)) {
			AnchorPane pane = new AnchorPane();
			ScrollPane scroll = new ScrollPane();
			VBox box = new VBox();
			Label label = new Label();

			pane.setPrefHeight(200);
			pane.setPrefWidth(200);

			AnchorPane.setTopAnchor(scroll, 36.0);
			AnchorPane.setRightAnchor(scroll, 0.0);
			AnchorPane.setBottomAnchor(scroll, 73.0);
			AnchorPane.setLeftAnchor(scroll, 0.0);

			scroll.setPrefHeight(271.0);
			scroll.setPrefWidth(576.0);
			scroll.setFitToHeight(true);
			scroll.setFitToWidth(true);
			scroll.getStyleClass().add("scroll-paneTaskView");
			scroll.setPadding(new Insets(25.0));

			label.setAlignment(Pos.CENTER);
			label.setPrefHeight(91.0);
			label.setPrefWidth(692.0);
			label.setText("Загрузка задач...");

			box.getChildren().add(label);

			scroll.setContent(box);

			pageMap.put(aPageNumber, scroll);
		}
		return pageMap.get(aPageNumber);
	}

	/**
	 * Загрузка записей
	 */
	public void load(final int aPageNumber) {
		Thread thread = new Thread(() -> {
			Platform.runLater(() -> {
				VBox box = getPageNode(aPageNumber);
				if (null != box) {
					box.getChildren().clear();
				}
			});
			model.paramLoad(typeView, aPageNumber);
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected TasksViewModel createPresentaionModel() {
		return new TasksViewModel();
	}

	/**
	 * Обработка изменения видимого списка задач
	 *
	 * @param pageToGo номер страницы к которой нужно перейти
	 * @param aChange изменение
	 */
	private void changeTasks(Change<? extends Task> aChange) {
		while (aChange.next()) {
			if (aChange.wasAdded()) {
				try {
					@SuppressWarnings("unchecked")
					List<Task> list = (List<Task>) aChange.getAddedSubList();
					ObservableList<Task> addedList = FXCollections
							.synchronizedObservableList(FXCollections.observableArrayList(list));
					for (Task task : addedList) {
						createTaskPanel(task);
					}
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
			} else if (aChange.wasRemoved()) {
				boolean needUpdateFlag = false;
				int pageNumber = 0;
				for (Task task : aChange.getRemoved()) {
					if (model.isTaskRemoved(task)) {
						needUpdateFlag = true;
						pageNumber = deletedTaskPageNumber;
					} else {
						pageNumber = model.getNumberPageByTask(task);
					}
					VBox box = getPageNode(pageNumber);
					if (null != box) {
						for (Node node : new ArrayList<>(box.getChildren())) {
							if (String.valueOf(task.getSuid()).equals(node.getId())) {
								box.getChildren().remove(node);
								model.getColorTaskPropertyMap()
										.remove(String.valueOf(task.getSuid()));
							}
						}
					}
				}
				if (needUpdateFlag) {
					load(pageNumber);
				}
			}
		}

	}

	/**
	 * Загрузка задач
	 */
	public void loadTasksViews() {
		try {
			for (Entry<Integer, ScrollPane> entry : pageMap.entrySet()) {
				((VBox) entry.getValue().getContent()).getChildren().clear();
			}
			for (Task task : new ArrayList<>(model.getVisibleTasks())) {
				createTaskPanel(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Логирование
		}
	}

	/**
	 * Создает панель задачи
	 *
	 * @param task задача
	 * @return панель задачи
	 * @throws IOException в случае ошибки
	 */
	private void createTaskPanel(Task task) {
		if (null != task) {
			try {
				TaskPanelView view = Utils.getInstance().loadView(TaskPanelView.class);
				TitledPane taskRoot = view.getTitledPane();

				// Связываем ширину панели задачи с шириной родительского композита
				Bindings.bindBidirectional(taskRoot.prefWidthProperty(), prefTaskWidth);
				taskRoot.setExpanded(false);
				taskRoot.setId(String.valueOf(task.getSuid()));
				if (model.getIllEnableCheckProperty().get()) {
					taskRoot.setStyle("-fx-border-color: #14D028");
				}
				VBox.setMargin(taskRoot, new Insets(3));
				model.createColorProperty(taskRoot.getId()).addListener(
						(ChangeListener<TaskBackGroundColor>) (observable, oldValue, newValue) -> {
							switch (newValue) {
							case RED:
								taskRoot.setStyle("-fx-border-color: #DD2A2A");
								break;
							case GREEN:
								taskRoot.setStyle("-fx-border-color: #14D028");
								break;
							case YELLOW:
								taskRoot.setStyle("-fx-border-color: #EBD71C");
								break;
							case DOESNT_START:
								taskRoot.setStyle("-fx-border-color: #6fb5f1");
								break;
							case DEFAULT:
								taskRoot.setStyle("-fx-border-color: #4a4e4c");
								break;
							}

						});

				// Слушатель выделения задач. Добавляет или удаляет селекшн
				taskRoot.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if (1 == event.getClickCount()) {
						selectTask(taskRoot);
					} else {
						openTaskHandler.handle(event);
					}
				});

				if (null != view) {
					view.getModel().setTask(task);
					applyTitledPaneStyle(taskRoot, task);
				}

				// Добавление задачи в общий композит
				int numberPage = model.getNumberPageByTask(task);
				VBox box = getPageNode(numberPage);

				if (null != box) {
					boolean flag = false;
					while (!flag) {
						if (!(box.getChildren().size() == model.getCountPageEntries())) {
							flag = true;
						}
						if (!flag) {
							numberPage++;
							box = getPageNode(numberPage);
							if (box == null) {
								ScrollPane pane = (ScrollPane) createPage(numberPage + 1);
								box = (VBox) pane.getContent();
							}
						}
					}
					if (flag) {
						box.getChildren().add(taskRoot);
					}
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}
	}

	/**
	 * Возвращает компонент страницы задач
	 *
	 * @param aPageNumber номер страницы
	 * @return компонент страницы задач
	 */
	private VBox getPageNode(int aPageNumber) {
		VBox result = null;
		ScrollPane pane = pageMap.get(aPageNumber);
		if (null != pane) {
			result = (VBox) pane.getContent();
		}
		return result;
	}

	/**
	 * Применяет стиль к панели задачи в зависимости от статуса и приоритета
	 *
	 * @param aPane панель задачи
	 * @param aTask задача
	 */
	private void applyTitledPaneStyle(TitledPane aPane, Task aTask) {
		if (null != aPane) {
			if (null != aTask.getStatus()) {
				switch (aTask.getStatus()) {
				case DONE:
					aPane.getStyleClass().add("status-complited");
					break;
				case OVERDUE:
					aPane.getStyleClass().add("status-timeoutcomplited");
					break;
				case CLOSED:
					aPane.getStyleClass().add("status-closed");
					break;
				case FAILD:
					aPane.getStyleClass().add("status-faild");
					break;
				case REWORK:
					aPane.getStyleClass().add("status-rework");
					break;
				case WORKING:
					aPane.getStyleClass().add("status-inwork");
					break;
				default:
					break;
				}
			}
			switch (aTask.getPriority()) {
			case CRITICAL:
				aPane.getStyleClass().add("priority-4");
				break;
			case MAJOR:
				aPane.getStyleClass().add("priority-3");
				break;
			case MINOR:
				aPane.getStyleClass().add("priority-2");
				break;
			case TRIVIAL:
				aPane.getStyleClass().add("priority-1");
				break;
			case EVERYDAY:
				aPane.getStyleClass().add("priority-0");
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Устанавливает значение полю {@link #prefTaskWidth}
	 *
	 * @param prefTaskWidth значение поля
	 */
	public void setPrefTaskWidth(DoubleProperty prefTaskWidth) {
		this.prefTaskWidth = prefTaskWidth;
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public AnchorPane getRootPane() {
		return rootPane;
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
	 * Устанавливает значение полю {@link#openTaskHandler}
	 *
	 * @param {@link#openTaskHandler}
	 */
	public void addOpenTaskHandler(EventHandler<MouseEvent> openTaskHandler) {
		openTaskButton.setOnMouseClicked(openTaskHandler);
		this.openTaskHandler = openTaskHandler;

	}

	/**
	 * Отображает все задачи
	 */
	@FXML
	public void showAllTasks() {
		clearMenu(dateFilterMenu);
		clearMenu(priorityFilterMenu);
		clearMenu(statusFilterMenu);
		model.showAllEntries();
	}

	/**
	 * Очищает отметки пунктов выбранного меню
	 *
	 * @param aMenu меню
	 */
	private void clearMenu(Menu aMenu) {
		for (MenuItem item : aMenu.getItems()) {
			if (item instanceof CheckMenuItem) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				checkItem.setSelected(false);
			}
		}
	}

	/**
	 * Устанавливает значение полю {@link#customTimeIntervalModeHandler}
	 *
	 * @param customTimeIntervalModeHandler значение поля
	 */
	public void setCustomTimeIntervalModeHandler(
			EventHandler<ActionEvent> customTimeIntervalModeHandler) {
		timeIntervalFilterItem.addEventHandler(ActionEvent.ACTION, customTimeIntervalModeHandler);
	}

	/**
	 * Осуществляет выбор задачи по панели задачи
	 *
	 * @param taskRoot корневая панель задачи
	 */
	private void selectTask(TitledPane taskRoot) {
		if (null != taskRoot) {
			selectedTask = model.getTaskBySuid(taskRoot.getId());
			if (null != selectedTask) {
				VBox box = getPageNode(model.getNumberPageByTask(selectedTask));
				if (null != box) {
					for (Node value : box.getChildren()) {
						value.getStyleClass().remove("titled-pane-task-panel");
					}

					taskRoot.getStyleClass().add("titled-pane-task-panel");

					// Проверка на автора задачи. Исполнитель не может удалить задачу
					if ((null != selectedTask)
							&& (!TaskType.DEFAULT.equals(selectedTask.getType()))) {
						Boolean isCurrentUserAuthor = selectedTask.getAuthor().getSuid()
								.equals(Utils.getInstance().getCurrentUser().getPositionSuid());
						deleteTaskButton.setVisible(isCurrentUserAuthor);
					}

					fireSelect(selectedTask);
				}
			}
		}
	}

	/**
	 * Устанавливает значение полю "Идентификатор должности, выбранной в дереве
	 * подчиненности"
	 *
	 * @param aPositonSuid идентификатор должности
	 */
	public void setPositionSuid(Long aPositonSuid) {
		model.setPositionSuid(aPositonSuid);
	}

	/**
	 * Возвращает {@link#typeView}
	 *
	 * @return the {@link#typeView}
	 */
	public TasksViewType getTypeView() {
		return typeView;
	}

	/**
	 * Устанавливает значение полю {@link#typeView}
	 *
	 * @param typeView значение поля
	 */
	public void setTypeView(TasksViewType typeView) {
		this.typeView = typeView;
		model.setTypeView(typeView);
	}

	/**
	 * Возвращаяет номер текущей страницы записей
	 *
	 * @return номер текущей страницы записей
	 */
	public Integer getCurrentPageNumber() {
		return pagination.getCurrentPageIndex();
	}
}
