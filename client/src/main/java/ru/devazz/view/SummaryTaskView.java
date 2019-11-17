package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ru.devazz.model.SummaryTaskViewModel;

/**
 * Представление панели задачи сводки по задачам
 */
public class SummaryTaskView extends AbstractView<SummaryTaskViewModel> {

	/** Корневая панель панели задачи сводки */
	@FXML
	private HBox rootPanel;

	/** Представление изображения приортета задачи */
	@FXML
	private ImageView priorityImage;

	/** Лейбл наименования задачи */
	@FXML
	private Label nameTaskLabel;

	/** Представление изображения статуса задачи */
	@FXML
	private ImageView statusImage;

	@Override
	public void initialize() {
		Bindings.bindBidirectional(priorityImage.imageProperty(), model.getPriorityImageProperty());
		Bindings.bindBidirectional(nameTaskLabel.textProperty(), model.getNameTaskProperty());
		Bindings.bindBidirectional(statusImage.imageProperty(), model.getStatusImageProperty());
	}

	/**
	 * Добавляет
	 *
	 * @param aOpenTaskhandler
	 */
	public void addOpenTaskListener(RootView.OpenTaskInSummaryHandler aOpenTaskhandler) {
		if (null != rootPanel) {
			RootView.OpenTaskInSummaryHandler handler = aOpenTaskhandler.copy();
			handler.setTaskSuid(model.getTaskSuid());
			rootPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
		}
	}

	/**
	 * Возвращает {@link#rootPanel}
	 *
	 * @return the {@link#rootPanel}
	 */
	public HBox getRootPanel() {
		return rootPanel;
	}

	@Override
	protected SummaryTaskViewModel createPresentaionModel() {
		return new SummaryTaskViewModel();
	}

}
