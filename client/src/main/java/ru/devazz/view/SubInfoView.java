package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.devazz.model.SubInfoViewModel;

/**
 * Представление информации о подразделении
 */
public class SubInfoView extends AbstractView<SubInfoViewModel> {

	/** Главная вкладка представления */
	@FXML
	private TabPane subInfoTabPane;

	/** Корневая панель */
	@FXML
	private AnchorPane pane;

	/** Изображение пользователя */
	@FXML
	private ImageView subElementImage;

	/** Заголовок представления */
	@FXML
	private Label labelTitle;

	/** Метка для имени пользователя */
	@FXML
	private Label fioLabel;

	/** Должность */
	@FXML
	private Label positionLabel;

	/** Лейбл отображения состояния пользователя в системе */
	@FXML
	private Label onlineLabel;

	@Override
	public void initialize() {
		Bindings.bindBidirectional(labelTitle.textProperty(), model.getTitleProperty());
		Bindings.bindBidirectional(fioLabel.textProperty(), model.getFioProperty());
		Bindings.bindBidirectional(positionLabel.textProperty(), model.getPositionProperty());
		Bindings.bindBidirectional(subElementImage.imageProperty(), model.getImageProperty());
		Bindings.bindBidirectional(onlineLabel.textProperty(), model.getOnlineProperty());

		subElementImage.setCache(true);
		subElementImage.setCacheHint(CacheHint.SPEED);
	}

	@Override
	protected SubInfoViewModel createPresentationModel() {
		return new SubInfoViewModel();
	}

	@Override
	public TabPane getTabPane() {
		return subInfoTabPane;
	}

	/**
	 * Возвращает вкладку панели
	 *
	 * @return вкладка панели
	 */
	@Override
	public Tab getTab() {
		Tab tab = (!subInfoTabPane.getTabs().isEmpty()) ? subInfoTabPane.getTabs().get(0) : null;
		return tab;
	}

}
