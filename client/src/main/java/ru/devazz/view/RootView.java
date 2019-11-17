package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.devazz.entities.ExtSearchRes;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.entities.Task;
import ru.devazz.model.RootViewPresentationModel;
import ru.devazz.server.EJBProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.server.api.model.enums.TaskPriority;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.UserRoles;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.EventType;
import ru.devazz.utils.SplitViewEnum;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Представление основного окна
 */
public class RootView extends AbstractView<RootViewPresentationModel> {

	/** Врехняя панель окна с управляющими кнопками (Вместо виндовой рамки) */
	@FXML
	private AnchorPane windowBorder;

	/** Корневой композит */
	@FXML
	private AnchorPane rootComposite;

	/** Лейбл для изменения размера окна */
	@FXML
	private Label sResizeLabel, nResizeLabel;

	/** Лейбл для изменения размера окна */
	@FXML
	private Label seResizeLabel;

	/** Лейбл для изменения размера окна */
	@FXML
	private Label eResizeLabel;
	/** Лейбл для изменения размера окна */

	@FXML
	private Label wResizeLabel;

	/** Лейбл для изменения размера окна */
	@FXML
	private Label swResizeLabel;

	/** Координаты окна до перемещения */
	private double oldMousePositionX, oldMousePositionY;

	/** Размеры окна */
	private double windowHeigt, windowWidth, stagePos, stagePosY;

	/** Крайняя левая горизонтальная сплит-панель */
	@FXML
	private SplitPane leftHorizontalSplitPane;

	/** Правая сплит-панель */
	@FXML
	private SplitPane rightSplitPane;

	/** Общая сплит-панель */
	@FXML
	private SplitPane commonSplitPane;

	/** Поле поиска */
	@FXML
	private TextField searchBox;

	/** Представление дерева подчиненности */
	private SubordinationTreeView subTreeView;

	/** Представление индикатора событий */
	private EventIndicatorView eventIndicatorView;

	/** Представление информации о подразделении */
	private SubInfoView subInfoView;

	/** Представление книги должностных лиц */
	private PositionBookView positionBookView;

	/** Представление расширенного поиска */
	private ExtendedSearchView extendedSearchView;

	/** Представление журнала событий */
	private EventJournalView eventJournalView;

	/** Представление рабочоего стола */
	private WorkbenchView workbenchView;

	/** Представление отчета по задачам */
	private ReportView reportView;

	/** Представление сводки по задачам */
	private SummaryView summaryView;

	/** Представление легенды */
	private LegendOfIconsView legendView;

	/** Пункт меню "Вид" -> "Информация о подразделении" */
	@FXML
	private RadioMenuItem subInfoMenuItem;

	/** Пункт меню "Вид" -> "Дерево подчиненности" */
	@FXML
	private RadioMenuItem subTreeMenuItem;

	/** Пункт меню "Вид" -> "Индикатор событий" */
	@FXML
	private RadioMenuItem eventViewMenuItem;

	/** Пункт меню "Вид" -> "Книга должностных лиц" */
	@FXML
	private RadioMenuItem positionBookMenuItem;

	/** Пункт меню "Вид" -> "Журнал событий" */
	@FXML
	private RadioMenuItem eventJournalViewMenuItem;

	/** Пункт меню "Вид" -> "Легенда обозначений" */
	@FXML
	private RadioMenuItem legendOfIconsViewMenuItem;

	/** Пункт меню "Вид" -> "Рабочий стол" */
	@FXML
	private RadioMenuItem workbenchMenuItem;

	/** Пункт меню "Вид" -> "Отчет по задачам" */
	@FXML
	private RadioMenuItem reportMenuItem;

	/** Пункт меню "Вид" -> "Сводка по задачам" */
	@FXML
	private RadioMenuItem summaryTasksMenuItem;

	/** Пункт меню "Файл" -> "Создать задачу" */
	@FXML
	private MenuItem createTask;

	/** Лейбл даты и времени */
	@FXML
	private Label dateTimeLabel;

	/** Общий компонент вкладок */
	@FXML
	private TabPane commonCentralTabPane;

	/** Меню "Вид" */
	@FXML
	private Menu viewMenu;

	/** Меню "Файл" */
	@FXML
	private Menu fileMenu;

	/** Лейбл имени пользователя */
	@FXML
	private Label userNameLabel;

	/** Координаты мыши */
	private double mousePointX, mousePointY;

	/** Координаты окна */
	private double windowPointX, windowPointY;

	/** Карта соответствия сплит-панели и позиции разделителя */
	private Map<SplitViewEnum, Double> mapSplitPanePosition = new HashMap<>();

	/** Карта открытых вкладок */
	private Map<Long, CurrentTaskView> openTaskMap = new HashMap<>();

	/** Признак изменения размера окна */
	private Boolean topResize = false;

	/**
	 * Метод сохраняет координаты мыши при клике и удержании на верхней панели окна
	 */
	@FXML
	public void moveWindowMousePress() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		mousePointX = location.getX();
		mousePointY = location.getY();
		windowPointX = stage.getX();
		windowPointY = stage.getY();
	}

	@Override
	protected RootViewPresentationModel createPresentaionModel() {
		return new RootViewPresentationModel();
	}

	/**
	 * Редактирование строки поиска
	 */
	@FXML
	public void editSearchBox() {
		System.out.println(model.getSearchBoxTextProperty().get());
	}

	@Override
	public void initialize() {
		try {
			initViews();

			bind();

			// Записываем текущее положение разделителя для кадждой панели
			leftHorizontalSplitPane.getDividers().get(0).positionProperty()
					.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
						mapSplitPanePosition.put(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL,
								oldValue.doubleValue());
					});
			rightSplitPane.getDividers().get(0).positionProperty()
					.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
						mapSplitPanePosition.put(SplitViewEnum.RIGHT_SPLIT_PANEL,
								oldValue.doubleValue());
					});
			commonSplitPane.getDividers().get(0).positionProperty()
					.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
						mapSplitPanePosition.put(SplitViewEnum.COMMON_SPLIT_PANEL,
								oldValue.doubleValue());
						if ((null != subTreeView) && (subTreeView.getDisableDivider())) {
							subTreeView.getDivider().setPosition(1);
							disableSplitDivider(SplitViewEnum.LEFT_VERTICAL_SPLIT_PANEL, true);
						}
					});

			// Переключение с кнопок управления фокуса на основное окно
			closeStage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
				windowBorder.requestFocus();
			});
			minimizedStage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
				windowBorder.requestFocus();
			});
			maximizedStage.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
				windowBorder.requestFocus();
			});

			// Слушатель перемещения окна
			windowBorder.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
				if (!stage.isMaximized() && !(closeStage.isFocused() || maximizedStage.isFocused()
						|| minimizedStage.isFocused())) {
					if (!topResize) {
						Point location = MouseInfo.getPointerInfo().getLocation();
						stage.setX((int) (windowPointX + (location.getX() - mousePointX)));
						stage.setY((int) (windowPointY + (location.getY() - mousePointY)));
					}
				}
			});

			// Разворачиваем окно на весь экран при двойном клике
			windowBorder.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				if (2 == event.getClickCount()) {
					setMaximized();
				}
			});

			// Принудительно устанавливаем position правому разделителю при сдвиге общего
			// разделителя если правый свернут
			commonSplitPane.getDividers().get(0).positionProperty()
					.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
						if (!subInfoMenuItem.isSelected()
								&& !legendOfIconsViewMenuItem.isSelected()) {
							rightSplitPane.getDividers().get(0).setPosition(1);
							mapSplitPanePosition.put(SplitViewEnum.RIGHT_SPLIT_PANEL,
									new Double(1));
						}

					});

			// изменения стиля курсорв при развёртывании окна (остальные методы реализуют то
			// же самое)
			sResizeLabel.addEventHandler(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(sResizeLabel, "s_resize"));

			nResizeLabel.addEventFilter(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(nResizeLabel, "n_resize"));

			eResizeLabel.addEventFilter(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(eResizeLabel, "e_resize"));

			wResizeLabel.addEventFilter(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(wResizeLabel, "w_resize"));

			swResizeLabel.addEventFilter(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(swResizeLabel, "sw_resize"));

			seResizeLabel.addEventFilter(MouseEvent.MOUSE_ENTERED,
					new CursorTypeChangeListner(seResizeLabel, "se_resize"));

			// Слушатель нажатия Enter для поля ввода
			searchBox.setOnKeyPressed(event -> {
				KeyCode code = event.getCode();
				if (KeyCode.ENTER.equals(code)) {
					try {
						showExtendedResViewByTasks(searchBox.getText());
					} catch (Exception e) {
						// TODO логирование
						e.printStackTrace();
					}
				}

			});

			// Инициализация меню
			Boolean access = Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT);
			if (!access) {
				viewMenu.getItems().remove(subTreeMenuItem);
				viewMenu.getItems().remove(reportMenuItem);
				fileMenu.getItems().remove(createTask);
			}

			UserModel user = Utils.getInstance().getCurrentUser();
			userNameLabel.setText(user.getPosition() + " " + user.getName());
			SplitPane sPane = (SplitPane) rightSplitPane.getItems().get(1);
			sPane.getDividers().get(0).setPosition(.73);
			// Инициализация представлений
			showSubInfo();
			showSubTree();
			showEventIndicator();
			showWorkbenchView();
			showIconsLegend();
		} catch (Exception e) {
			// TODO сделать логирование
			e.printStackTrace();
		}

	}

	/**
	 * Связывание
	 */
	public void bind() {
		// Binding
		Bindings.bindBidirectional(searchBox.textProperty(), model.getSearchBoxTextProperty());
		Bindings.bindBidirectional(dateTimeLabel.textProperty(), model.getDateTimeTextProperty());
	}

	/**
	 * Отображает представление сводки по задачам
	 *
	 * @throws Exception в слчае ошибки
	 */
	@FXML
	public void showSummaryTasksView() throws Exception {
		Boolean menuSelected = summaryTasksMenuItem.isSelected();
		if (menuSelected) {
			summaryView = Utils.getInstance().loadView(SummaryView.class);
			summaryView.setOpenTaskHandler(new OpenTaskInSummaryHandler());
			summaryView.setStage(stage);

			summaryView.addCloseListener(() -> {
				summaryTasksMenuItem.setSelected(false);
			});
			commonCentralTabPane.getTabs().add(summaryView.getTab());
			commonCentralTabPane.getSelectionModel().select(summaryView.getTab());
			summaryView.getModel().setOpenFlagValue(true);
		} else {
			Tab summaryTab = summaryView.getTab();
			if (null != summaryTab) {
				commonCentralTabPane.getTabs().remove(summaryTab);
				summaryView.closeTabView();
			}
		}
	}

	/**
	 * Отображает представление отчета по задачам
	 *
	 * @throws Exception в случае ошибки
	 */
	@FXML
	public void showReportView() throws Exception {
		Boolean menuSelected = reportMenuItem.isSelected();
		if (menuSelected) {
			reportView = Utils.getInstance().loadView(ReportView.class);
			reportView.setStage(stage);

			reportView.addCloseListener(() -> {
				reportMenuItem.setSelected(false);
			});
			commonCentralTabPane.getTabs().add(reportView.getTab());
			commonCentralTabPane.getSelectionModel().select(reportView.getTab());
			reportView.getModel().setOpenFlagValue(true);
		} else {
			commonCentralTabPane.getTabs().remove(reportView.getTab());
			reportView.closeTabView();
		}
	}

	/**
	 * Отображает представление рабочего стола
	 */
	@FXML
	public void showWorkbenchView() throws Exception {
		Boolean menuSelected = workbenchMenuItem.isSelected();
		if (menuSelected) {
			workbenchView = Utils.getInstance().loadView(WorkbenchView.class);
			workbenchView.setStage(getStage());
			workbenchView.getModel()
					.setPositionSuid((null != subTreeView.getSelection())
							? subTreeView.getSelection().getSuid()
							: null);
			Long positionSuid = (null != subTreeView.getSelection())
					? subTreeView.getSelection().getSuid()
					: subTreeView.getModel().getRootElement().getSuid();
			workbenchView.setPositionSuid(positionSuid);
			workbenchView.initViews();
			workbenchView.setStage(getStage());
			workbenchView.getModel().connectToJMSService();
			commonCentralTabPane.getTabs().add(workbenchView.getTab());
			commonCentralTabPane.getSelectionModel().select(workbenchView.getTab());

			workbenchView.getModel().setGoOverTaskListener(new GoOverTaskListener());
			workbenchView.addCloseListener(() -> {
				workbenchMenuItem.setSelected(false);
			});

			workbenchView.setOpenTaskHandler(event -> {
				try {
					Boolean doneFlag = (null != workbenchView.getSelectedTask()) && TaskStatus.DONE
							.equals(workbenchView.getSelectedTask().getStatus());
					showCurrentTaskView(workbenchView.getSelectedTask(), false, doneFlag);
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
			});

			Platform.runLater(() -> {
				if ((null != subTreeView) && (null != subTreeView.getRoot())) {
					String name = (null == subTreeView.getSelection())
							? subTreeView.getRoot().getValue().getName()
							: subTreeView.getSelection().getName();
					workbenchView.setCurrentTasksTitle(name);
				}
			});

			workbenchView.setCustomTimeIntervalModeHandler(event -> {
				if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
					rightSplitPane.getDividers().get(0).setPosition(0.7);
				}
				commonSplitPane.getDividers().get(0).setPosition(0.2);
			});

			if (!subInfoMenuItem.isSelected()) {
				rightSplitPane.getDividers().get(0).setPosition(1);
			} else if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
				rightSplitPane.getDividers().get(0).setPosition(0.6);
			}

			workbenchView.getModel().setOpenFlagValue(true);
		} else {
			commonCentralTabPane.getTabs().remove(workbenchView.getTab());
			workbenchView.closeTabView();
		}
	}

	/**
	 * Отображает вкладку расширенного поиска
	 *
	 * @throws Exception
	 */
	@FXML
	public void showExtendedSearchView() throws Exception {
		if (!extendedSearchView.getModel().getOpenViewFlag().get()) {
			extendedSearchView = Utils.getInstance().loadView(ExtendedSearchView.class);
			extendedSearchView.setDoubleClickHandler(event -> {
				ExtSearchRes res = extendedSearchView.getModel().getSelectedResult();
				IEntity entity = res.getEntity();
				if (entity instanceof TaskModel) {
					try {
						TaskModel task = EJBProxyFactory.getInstance()
								.getService(ITaskService.class).get(entity.getSuid());
						showCurrentTaskView(EntityConverter.getInstatnce()
								.convertTaskModelToClientWrapTask(task), false, false);
					} catch (Exception e) {
						// TODO Логирование
						e.printStackTrace();
					}
				} else if (entity instanceof UserModel) {
					showPositionBookWithSelection(entity.getSuid());
				} else if (entity instanceof SubordinationElementModel) {
					showPositionBookWithSelectionSubEl(entity.getSuid());
				}

			});
			commonCentralTabPane.getTabs().add(extendedSearchView.getTab());
			commonCentralTabPane.getSelectionModel().select(extendedSearchView.getTab());

			if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
				rightSplitPane.setDividerPositions(0.7);
				commonSplitPane.getDividers().get(0).setPosition(0.2);
			}

			extendedSearchView.getModel().setOpenFlagValue(true);

			if (!searchBox.getText().isEmpty()) {
				extendedSearchView.getModel().setTaskNameProperty(searchBox.getText());
				extendedSearchView.showExtendedSearchResults();
			}
		}
		if (!subTreeView.getModel().getOpenViewFlag().get()
				&& !eventIndicatorView.getModel().getOpenViewFlag().get()) {
			commonSplitPane.setDividerPositions(0);
		}
	}

	/**
	 * Отображает журнал событий
	 *
	 * @throws Exception в случае ошибки
	 */
	@FXML
	public void showEventJournalView() throws Exception {
		Boolean menuSelected = eventJournalViewMenuItem.isSelected();
		if (menuSelected) {
			if (!commonCentralTabPane.getTabs().contains(eventJournalView.getTab())) {
				eventJournalView = Utils.getInstance().loadView(EventJournalView.class);
				eventJournalView.setStage(getStage());
				eventJournalView.getModel().connectToJMSService();
				eventJournalView.addCloseListener(() -> {
					eventJournalViewMenuItem.setSelected(false);
				});
				commonCentralTabPane.getTabs().add(eventJournalView.getTab());
				commonCentralTabPane.getSelectionModel().select(eventJournalView.getTab());
				if (!subInfoMenuItem.isSelected()) {
					rightSplitPane.getDividers().get(0).setPosition(1);
				} else if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
					rightSplitPane.getDividers().get(0).setPosition(0.6);
				}
				eventJournalView.getModel().setOpenFlagValue(true);
			}
		} else {
			commonCentralTabPane.getTabs().remove(eventJournalView.getTab());
			eventJournalView.closeTabView();
		}
	}

	/**
	 * Отображает представление задачи
	 *
	 * @param aTask задача
	 * @param aCreateFlag флаг создания задачи
	 * @param aDoneFlag флаг завершения задачи
	 * @throws Exception в случае ошибки
	 */
	public void showCurrentTaskView(Task aTask, Boolean aCreateFlag, Boolean aDoneFlag)
			throws Exception {
		if (null != aTask) {
			if (!openTaskMap.containsKey(aTask.getSuid())) {
				CurrentTaskView currentTaskView = Utils.getInstance()
						.loadView(CurrentTaskView.class);

				if (!aCreateFlag) {
					String titleTab = Utils.getInstance().ellipsString(aTask.getName(), 30);
					currentTaskView.getTab().setText(titleTab);
				} else {
					aTask.setAuthor(Utils.getInstance().getCurrentUserSubEl());
					currentTaskView.getTab().setText("Создать задачу");
				}

				currentTaskView.initCycleView();
				currentTaskView.getModel().setTask(aTask);
				currentTaskView.getModel().setCreateFlagValue(aCreateFlag);
				currentTaskView.getModel().initTaskModel();
				currentTaskView.setStage(getStage());

				currentTaskView.setCommonTabPane(commonCentralTabPane);
				currentTaskView.initContent();

				commonCentralTabPane.getTabs().add(currentTaskView.getTab());

				commonCentralTabPane.getSelectionModel().select(currentTaskView.getTab());

				currentTaskView.addCloseListener(() -> {
					openTaskMap.remove(aTask.getSuid());
				});

				if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
					rightSplitPane.getDividers().get(0).setPosition(0.7);
				}
				commonSplitPane.getDividers().get(0).setPosition(0.2);
				openTaskMap.put(aTask.getSuid(), currentTaskView);
				currentTaskView.getModel().setOpenFlagValue(true);
			} else {
				commonCentralTabPane.getSelectionModel()
						.select(openTaskMap.get(aTask.getSuid()).getTab());
			}
		} else {
			DialogUtils.getInstance().showAlertDialog("Невозможно открыть задачу",
													  "Задача удалена или недоступна", AlertType.INFORMATION);
		}

	}

	/**
	 * Отображает книгу должностных лиц
	 *
	 * @throws Exception в случае ошибки
	 */
	@FXML
	public void showPositionBookView() throws Exception {
		Boolean menuSelected = positionBookMenuItem.isSelected();
		if (menuSelected) {
			positionBookView = Utils.getInstance().loadView(PositionBookView.class);
			commonCentralTabPane.getTabs().add(positionBookView.getTab());
			commonCentralTabPane.getSelectionModel().select(positionBookView.getTab());
			positionBookView.addCloseListener(() -> {
				positionBookMenuItem.setSelected(false);
			});
			if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
				rightSplitPane.getDividers().get(0).setPosition(0.6);
			}
			positionBookView.getModel().setOpenFlagValue(true);
		} else {
			commonCentralTabPane.getTabs().remove(positionBookView.getTab());
			positionBookView.closeTabView();
		}
		positionBookView.getModel().getOpenViewFlag().set(true);
	}

	/**
	 * Отображает дерево подчиненности
	 *
	 * @throws IOException в случае ошибки отсутствия файла описания представления
	 */
	@FXML
	public void showSubTree() throws Exception {
		if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
			Boolean menuSelected = subTreeMenuItem.isSelected();
			if (menuSelected) {
				subTreeView = Utils.getInstance().loadView(SubordinationTreeView.class);
				AnchorPane pane = (AnchorPane) leftHorizontalSplitPane.getItems().get(0);
				if (null != pane) {
					if (!pane.getChildren().isEmpty()) {
						pane.getChildren().clear();
					}
					pane.getChildren().add(subTreeView.getTabPane());

					// Слушатель выбора элемента в дереве подчиненности
					subTreeView.addSelectionListener(aOjbect -> {
						SubordinationElement element = (SubordinationElement) aOjbect;
						subInfoView.getModel().setSelectionSub(element);
						workbenchView.getSubTreeEventHandler().fireSelect(aOjbect);
						workbenchView.setCurrentTasksTitle(element.getName());
					});

					subTreeView.setCreateTaskHandler(event -> {
						Task task = new Task(0L, "", "", "Описание", null, TaskPriority.CRITICAL,
											 (double) 0, null, null);
						task.setExecutor(subInfoView.getModel().getSelectionSub());
						try {
							showCurrentTaskView(task, true, false);
						} catch (Exception e) {
							// TODO Логирование
							e.printStackTrace();
						}

					});
					subTreeView.setGenerateReportHandler(event -> {
						showReportViewWithSubEls(subTreeView.getModel().getSelectedElement());
					});

					// Слушатель закрытия таба. По закрытию таба опускаем сплит
					subTreeView.addCloseListener(() -> {
						subTreeMenuItem.setSelected(false);
						leftHorizontalSplitPane.getDividers().get(0).setPosition(0);
						disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);

						if (!subInfoMenuItem.isSelected()) {
							rightSplitPane.getDividers().get(0).setPosition(1);
						}

						if (!eventViewMenuItem.isSelected()) {
							leftHorizontalSplitPane.getDividers().get(0).setPosition(1);
							disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);

							// Устанавливаем общую сплит-панель в открытое положение
							commonSplitPane.getDividers().get(0).setPosition(0);
							disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, true);
						}
					});

					if (!eventViewMenuItem.isSelected()) {
						leftHorizontalSplitPane.getDividers().get(0).setPosition(1);
						disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);

						mapSplitPanePosition.put(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL,
												 1d);
						disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, false);
					} else {
						// Поднимаем сплит при открытии view
						leftHorizontalSplitPane.getDividers().get(0).setPosition(0.7);
						disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, false);
					}

					// Устанавливаем общую сплит-панель в открытое положение
					commonSplitPane.getDividers().get(0).setPosition(0.3);
					mapSplitPanePosition.put(SplitViewEnum.COMMON_SPLIT_PANEL, 0.3);
					subTreeView.getModel().setOpenFlagValue(true);
				}
			} else if (null != subTreeView) {
				if (!eventViewMenuItem.isSelected()) {
					leftHorizontalSplitPane.getDividers().get(0).setPosition(1);
					disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);

					// Устанавливаем общую сплит-панель в открытое положение
					commonSplitPane.getDividers().get(0).setPosition(0);
					disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, true);
				}

				subTreeView.closeTabView();
			}
		} else {
			leftHorizontalSplitPane.getDividers().get(0).setPosition(0);
			disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);
			subTreeMenuItem.setSelected(false);
		}
	}

	/**
	 * Отображает {@link #subInfoView}
	 *
	 * @throws IOException в случае ошибки отсутствия файла описания представления
	 */
	@FXML
	public void showSubInfo() throws Exception {
		Boolean menuSelected = subInfoMenuItem.isSelected();
		if (menuSelected) {
			subInfoView = Utils.getInstance().loadView(SubInfoView.class);
			SplitPane sPane = (SplitPane) rightSplitPane.getItems().get(1);
			AnchorPane pane = (AnchorPane) sPane.getItems().get(0);
			if (null != pane) {
				if (!pane.getChildren().isEmpty()) {
					pane.getChildren().clear();
				}
				pane.getChildren().add(subInfoView.getTabPane());
			}

			ISubordinationElementService subelService = EJBProxyFactory.getInstance()
					.getService(ISubordinationElementService.class);
			SubordinationElement element = (null != subTreeView.getSelection())
					? subTreeView.getSelection()
					: EntityConverter.getInstatnce().convertSubElEntityToClientWrap(subelService
							.get(Utils.getInstance().getCurrentUser().getPositionSuid()));
			subInfoView.getModel().setSelectionSub(element);

			subInfoView.addCloseListener(() -> {
				subInfoMenuItem.setSelected(false);
				sPane.getDividers().get(0).setPosition(0);
				if (!legendOfIconsViewMenuItem.isSelected()) {
					rightSplitPane.getDividers().get(0).setPosition(1);
					disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, true);
				} else {
					disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, false);
					disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, true);
				}
			});
			if (legendOfIconsViewMenuItem.isSelected()) {
				subInfoView.getModel().setSelectionSub(subTreeView.getSelection());
				sPane.getDividers().get(0).setPosition(0.73);
				disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, false);
				disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, false);
			} else {
				sPane.getDividers().get(0).setPosition(1);
				rightSplitPane.getDividers().get(0).setPosition(0.6);
			}
			subInfoView.getModel().setOpenFlagValue(true);
			disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, false);
		} else {
			if (null != subInfoView) {
				subInfoView.closeTabView();
				SplitPane sPane = (SplitPane) rightSplitPane.getItems().get(1);
				sPane.getDividers().get(0).setPosition(0);
				disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, true);
			}
			if (!legendOfIconsViewMenuItem.isSelected()) {
				rightSplitPane.getDividers().get(0).setPosition(1);
			}
		}
	}

	/**
	 * Отображает легенду иконок
	 *
	 * @throws Exception - в случае ошибки отсутсвия файла
	 */
	@FXML
	public void showIconsLegend() throws Exception {
		Boolean menuSelected = legendOfIconsViewMenuItem.isSelected();
		if (menuSelected) {
			legendView = Utils.getInstance().loadView(LegendOfIconsView.class);
			SplitPane sPane = (SplitPane) rightSplitPane.getItems().get(1);
			AnchorPane pane = (AnchorPane) sPane.getItems().get(1);
			if (null != pane) {
				if (!pane.getChildren().isEmpty()) {
					pane.getChildren().clear();
				}
				pane.getChildren().add(legendView.getTabPane());
			}
			if (subInfoMenuItem.isSelected()) {
				sPane.getDividers().get(0).setPosition(0.73);
				disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, false);
			} else {
				sPane.getDividers().get(0).setPosition(0);
				rightSplitPane.getDividers().get(0).setPosition(0.6);
			}
			legendView.getTabPane().getTabs().addListener((ListChangeListener<? super Tab>) c -> {
				if (legendView.getTabPane().getTabs().isEmpty()) {
					sPane.getDividers().get(0).setPosition(1);
					legendOfIconsViewMenuItem.setSelected(false);
					disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, true);
					if ((!subInfoMenuItem.isSelected()) || (null == subInfoView)) {
						rightSplitPane.getDividers().get(0).setPosition(1);
					}
					if (!subInfoMenuItem.isSelected()) {
						disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, true);
					} else {
						disableSplitDivider(SplitViewEnum.RIGHT_SPLIT_PANEL, false);
					}
				}
			});
		} else {
			if (null != legendView) {
				legendView.closeTabView();
				SplitPane sPane = (SplitPane) rightSplitPane.getItems().get(1);
				sPane.getDividers().get(0).setPosition(1);
				disableSplitDivider(SplitViewEnum.RIGHT_HORIZONTAL_SPLIT_PANEL, true);
			}
			if ((!subInfoMenuItem.isSelected()) || (null == subInfoView)) {
				rightSplitPane.getDividers().get(0).setPosition(1);
			}
		}
	}

	/**
	 * Отображает индикатор событий
	 *
	 * @throws IOException в случае ошибки отсутствия файла описания представления
	 */
	@FXML
	public void showEventIndicator() throws Exception {
		Boolean menuSelected = eventViewMenuItem.isSelected();
		if (menuSelected) {
			eventIndicatorView = Utils.getInstance().loadView(EventIndicatorView.class);
			AnchorPane pane = (AnchorPane) leftHorizontalSplitPane.getItems().get(1);
			if (null != pane) {
				eventIndicatorView.getModel().connectToJMSService();
				// Слушатель закрытия таба. По закрытию таба опускаем сплит
				eventIndicatorView.addCloseListener(() -> {
					eventViewMenuItem.setSelected(false);
					leftHorizontalSplitPane.getDividers().get(0).setPosition(1);
					disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);

					if (!subInfoMenuItem.isSelected()) {
						rightSplitPane.getDividers().get(0).setPosition(1);
					}

					if (!subTreeMenuItem.isSelected()) {
						leftHorizontalSplitPane.getDividers().get(0).setPosition(0);
						disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);
						// Устанавливаем общую сплит-панель в закрытое положение
						commonSplitPane.getDividers().get(0).setPosition(0);
						disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, true);
					}
				});

				// Добавляем слушателей для пунктов контекстного меню
				eventIndicatorView.setTaskItemClickHandler(event -> {
					try {
						ru.devazz.entities.Event eventValue = eventIndicatorView.getSelection();
						Task task = EntityConverter.getInstatnce()
								.convertTaskModelToClientWrapTask(EJBProxyFactory.getInstance()
										.getService(ITaskService.class)
										.get(eventValue.getTaskId()));
						if (null != eventValue.getEventType()) {
							if (eventValue.getEventType() == EventType.DONE) {
								showCurrentTaskView(task, false, true);
							} else {
								showCurrentTaskView(task, false, false);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				eventIndicatorView.setJournalItemClickHandler(event -> {
					showEventJournalWithSelection(eventIndicatorView.getSelection());
				});
				eventIndicatorView.setDoubleClickHandler(event -> {
					if (2 == event.getClickCount()) {
						showEventJournalWithSelection(eventIndicatorView.getSelection());
					}
				});

				if (!subTreeMenuItem.isSelected()) {
					leftHorizontalSplitPane.getDividers().get(0).setPosition(0);
					disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);
					disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, false);
				} else {
					final double pos = 0.9;
					// Поднимаем сплит при открытии view
					if (pos < leftHorizontalSplitPane.getDividers().get(0).getPosition()) {
						if (Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT)) {
							leftHorizontalSplitPane.getDividers().get(0).setPosition(0.7);
							disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, false);
						}
					}
				}

				if (!pane.getChildren().isEmpty()) {
					pane.getChildren().clear();
				}
				pane.getChildren().add(eventIndicatorView.getTabPane());
				// Устанавливаем общую сплит-панель в открытое положение
				commonSplitPane.getDividers().get(0).setPosition(0.3);
				eventIndicatorView.getModel().setOpenFlagValue(true);
			}
		} else if (null != eventIndicatorView) {
			if (!subTreeMenuItem.isSelected()) {
				leftHorizontalSplitPane.getDividers().get(0).setPosition(0);
				disableSplitDivider(SplitViewEnum.LEFT_HORIZONTAL_SPLIT_PANEL, true);
				// Устанавливаем общую сплит-панель в закрытое положение
				commonSplitPane.getDividers().get(0).setPosition(0);
				disableSplitDivider(SplitViewEnum.COMMON_SPLIT_PANEL, true);
			}

			eventIndicatorView.closeTabView();
		}
	}

	/**
	 * Открывает представление книги должностных лиц с выделением пользователя по
	 * идентификатору
	 *
	 * @param aUserSuid идентификатор пользователя
	 */
	private void showPositionBookWithSelection(Long aUserSuid) {
		try {
			if (positionBookView.isViewOpen()) {
				commonCentralTabPane.getSelectionModel().select(positionBookView.getTab());
				positionBookView.selectUserByUserSuid(aUserSuid);
			} else {
				positionBookMenuItem.setSelected(true);
				showPositionBookView();
				positionBookView.selectUserByUserSuid(aUserSuid);
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Открывает представление книги должностных лиц с выделением пользователя по
	 * идентификатору
	 *
	 * @param aSubElSuid идентификатор пользователя
	 */
	private void showPositionBookWithSelectionSubEl(Long aSubElSuid) {
		try {
			if (positionBookView.isViewOpen()) {
				commonCentralTabPane.getSelectionModel().select(positionBookView.getTab());
				positionBookView.selectUsersBySubElSuid(aSubElSuid);
			} else {
				positionBookMenuItem.setSelected(true);
				showPositionBookView();
				positionBookView.selectUsersBySubElSuid(aSubElSuid);
			}
		} catch (Exception e) {
			// TODO Логирование
		}
	}

	/**
	 * Открывает журнал событий с установкой селекшена на событии
	 *
	 * @param aEvent событие
	 */
	private void showEventJournalWithSelection(ru.devazz.entities.Event aEvent) {
		if (eventJournalView.isViewOpen()) {
			eventJournalView.setSelectedEvent(aEvent);
			commonCentralTabPane.getSelectionModel().select(eventJournalView.getTab());
		} else {
			try {
				eventJournalViewMenuItem.setSelected(true);
				showEventJournalView();
				eventJournalView.setSelectedEvent(aEvent);
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}
	}

	/**
	 * Отображает представление расширенного поиска с установленным параметром
	 * "Наименование задачи"
	 *
	 * @param aTaskName наименование задачи
	 * @throws Exception в случае ошибки
	 */
	private void showExtendedResViewByTasks(String aTaskName) throws Exception {
		if (extendedSearchView.isViewOpen()) {
			commonCentralTabPane.getSelectionModel().select(extendedSearchView.getTab());
			extendedSearchView.getModel().setTaskNameProperty(aTaskName);
			extendedSearchView.showExtendedSearchResults();
		} else {
			showExtendedSearchView();
			extendedSearchView.getModel().setTaskNameProperty(aTaskName);
			extendedSearchView.showExtendedSearchResults();
		}
	}

	/**
	 * Отобразить справку
	 *
	 * @throws IOException
	 */
	@FXML
	private void showHelp() throws IOException {
		Stage helpStage = new Stage();
		helpStage.setTitle("Справка Менеджера задач РЦУ");
		helpStage.getIcons().add(new Image("/css/star.png"));
		helpStage.setResizable(false);
		HelpView view = Utils.getInstance().loadView(HelpView.class);
		Scene scene = new Scene(view.getRootPane());
		helpStage.setScene(scene);
		helpStage.show();
	}

	/**
	 * Отображает представление отчета по задачам с установленной должностью
	 *
	 * @param aElement должность
	 */
	private void showReportViewWithSubEls(SubordinationElement aElement) {
		if (null != aElement) {
			if (reportView.isViewOpen()) {
				commonCentralTabPane.getSelectionModel().select(reportView.getTab());

				reportView.getModel().setPositionSuid(aElement.getSuid());
				reportView.getModel().setBattleNameTextPropertyValue(
						subTreeView.getModel().getSelectedElement().getName());
			} else {
				reportMenuItem.setSelected(true);
				try {
					showReportView();
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
				reportView.getModel().setPositionSuid(aElement.getSuid());
				reportView.getModel().setBattleNameTextPropertyValue(aElement.getName());
			}
		}
	}

	/**
	 * Устанавливает положения разделителей по умолчанию. Открывает представления,
	 * которые должны быть открыты по умолчанию
	 */
	@FXML
	private void setDefautlLocation() {
		try {
			Boolean access = Utils.getInstance().checkUserAccess(UserRoles.ASSISTENT);
			if (!subTreeMenuItem.isSelected()) {
				subTreeMenuItem.setSelected(true);
				showSubTree();
			}

			if (!eventViewMenuItem.isSelected()) {
				eventViewMenuItem.setSelected(true);
				showEventIndicator();
			}

			if (!subInfoMenuItem.isSelected()) {
				subInfoMenuItem.setSelected(true);
				showSubInfo();
			}

			if (!workbenchMenuItem.isSelected()) {
				workbenchMenuItem.setSelected(true);
				showWorkbenchView();
			}

			if (!legendOfIconsViewMenuItem.isSelected()) {
				legendOfIconsViewMenuItem.setSelected(true);
				showIconsLegend();
			}

			if (access) {
				leftHorizontalSplitPane.getDividers().get(0).setPosition(0.7);
				rightSplitPane.getDividers().get(0).setPosition(0.7);
			}
			commonSplitPane.getDividers().get(0).setPosition(0.17);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO Логирование
		}
	}

	/**
	 * Управляет доступностью разделителя сплит панели
	 *
	 * @param aNumber номер сплит-панели
	 * @param aDisable доступность
	 */
	private void disableSplitDivider(SplitViewEnum aNumber, Boolean aDisable) {
		Set<Node> nodes = commonSplitPane.lookupAll(".split-pane-divider");
		int i = 0;
		for (Node node : nodes) {
			if (aNumber.getNumSpleetPanel() == i) {
				node.setMouseTransparent(aDisable);
			}
			i++;
		}
	}

	@Override
	@FXML
	protected void setMaximized() {
		super.setMaximized();
		for (Map.Entry<SplitViewEnum, Double> entry : mapSplitPanePosition.entrySet()) {
			switch (entry.getKey()) {
			case COMMON_SPLIT_PANEL:
				commonSplitPane.getDividers().get(0).setPosition(entry.getValue());
				break;
			case RIGHT_SPLIT_PANEL:
				rightSplitPane.getDividers().get(0).setPosition(entry.getValue());
				break;
			case LEFT_HORIZONTAL_SPLIT_PANEL:
				leftHorizontalSplitPane.getDividers().get(0).setPosition(entry.getValue());
				break;
			default:
				break;
			}
		}

	}

	/**
	 * Изменение размера окна по вертикали (снизу)
	 */
	@FXML
	public void verticalResizeB() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();

		sResizeLabel.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double newHeight = windowHeigt + (location1.getY() - oldMousePositionY);
			if (!stage.isMaximized()) {
				stage.setOpacity(0.85);
				stage.setHeight(newHeight > 705 ? newHeight : 705);
			}
		});

		sResizeLabel.setOnMouseReleased(event -> stage.setOpacity(1));

	}

	/**
	 * Изменение размера окна по вертикали (сверху)
	 */
	@FXML
	public void verticalResizeT() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();
		stagePos = stage.getX();
		stagePosY = stage.getY();

		nResizeLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			topResize = true;
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double deltaY = location1.getY() - oldMousePositionY;
			double newHeight = windowHeigt - deltaY;
			if (!stage.isMaximized()) {
				stage.setOpacity(0.7);
				if ((deltaY > 0) && (stage.getHeight() > 715)) {
					stage.setY((newHeight < 715) ? stagePosY : stagePosY + deltaY);
					stage.setHeight((newHeight < 715) ? 715 : newHeight);
				} else if (deltaY < 0) {
					stage.setY((newHeight < 715) ? stagePosY : stagePosY + deltaY);
					stage.setHeight((newHeight < 715) ? 715 : newHeight);
				}
			}
		});

		nResizeLabel.setOnMouseReleased(event -> {
			stage.setOpacity(1);
			topResize = false;
		});

	}

	/**
	 * Изменение размера окна по горизонтали(справа)
	 */
	@FXML
	public void horisontalResizeR() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();

		eResizeLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double newWidth = windowWidth + (location1.getX() - oldMousePositionX);
			if (!stage.isMaximized()) {
				stage.setOpacity(0.85);
				stage.setWidth(newWidth > 1300 ? newWidth : 1300);
			}
		});

		eResizeLabel.setOnMouseReleased(event -> stage.setOpacity(1));

	}

	/**
	 * Изменение размера окна по горизонтали(слева)
	 */
	@FXML
	public void horisontalResizeL() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();
		stagePos = stage.getX();

		wResizeLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double deltaX = location1.getX() - oldMousePositionX;
			if (!stage.isMaximized()) {
				stage.setOpacity(0.7);
				if (deltaX < 0) {
					stage.setX(stagePos + deltaX);
					stage.setWidth(windowWidth - deltaX);
				} else if (stage.getWidth() > 1320) {
					double width = windowWidth - deltaX;
					stage.setX((width < 1320) ? stagePos : stagePos + deltaX);
					stage.setWidth((width < 1320) ? 1320 : width);
				}
				if (!subTreeView.getModel().getOpenViewFlag().get()
						&& !eventIndicatorView.getModel().getOpenViewFlag().get()) {
					commonSplitPane.setDividerPositions(0);
				}
			}
		});
		wResizeLabel.setOnMouseReleased(event -> stage.setOpacity(1));

	}

	/**
	 * Изменение размера окна по вертикали и горизонтали(справа)
	 */
	@FXML
	public void verticalHorisontalResizeR() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();

		seResizeLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double newWidth = windowWidth + (location1.getX() - oldMousePositionX);
			double newHeight = windowHeigt + (location1.getY() - oldMousePositionY);
			if (!stage.isMaximized()) {
				stage.setOpacity(0.85);
				stage.setHeight(newHeight > 705 ? newHeight : 705);
				stage.setWidth((newWidth > 1300) ? newWidth : 1300);
			}
		});

		seResizeLabel.setOnMouseReleased(event -> stage.setOpacity(1));

	}

	/**
	 * Изменение размера окна по вертикали и горизонтали(слева)
	 */
	@FXML
	public void verticalHorisontalResizeL() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		oldMousePositionX = location.getX();
		oldMousePositionY = location.getY();
		windowHeigt = stage.getHeight();
		windowWidth = stage.getWidth();
		stagePos = stage.getX();

		swResizeLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			double deltaX = location1.getX() - oldMousePositionX;
			double deltaY = location1.getY() - oldMousePositionY;
			double newWidth = windowWidth - deltaX;
			double newHeight = windowHeigt + deltaY;

			if (!stage.isMaximized()) {
				stage.setOpacity(0.7);
				if (deltaX < 0) {
					stage.setX(stagePos + deltaX);
					stage.setWidth(windowWidth - deltaX);
					stage.setHeight(newHeight > 705 ? newHeight : 705);
				} else if ((stage.getWidth() > 1320) && (stage.getHeight() > 715)) {
					stage.setX(((newWidth > 1300) && (newHeight > 705)) ? stagePos + deltaX
							: stagePos);
					stage.setWidth((newWidth > 1300) ? newWidth : 1300);
					stage.setHeight(newHeight > 705 ? newHeight : 705);
				}
				if (!subTreeView.getModel().getOpenViewFlag().get()
						&& !eventIndicatorView.getModel().getOpenViewFlag().get()) {
					commonSplitPane.setDividerPositions(0);
				}
			}
		});

		swResizeLabel.setOnMouseReleased(event -> stage.setOpacity(1));

	}

	/**
	 * Отобразить форму создания задачи
	 */
	@FXML
	private void showCreateTaskView() {
		Task task = new Task(0L, "", "", "Описание", null, TaskPriority.CRITICAL, new Double(0),
				null, null);
		try {
			showCurrentTaskView(task, true, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Загрузка представлений
	 *
	 * @throws IOException в случае ошибки загрузки
	 */
	private void initViews() throws Exception {
		subTreeView = Utils.getInstance().loadView(SubordinationTreeView.class);
		eventIndicatorView = Utils.getInstance().loadView(EventIndicatorView.class);
		subInfoView = Utils.getInstance().loadView(SubInfoView.class);
		positionBookView = Utils.getInstance().loadView(PositionBookView.class);
		eventJournalView = Utils.getInstance().loadView(EventJournalView.class);
		extendedSearchView = Utils.getInstance().loadView(ExtendedSearchView.class);
		workbenchView = Utils.getInstance().loadView(WorkbenchView.class);
		reportView = Utils.getInstance().loadView(ReportView.class);
	}

	/**
	 * Возвращает {@link#rootComposite}
	 *
	 * @return the {@link#rootComposite}
	 */
	public AnchorPane getRootComposite() {
		return rootComposite;
	}

	/**
	 * Слушатель изменения курсора мыши при наведении на зоны (label) изменения
	 * размеров окна
	 */
	private class CursorTypeChangeListner implements EventHandler<MouseEvent> {

		/** Объект у которого меняем стиль */
		private Label label;

		/** Стиль курсора */
		private String cursorType;

		/**
		 * Коснтруктор слушателя
		 *
		 * @param label - объект
		 * @param cursorType - стиль курсора
		 */
		public CursorTypeChangeListner(Label label, String cursorType) {
			super();
			this.label = label;
			this.cursorType = cursorType;
		}

		/**
		 * Обработчик события В полноэкранном режиме, именение размера отключается как и
		 * стиль курсора
		 */
		@Override
		public void handle(MouseEvent event) {
			if (!stage.isMaximized()) {
				label.setStyle("-fx-cursor: " + cursorType);
			} else {
				label.setStyle("-fx-cursor: default");
			}
		}

	}

	/**
	 * Слушатель перехода на вновь созданную задачу
	 */
	public class GoOverTaskListener {

		/** Идентификатор задачи */
		private Task task;

		/**
		 * Устанавливает значение полю {@link#task}
		 *
		 * @param task значение поля
		 */
		public void setTask(Task task) {
			this.task = task;
		}

		/**
		 * Переход к задаче
		 */
		public void goOver() {
			if (null != task) {
				try {
					showCurrentTaskView(task, false, false);
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * Обработчик откртия задачи из сводки задач
	 */
	public class OpenTaskInSummaryHandler implements EventHandler<MouseEvent> {

		/** Идентификатор задачи */
		private Long taskSuid;

		/**
		 * Возвращает {@link#taskSuid}
		 *
		 * @return the {@link#taskSuid}
		 */
		public Long getTaskSuid() {
			return taskSuid;
		}

		/**
		 * Устанавливает значение полю {@link#taskSuid}
		 *
		 * @param taskSuid значение поля
		 */
		public void setTaskSuid(Long taskSuid) {
			this.taskSuid = taskSuid;
		}

		/**
		 * Копирует экземпляр класса
		 *
		 * @return экземпляр класса
		 */
		public OpenTaskInSummaryHandler copy() {
			OpenTaskInSummaryHandler handler = new OpenTaskInSummaryHandler();
			if (null != taskSuid) {
				handler.setTaskSuid(new Long(taskSuid));
			}
			return handler;
		}

		/**
		 * @see javafx.event.EventHandler#handle(javafx.event.Event)
		 */
		@Override
		public void handle(MouseEvent event) {
			if ((2 == event.getClickCount()) && (null != taskSuid)) {
				Thread thread = new Thread(() -> {
					try {
						TaskModel entity = EJBProxyFactory.getInstance()
								.getService(ITaskService.class).get(taskSuid);
						Platform.runLater(() -> {
							if (null != entity) {
								try {
									showCurrentTaskView(
											EntityConverter.getInstatnce()
													.convertTaskModelToClientWrapTask(entity),
											false, false);
								} catch (Exception e) {
									// TODO Логирование
									e.printStackTrace();
								}
							}
						});

					} catch (Exception e) {
						// TODO Логирование
						e.printStackTrace();
					}
				});
				thread.setDaemon(true);
				thread.start();

			}

		}

	}

}
