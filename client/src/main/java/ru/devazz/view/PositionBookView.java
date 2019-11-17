package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import ru.devazz.entities.User;
import ru.devazz.model.PositionBookViewModel;
import ru.devazz.server.EJBProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.SubordinationElementModel;

import java.util.List;

/**
 * Представление книги должностных лиц
 */
public class PositionBookView extends AbstractView<PositionBookViewModel> {

	/** Композит вкладок книги должностных лиц */
	@FXML
	private TabPane positionBookTabPane;

	/** Таблица должностных лиц */
	@FXML
	private TableView<User> personsTable;

	/** Столбец ФИО пользователя */
	@FXML
	private TableColumn<User, String> fioColumn;

	/** Столбец должность пользователя */
	@FXML
	private TableColumn<User, String> positionColumn;

	/** Столбец должность пользователя */
	@FXML
	private TableColumn<User, String> subElColumn;

	/** Столбец портрет пользователя */
	@FXML
	private TableColumn<User, TitledPane> imageColumn;

	@Override
	@FXML
	public void initialize() {

		Bindings.bindBidirectional(personsTable.itemsProperty(), model.getDataProperty());

		// устанавливаем тип и значение которое должно хранится в колонке
		fioColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
		positionColumn.setCellValueFactory(new PropertyValueFactory<User, String>("position"));

		subElColumn.setCellValueFactory(user -> {
			SimpleStringProperty property = new SimpleStringProperty();
			Long subElSuid = user.getValue().getSubElementSuid();
			if (null != subElSuid) {
				ISubordinationElementService subElService = EJBProxyFactory.getInstance()
						.getService(ISubordinationElementService.class);
				SubordinationElementModel subEl = subElService.get(subElSuid);
				if (null != subEl) {
					property.set(subEl.getName());
				}
			}
			return property;
		});
		imageColumn.setCellValueFactory(user -> {
			ObjectProperty<TitledPane> property = new SimpleObjectProperty<>();
			TitledPane imagePane = new TitledPane();
			ImageView imageView = new ImageView();
			imageView.setFitWidth(289);
			imageView.setFitHeight(293);
			imageView.getStyleClass().add("subInfoImageStyle");
			imagePane.setOnMouseClicked(event -> {
				if (imagePane.isExpanded()) {
					imageView.setImage(new Image(model.getImageURI(user.getValue().getSuid()), 289,
							293, true, false, true));
				} else {
					imageView.setImage(null);
				}
			});
			imagePane.setText(user.getValue().getName());

			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(imageView);
			ScrollPane pane = new ScrollPane(borderPane);
			pane.setFitToHeight(true);
			pane.setFitToWidth(true);
			imagePane.setContent(pane);
			imagePane.setExpanded(false);
			property.set(imagePane);
			// }
			return property;
		});

		personsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@Override
	protected PositionBookViewModel createPresentaionModel() {
		return new PositionBookViewModel();
	}

	@Override
	public TabPane getTabPane() {
		return positionBookTabPane;
	}

	@Override
	public Tab getTab() {
		Tab tab = (!positionBookTabPane.getTabs().isEmpty()) ? positionBookTabPane.getTabs().get(0)
				: null;
		return tab;
	}

	/**
	 * Выделяет пользователя в таблице
	 *
	 * @param aUserSuid идентификатор пользователя
	 */
	public void selectUserByUserSuid(Long aUserSuid) {
		personsTable.getSelectionModel().clearSelection();
		User user = model.getUserBySuid(aUserSuid);

		personsTable.requestFocus();
		personsTable.getSelectionModel().select(user);
		personsTable.getFocusModel().focus(personsTable.getSelectionModel().getSelectedIndex());
		personsTable.scrollTo(user);
	}

	/**
	 * Выделяет должностных лиц по идентификатору элемента подчиненности
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 */
	public void selectUsersBySubElSuid(Long aSubElSuid) {
		personsTable.getSelectionModel().clearSelection();
		List<User> users = model.getUsersBySubElSuid(aSubElSuid);
		for (User user : users) {
			personsTable.getSelectionModel().select(user);
			personsTable.getFocusModel().focus(personsTable.getSelectionModel().getSelectedIndex());
		}
	}

}
