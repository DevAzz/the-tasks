package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.SubordinationElementEntity;
import ru.siencesquad.hqtasks.ui.entities.ExtSearchRes;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.model.ExtendedSearchViewModel;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

import javax.naming.NamingException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Представление вкладки расширенного поиска
 */
public class ExtendedSearchView extends AbstractView<ExtendedSearchViewModel> {

	/** Панель вкладок представления */
	@FXML
	private TabPane extendedSearchTabPane;

	/** Панель поисковых вкладок */
	@FXML
	private TabPane searchTabPane;

	/** Вкладка поиска по задачам */
	@FXML
	private Tab taskSearchTab;

	/** Вкладка поиска по ДЛ */
	@FXML
	private Tab personsSearchTab;

	/** Подвкладка результатов расширенного поиска для поиска задач */
	@FXML
	private TitledPane tasksResultsTitledPane;

	/** Подвкладка параметров расширенного поиска для поиска задач */
	@FXML
	private TitledPane tasksParametersTitledPane;

	/** Подвкладка параметров расширенногопоиска для поиска ДЛ */
	@FXML
	private TitledPane personsParametersTitledPane;

	/** Подвкладка результатов расширенного поиска для поиска ДЛ */
	@FXML
	private TitledPane personsResultsTitledPane;

	/** Контейнер для представления результатов расширенного поиска задач */
	@FXML
	private VBox tasksResultsVBox;

	/** Контейнер для представления результатов расширенного поиска ДЛ */
	@FXML
	private VBox personsResultsVBox;

	/** Контейнер для представления результатов расширенного поиска */
	@FXML
	private VBox commonVBox;

	/** Текстовое поле Должность */
	@FXML
	private TextField authorTextField;

	/** Текстовое поле Наименование задачи */
	@FXML
	private TextField nameTaskTextField;

	/** Текстовое поле Наименование боевого поста */
	@FXML
	private TextField executorTextField;

	// Поля ввода поисковых признаков для вкладки "Должностные лица"

	/** Текстовое поле ФИО ДЛ */
	@FXML
	private TextField personNameTextField;

	/** Текстовое поле Должность ДЛ */
	@FXML
	private TextField positionSubElTextField;

	/** Текстовое поле наименование боевого поста */
	@FXML
	private TextField subElnameTextField;

	/** Обработчик двойного клика по результату поиска */
	private EventHandler<MouseEvent> doubleClickHandler;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		bindView();
		authorTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});
		nameTaskTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});
		executorTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});

		personNameTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});
		positionSubElTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});
		subElnameTextField.setOnKeyPressed(event -> {
			startSearchByEnter(event);
		});
	}

	/**
	 * Запуск поиска записей
	 *
	 * @param event событие
	 */
	private void startSearchByEnter(KeyEvent event) {
		if (KeyCode.ENTER.equals(event.getCode())) {
			Tab tab = searchTabPane.getSelectionModel().getSelectedItem();
			if (tab.equals(taskSearchTab)) {
				showExtendedSearchResults();
			} else {
				showSubElsResults();
			}
		}
	}

	/**
	 * Связывает представление и модель
	 */
	private void bindView() {

		// Поля ввода для вкладки поиска задач
		Bindings.bindBidirectional(authorTextField.textProperty(), model.getAuthorProperty());
		Bindings.bindBidirectional(nameTaskTextField.textProperty(), model.getTaskNameProperty());
		Bindings.bindBidirectional(executorTextField.textProperty(), model.getExecutorProperty());

		// Поля ввода для вкладки поиска ДЛ
		Bindings.bindBidirectional(personNameTextField.textProperty(),
				model.getPersonNameProperty());
		Bindings.bindBidirectional(positionSubElTextField.textProperty(),
				model.getPositionSubElProperty());
		Bindings.bindBidirectional(subElnameTextField.textProperty(), model.getSubElNameProperty());
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected ExtendedSearchViewModel createPresentaionModel() {
		return new ExtendedSearchViewModel();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#getTabPane()
	 */
	@Override
	public TabPane getTabPane() {
		return extendedSearchTabPane;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#getTab()
	 */
	@Override
	public Tab getTab() {
		Tab tab = (!extendedSearchTabPane.getTabs().isEmpty())
				? extendedSearchTabPane.getTabs().get(0)
				: null;
		return tab;
	}

	/**
	 * Выбор автора
	 */
	@FXML
	private void selectAuthor() {
		SubordinatioElementServiceRemote subElsService;
		try {
			subElsService = EJBProxyFactory.getInstance()
					.getService(SubordinatioElementServiceRemote.class);

			ObservableList<SubordinationElement> listSubEls = FXCollections.observableArrayList();

			Consumer<? super List<SubordinationElement>> consumer = t -> {
				SubordinationElement element = t.get(0);
				if (null != element) {
					model.setAuthor(element);
					model.setExecutor(null);
				}
				authorTextField.requestFocus();
			};

			for (SubordinationElementEntity entity : subElsService
					.getAll(Utils.getInstance().getCurrentUser().getIduser())) {
				listSubEls
						.add(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity));
			}

			DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, false, true);
		} catch (NamingException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Выбор исполнителя
	 */
	@FXML
	private void selectExecutor() {
		SubordinatioElementServiceRemote subElsService;
		try {
			subElsService = EJBProxyFactory.getInstance()
					.getService(SubordinatioElementServiceRemote.class);

			ObservableList<SubordinationElement> listSubEls = FXCollections.observableArrayList();

			Consumer<? super List<SubordinationElement>> consumer = t -> {
				SubordinationElement element = t.get(0);
				if (null != element) {
					model.setAuthor(null);
					model.setExecutor(element);
				}
				executorTextField.requestFocus();
			};

			for (SubordinationElementEntity entity : subElsService
					.getAll(Utils.getInstance().getCurrentUser().getIduser())) {
				listSubEls
						.add(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity));
			}

			DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, false, true);
		} catch (NamingException e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Метод вызова поиска задач
	 */
	@FXML
	public void showExtendedSearchResults() {
		tasksResultsTitledPane.setExpanded(true);
		model.searchTasks();
		loadTasksResultView();

	}

	/**
	 * Метод вызова поиска должностных лиц
	 */
	@FXML
	public void showSubElsResults() {
		personsResultsTitledPane.setExpanded(true);
		model.searchPersons();
		loadPersonsResultView();
	}

	/**
	 * Создание панели результатов
	 *
	 * @param result результат
	 */
	public void createResultPanel(VBox aBox, ExtSearchRes result) {
		try {
			// загружаем представление результата
			ExtSearchResView extSearchResView = Utils.getInstance()
					.loadView(ExtSearchResView.class);
			if (null != extSearchResView) {
				extSearchResView.getModel().setResult(result);
				extSearchResView.getRootPane().setId(String.valueOf(result.getEntity().getSuid()));
				extSearchResView.addResultSelectionHandler(event -> {
					int countClick = event.getClickCount();
					AnchorPane pane = (AnchorPane) event.getSource();
					selectTask(pane, aBox);
					if (2 == countClick) {
						doubleClickHandler.handle(event);
					}
				});
			}
			aBox.getChildren().add(extSearchResView.getRootPane());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Осуществляет выбор задачи по панели задачи
	 *
	 * @param aRootPane корневая панель задачи
	 */
	private void selectTask(AnchorPane aRootPane, VBox aBox) {
		for (Node value : aBox.getChildren()) {
			value.getStyleClass().remove("selected-search-result-panel");
		}

		if (null != aRootPane) {
			if (tasksResultsVBox.equals(aBox)) {
				model.setSelectedResult(model.getResTasksBySuid(aRootPane.getId()));
			} else {
				model.setSelectedResult(model.getResPersonsBySuid(aRootPane.getId()));
			}
			aRootPane.getStyleClass().add("selected-search-result-panel");
		}
	}

	/**
	 * Загрузка и отрисовка результатов поиска
	 */
	public void loadTasksResultView() {
		tasksResultsVBox.getChildren().clear();
		for (ExtSearchRes result : model.getTasksData()) {
			createResultPanel(tasksResultsVBox, result);
		}
	}

	/**
	 * Загрузка и отрисовка результатов поиска
	 */
	public void loadPersonsResultView() {
		personsResultsVBox.getChildren().clear();
		for (ExtSearchRes result : model.getPersonsData()) {
			createResultPanel(personsResultsVBox, result);
		}
	}

	/**
	 * Возвращает {@link#doubleClickHandler}
	 *
	 * @return the {@link#doubleClickHandler}
	 */
	public EventHandler<MouseEvent> getDoubleClickHandler() {
		return doubleClickHandler;
	}

	/**
	 * Устанавливает значение полю {@link#doubleClickHandler}
	 *
	 * @param doubleClickHandler значение поля
	 */
	public void setDoubleClickHandler(EventHandler<MouseEvent> doubleClickHandler) {
		this.doubleClickHandler = doubleClickHandler;
	}

}
