package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import ru.siencesquad.hqtasks.ui.model.ExtSearchResModel;

/**
 * Представление результатов поиска
 */
public class ExtSearchResView extends AbstractView<ExtSearchResModel> {

	/** Лейбл для отображение результатов поиска */
	@FXML
	private Label resultsLabel;

	/** Корневой композит для представления результатов поиска */
	@FXML
	private AnchorPane rootPane;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(resultsLabel.textProperty(), model.getResLabelProperty());
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected ExtSearchResModel createPresentaionModel() {
		return new ExtSearchResModel();
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the rootPane
	 */
	public AnchorPane getRootPane() {
		return rootPane;
	}

	/**
	 * Устанавливает значение полю rootPane
	 *
	 * @param rootPane значение поле
	 */
	public void setRootPane(AnchorPane rootPane) {
		this.rootPane = rootPane;
	}

	/**
	 * Устанавливает стиль выделения панели результата поиска
	 *
	 * @param aStyle стиль селекшена
	 */
	public void setSelectedStyle(String aStyle) {
		rootPane.getStyleClass().add(aStyle);
	}

	/**
	 * Удаляет стиль селекшена панели результата поиска
	 *
	 * @param aStyle стиль селекшена
	 */
	public void removeSelectedStyle(String aStyle) {
		rootPane.getStyleClass().remove(aStyle);
	}

	/**
	 * Добавляет обработчик выделения панели
	 *
	 * @param обработчик выделения
	 */
	public void addResultSelectionHandler(EventHandler<MouseEvent> aHandler) {
		rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, aHandler);
	}

}
