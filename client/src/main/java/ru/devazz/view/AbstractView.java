package ru.devazz.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ru.devazz.interfaces.CloseListener;
import ru.devazz.interfaces.SelectableObject;
import ru.devazz.interfaces.SelectionListener;
import ru.devazz.model.PresentationModel;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Абстрактное представление
 *
 * @param <T> тип модели представления
 */
public abstract class AbstractView<T extends PresentationModel<? extends ICommonService, ? extends IEntity>> {

	/** Кнопка, закрывающая окно */
	@FXML
	protected Button closeStage;

	/** Кнопка, раскрывающая окно на весь экран */
	@FXML
	protected Button maximizedStage;

	/** Конпка, минимизирующая окно представления */
	@FXML
	protected Button minimizedStage;

	/** Окно, в котором открывается преддставление */
	protected Stage stage;

	/** Модель представления */
	protected T model;

	/** Список слушателей закрытия окна */
	private List<CloseListener> listCloseListeners = new ArrayList<>();

	/** Список слушателей селекшена */
	private List<SelectionListener> listSelectionListeners = new ArrayList<>();

	/**
	 * Конструктор
	 */
	public AbstractView() {
		model = createPresentationModel();
	}

	/**
	 * Инициализация представления
	 */
	public abstract void initialize();

	/**
	 * Создание модели представления
	 *
	 * @return модель преддставления
	 */
	protected abstract T createPresentationModel();

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Закрывает окно
	 */
	@FXML
	protected void close() {
		model.setOpenFlagValue(false);
		fireClose();

	}

	/**
	 * Сворачивает окно приложения
	 */
	@FXML
	protected void setIconofy() {
		stage.setIconified(true);
	}

	/**
	 * Разворачвает окно на весь экран
	 */
	@FXML
	protected void setMaximized() {
		if (stage.isMaximized()) {
			stage.setMaximized(false);
		} else {
			stage.setMaximized(true);
		}
	}

	public void addCloseListener(CloseListener aListener) {
		listCloseListeners.add(aListener);
	}

	/**
	 * Уведомляет слушателей о закрытии представления
	 */
	void fireClose() {
		for (CloseListener listener : listCloseListeners) {
			listener.closeWindow();
		}
	}

	void addSelectionListener(SelectionListener aListener) {
		listSelectionListeners.add(aListener);
	}

	/**
	 * Уведомляет слушателей о закрытии представления
	 */
	void fireSelect(SelectableObject aObject) {
		for (SelectionListener listener : listSelectionListeners) {
			listener.fireSelect(aObject);
		}
	}

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	/**
	 * Возвращает основную вкладку представления
	 *
	 * @return основная вкладка
	 */
	public Tab getTab() {
		return null;
	}

	/**
	 * Возвращает панель вкладок
	 *
	 * @return панель вкладок
	 */
	public TabPane getTabPane() {
		return null;
	}

	/**
	 * Закрывает вкладку
	 */
	private void closeTab() {
		TabPane tabPane = getTabPane();
		if (null != tabPane) {
			tabPane.getTabs().remove(getTab());
		}
	}

	/**
	 * Закрывает вкладочное представление
	 */
	@FXML
	protected void closeTabView() {
		closeTab();
		model.setOpenFlagValue(false);
		fireClose();
	}

	/**
	 * Возвращает признак того, что окно открыто
	 *
	 * @return признак
	 */
	boolean isViewOpen() {
		return model.getOpenViewFlag().get();
	}

	/**
	 * Обновление представления
	 */
	public void refresh() {
		model.loadEntities();
	}

}
