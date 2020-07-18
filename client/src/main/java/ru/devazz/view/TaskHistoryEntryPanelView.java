package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.devazz.model.TaskHistoryEntryPanelModel;

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

	@Override
	public void initialize() {
		Bindings.bindBidirectional(textLabel.textProperty(), model.getTextLabelProperty());
		Bindings.bindBidirectional(titleLabel.textProperty(), model.getTitleLabelProperty());
		Bindings.bindBidirectional(dateLabel.textProperty(), model.getDateLabelProperty());
		Bindings.bindBidirectional(historyTypeImage.imageProperty(), model.getImageProperty());

	}

	@Override
	protected TaskHistoryEntryPanelModel createPresentationModel() {
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
