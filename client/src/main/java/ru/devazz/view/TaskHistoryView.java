package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.devazz.model.TaskHistoryViewModel;
import ru.devazz.server.api.model.TaskHistoryModel;
import ru.devazz.utils.Utils;
import ru.devazz.widgets.CustomTimeIntervalView;
import ru.devazz.widgets.PageSettingsView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Представление формы истории задачи
 */
public class TaskHistoryView extends AbstractView<TaskHistoryViewModel> {

	/** Корневая панель представления */
	@FXML
	private AnchorPane rootPane;

	/** Меню "Фильтр по дате" */
	@FXML
	private Menu dateFilterMenu;

	/** Пункт меню - "Отобразить все задачи" */
	@FXML
	private MenuItem showAll;

	/** Пункт меню "Сортировка записей" -> "По дате" -> "Сначала новые" */
	@FXML
	private CheckMenuItem sortByDateFirstNewItem;

	/** Пункт меню "Сортировка записей" -> "По дате" -> "Сначала старые" */
	@FXML
	private CheckMenuItem sortByDateFirstOldItem;

	/** Пункт меню фильтра по дате "Выбрать промежуток времени" */
	@FXML
	private CustomMenuItem timeIntervalFilterItem;

	/** Меню сортировки по дате */
	@FXML
	private Menu sortByDateMenu;

	/** Меню фильтрации по типу записей */
	@FXML
	private Menu typeFilterMenu;

	/** Пункт меню вызова настроек паджинации */
	@FXML
	private CustomMenuItem pageSettingsMenuItem;

	/** Компонент паджинации */
	@FXML
	private Pagination pagination;

	/** Меню активных фильтров */
	@FXML
	private Menu activeFiltersMenu;

	/** Карта страниц задач */
	private Map<Integer, ScrollPane> pageMap = new HashMap<>();

	@Override
	public void initialize() {
		Bindings.bindBidirectional(pagination.pageCountProperty(), model.getPageCountProperty());
		Bindings.bindBidirectional(pagination.currentPageIndexProperty(),
				model.getCurrentPageProperty());
		menuBin();

		pagination.setPageFactory(param -> createPage(param));
		pagination.currentPageIndexProperty()
				.addListener((observable, oldValue, newValue) -> {
					loadPageEntries(pagination.getCurrentPageIndex());
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

		model.getVisibleEntries().addListener((ListChangeListener<TaskHistoryModel>) c -> {
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

				while (c.next()) {
					if (c.wasAdded()) {
						for (TaskHistoryModel entity : FXCollections.synchronizedObservableList(
								FXCollections.observableArrayList(c.getAddedSubList()))) {
							createHistoryEntryPanel(entity);
						}
					} else if (c.wasRemoved()) {
						VBox box = getPageNode(pagination.getCurrentPageIndex());
						if (null != box) {
							box.getChildren().clear();
						}
					}
				}
			});
		});

		initPageSettingsView();

	}

	/**
	 * Связывание пунктов меню с моделью
	 */
	private void menuBin() {
		Bindings.bindBidirectional(sortByDateFirstNewItem.selectedProperty(),
				model.getSortByDateFirstNew());
		Bindings.bindBidirectional(sortByDateFirstOldItem.selectedProperty(),
				model.getSortByDateFirstOld());
		sortByDateFirstNewItem.setOnAction(event -> {
			model.sort(sortByDateFirstNewItem.getId());
		});
		sortByDateFirstOldItem.setOnAction(event -> {
			model.sort(sortByDateFirstOldItem.getId());
		});

		for (MenuItem item : dateFilterMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				switch (checkItem.getId()) {
				case "dayFilterItem":
					Bindings.bindBidirectional(checkItem.selectedProperty(),
							model.getDayFilterSelectedProperty());
					break;
				case "weekFilterItem":
					Bindings.bindBidirectional(checkItem.selectedProperty(),
							model.getWeekFilterSelectedProperty());
					break;
				case "monthFilterItem":
					Bindings.bindBidirectional(checkItem.selectedProperty(),
							model.getMonthFilterSelectedProperty());
					break;
				case "allTimeFilterItem":
					Bindings.bindBidirectional(checkItem.selectedProperty(),
							model.getAllTimeFilterSelectedProperty());
					break;
				default:
					break;
				}
				checkItem.setOnAction(event -> model.dateFilter(checkItem.getId()));
			}
		}

		for (MenuItem item : typeFilterMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				checkItem.setOnAction(
						event -> model.typeFilter(getSelectedMenuItems(typeFilterMenu)));
			}
		}
		// Слушатель изменения списка наименований активных фильтров
		model.getNameActiveFiltersList().addListener((ListChangeListener<String>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (String value : c.getAddedSubList()) {
						if (("Фильтры по типу:".equals(value) || "Фильтр по дате:".equals(value))) {
							activeFiltersMenu.getItems().add(new SeparatorMenuItem());
						}
						MenuItem item = new MenuItem(value);
						activeFiltersMenu.getItems().add(item);

					}
				} else if (c.wasRemoved()) {
					activeFiltersMenu.getItems().clear();
				}
			}

		});
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
	 * Снимает выделение у всех пунктов заданного меню
	 *
	 * @param aMenu заданное меню
	 */
	private void clearMenu(Menu aMenu) {
		for (MenuItem item : aMenu.getItems()) {
			if ((null != item.getId()) && (item instanceof CheckMenuItem)) {
				CheckMenuItem checkItem = (CheckMenuItem) item;
				checkItem.setSelected(false);
			}
		}
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
					loadPageEntries(pagination.getCurrentPageIndex());
				}

			});

		} catch (IOException e) {
			// TODO Логирование
			e.printStackTrace();
		}

	}

	/**
	 * Создает страницу записей
	 *
	 * @param aPageNumber номер страницы
	 */
	private Node createPage(Integer aPageNumber) {
		if (null == pageMap.get(aPageNumber)) {
			ScrollPane pane = new ScrollPane();
			VBox box = new VBox();

			AnchorPane.setTopAnchor(pane, 0.0);
			AnchorPane.setRightAnchor(pane, 0.0);
			AnchorPane.setBottomAnchor(pane, 0.0);
			AnchorPane.setLeftAnchor(pane, 0.0);

			pane.setPrefHeight(391.0);
			pane.setPrefWidth(694.0);
			pane.setFitToHeight(true);
			pane.setFitToWidth(true);
			pane.getStyleClass().add("scroll-paneTaskView");
			pane.setPadding(new Insets(5.0));

			pane.setContent(box);
			pageMap.put(aPageNumber, pane);
		}
		return pageMap.get(aPageNumber);
	}

	/**
	 * Создание панели исторической записи
	 *
	 * @param aEntity историческая запись
	 */
	private void createHistoryEntryPanel(TaskHistoryModel aEntity) {
		try {
			TaskHistoryEntryPanelView panel = Utils.getInstance()
					.loadView(TaskHistoryEntryPanelView.class);
			panel.getModel().setEntity(aEntity);

			TitledPane taskRoot = panel.getRootPane();
			taskRoot.setExpanded(false);
			taskRoot.setId(String.valueOf(aEntity.getSuid()));

			// Слушатель выделения задач. Добавляет или удаляет селекшн
			taskRoot.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				if (1 == event.getClickCount()) {
					selectTask(taskRoot);
				}
			});

			// Добавление задачи в общий композит
			int numberPage = model.getNumberPageByTask(aEntity);
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

		} catch (IOException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	@Override
	protected TaskHistoryViewModel createPresentaionModel() {
		return new TaskHistoryViewModel();
	}

	/**
	 * Возвращает {@link#rootScrollPane}
	 *
	 * @return the {@link#rootScrollPane}
	 */
	public AnchorPane getRootPane() {
		return rootPane;
	}

	/**
	 * Осуществляет выбор задачи по панели задачи
	 *
	 * @param taskRoot корневая панель задачи
	 */
	private void selectTask(TitledPane taskRoot) {
		if (null != taskRoot) {
			TaskHistoryModel entry = model.getHistoryEntryBySuid(Long.getLong(taskRoot.getId()));
			if (null != entry) {
				VBox box = getPageNode(model.getNumberPageByTask(entry));
				if (null != box) {
					for (Node value : box.getChildren()) {
						value.getStyleClass().remove("titled-pane-task-panel");
					}
					taskRoot.getStyleClass().add("titled-pane-task-panel");
				}
			}
		}
	}

	/**
	 * Отображает все задачи
	 */
	@FXML
	public void showAllEntries() {
		clearMenu(typeFilterMenu);
		model.showAllEntries();
	}

	/**
	 * Загрузка страницы записей
	 *
	 * @param aPageNumber номер страницы записей
	 */
	public void loadPageEntries(Integer aPageNumber) {
		VBox box = getPageNode(aPageNumber);
		if (null != box) {
			box.getChildren().clear();
		}
		model.loadPageEntries(aPageNumber);
	}

	/**
	 * Возвращает компонент страницы исторических записей
	 *
	 * @param aPageNumber номер страницы
	 * @return компонент страницы исторических записей
	 */
	private VBox getPageNode(int aPageNumber) {
		VBox box = null;
		if (null != pageMap.get(aPageNumber)) {
			box = (VBox) pageMap.get(aPageNumber).getContent();
		}
		return box;
	}

}
