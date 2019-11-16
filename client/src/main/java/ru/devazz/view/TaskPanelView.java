package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import ru.siencesquad.hqtasks.ui.model.TaskPanelViewModel;

/**
 * Представление панели задачи
 */
public class TaskPanelView extends AbstractView<TaskPanelViewModel> {

	/** Лейбл описание задачи */
	@FXML
	private Label noteLabel;

	/** Лейбл заголовок задачи */
	@FXML
	private Label titleLabel;

	/** Лейбл даты */
	@FXML
	private Label dateLabel;

	/** Лейбл автора задачи */
	@FXML
	private Label taskAuthorLabel;

	/** Панель задачи */
	@FXML
	private TitledPane titledPane;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(noteLabel.textProperty(), model.getNoteLabelProperty());
		Bindings.bindBidirectional(titleLabel.textProperty(), model.getTitleLabelProperty());
		Bindings.bindBidirectional(dateLabel.textProperty(), model.getDateLabelProperty());
		Bindings.bindBidirectional(taskAuthorLabel.textProperty(), model.getAuthorLabelProperty());
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected TaskPanelViewModel createPresentaionModel() {
		return new TaskPanelViewModel();
	}

	/**
	 * Возвращает {@link#titledPane}
	 *
	 * @return the {@link#titledPane}
	 */
	public TitledPane getTitledPane() {
		return titledPane;
	}

}
