package ru.devazz.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ru.devazz.interfaces.CloseListener;
import ru.devazz.interfaces.SelectableObject;
import ru.devazz.interfaces.SelectionListener;
import ru.devazz.interfaces.UpdateListener;
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

	/** Выделенный объект */
	protected SelectableObject selection;

	/** Список слушателей закрытия окна */
	private List<CloseListener> listCloseListeneres = new ArrayList<>();

	/** Список слушателей селекшена */
	private List<SelectionListener> listSelectionListeneres = new ArrayList<>();

	/** Список слушателей обновления */
	private List<UpdateListener> listUpdateListeners = new ArrayList<>();

	/**
	 * Конструктор
	 */
	public AbstractView() {
		model = createPresentaionModel();
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
	protected abstract T createPresentaionModel();

	/**
	 * Возвращает {@link#stage}
	 *
	 * @return the {@link#stage}
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Устанавливает значение полю {@link#stage}
	 *
	 * @param {@link#stage}
	 */
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

	/**
	 * Добавляет слушателя закрытия представления
	 *
	 * @param aListener
	 */
	public void addCloseListener(CloseListener aListener) {
		listCloseListeneres.add(aListener);
	}

	/**
	 * Уведомляет слушателей о закрытии представления
	 */
	public void fireClose() {
		for (CloseListener listener : listCloseListeneres) {
			listener.closeWindow();
		}
	}

	/**
	 * Добавляет слушателя закрытия представления
	 *
	 * @param aListener
	 */
	public void addSelectionListener(SelectionListener aListener) {
		listSelectionListeneres.add(aListener);
	}

	/**
	 * Уведомляет слушателей о закрытии представления
	 */
	public void fireSelect(SelectableObject aObject) {
		for (SelectionListener listener : listSelectionListeneres) {
			listener.fireSelect(aObject);
		}
	}

	/**
	 * Добавляет слушателя обновления представления
	 *
	 * @param aListener слушатель обновления представления
	 */
	public void addUpdateListener(UpdateListener aListener) {
		listUpdateListeners.add(aListener);
	}

	/**
	 * Уведомляет слушателей об обновлении
	 */
	public void fireUpdate() {
		for (UpdateListener listener : listUpdateListeners) {
			listener.fireUpdateView();
		}
	}

	/**
	 * Возвращает {@link#model}
	 *
	 * @return the {@link#model}
	 */
	public T getModel() {
		return model;
	}

	/**
	 * Устанавливает значение полю {@link#model}
	 *
	 * @param {@link#model}
	 */
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
	public void closeTab() {
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
	public boolean isViewOpen() {
		return model.getOpenViewFlag().get();
	}

	/**
	 * Обновление представления
	 */
	public void refresh() {
		model.loadEntities();
	}

}
