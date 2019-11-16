package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.siencesquad.hqtasks.ui.model.TaskHistoryEntryPanelModel;

/**
 * Представление панели исторической записи
 */
public class TaskHistoryEntryPanelView extends AbstractView<TaskHistoryEntryPanelModel> {

	/** Корневая панель */
	@FXML
	private TitledPane rootPane;

	/** Лейбл описание задачи */
	@FXML
	private Label textLabel;

	/** Лейбл заголовок задачи */
	@FXML
	private Label titleLabel;

	/** Лейбл даты */
	@FXML
	private Label dateLabel;

	/** пиктограмма типа исторической записи */
	@FXML
	private ImageView historyTypeImage;

	/** Контейнер заголовка исторической записи */
	@FXML
	private AnchorPane titleAchorPane;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(textLabel.textProperty(), model.getTextLabelProperty());
		Bindings.bindBidirectional(titleLabel.textProperty(), model.getTitleLabelProperty());
		Bindings.bindBidirectional(dateLabel.textProperty(), model.getDateLabelProperty());
		Bindings.bindBidirectional(historyTypeImage.imageProperty(), model.getImageProperty());

	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected TaskHistoryEntryPanelModel createPresentaionModel() {
		return new TaskHistoryEntryPanelModel();
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public TitledPane getRootPane() {
		return rootPane;
	}

}
