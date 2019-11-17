package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import ru.devazz.model.RegistryModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.LoadAnimation;
import ru.devazz.utils.SubElType;
import ru.devazz.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Регистрация пользователей
 */
public class RegistryView extends AbstractView<RegistryModel> {

	/** Корневой композит */
	@FXML
	private AnchorPane rootPane;

	/** Панель вкладок */
	@FXML
	private TabPane adminTabPane;

	/** Вкладка добавления нового пользователя */
	@FXML
	private Tab registryTab;

	/** Вкладка управления пользователями */
	@FXML
	private Tab userManagerTab;

	/** Кнопка отмены и очистики полей */
	@FXML
	private Button cancelButton;

	/** Кнопка для записи данных в базу */
	@FXML
	private Button applyButton;

	/** Кнопка для закрытия приложения */
	@FXML
	private Button exitButton;

	/** Кнопка для выбора фотографии */
	@FXML
	private Button picButton;

	/** Кнопка для сворачивания окна */
	@FXML
	private Button iconofyButton;

	/** Кнопка для закрытия приложения(крестик) */
	@FXML
	private Button closeButton;

	/** Лейбл для переноса окна */
	@FXML
	private Label moveLabel;

	/** Поле логина */
	@FXML
	private TextField loginField;

	/** Поле пароля */
	@FXML
	private TextField passwordField;

	/** Поле имени */
	@FXML
	private TextField nameField;

	/** Поле фамилии */
	@FXML
	private TextField surnameField;

	/** Поле отчества */
	@FXML
	private TextField patronymicField;

	/** Поле должности */
	@FXML
	private TextField positionField;

	/** Комбо-бокс выбора должности */
	@FXML
	private ComboBox<SubElType> subElBox;

	/** Представление фотографии */
	@FXML
	private ImageView imageView;

	/** Картинка для анимации загрузки */
	@FXML
	private ImageView loadImage;

	/** Таблица со списком пользователей */
	@FXML
	private TableView<UserModel> usersTable;

	/** Колонка логина */
	@FXML
	private TableColumn<UserModel, AnchorPane> loginColumn;

	/** Колонка пароля */
	@FXML
	private TableColumn<UserModel, AnchorPane> passwordColumn;

	/** Колонка ФИО */
	@FXML
	private TableColumn<UserModel, AnchorPane> fioColumn;

	/** Колонка должности */
	@FXML
	private TableColumn<UserModel, AnchorPane> positionColumn;

	/** Колонка должнолсти */
	@FXML
	private TableColumn<UserModel, AnchorPane> subordinationColumn;

	/** Колонка в сети */
	@FXML
	private TableColumn<UserModel, AnchorPane> onlineColumn;

	/** Кнопка перехода к вкладке добавления пользователей */
	@FXML
	private Button umAdd;

	/** Кнопка удаления пользователя */
	@FXML
	private Button umDelete;

	/** Кнопка изменения флага онлайн */
	@FXML
	private Button umChangeOnline;

	/** Кнопка изменения фотографии */
	@FXML
	private Button umChangePhoto;

	/** Кнопка сохранения */
	@FXML
	private Button umSave;

	/** Координаты мыши */
	private double mousePointX, mousePointY;

	/** Координаты окна */
	private double windowPointX, windowPointY;

	/** Буфер для чтения данных из файла */
	private byte[] buffer;

	/** Флаг для изменения цвета рамки при нажатии на верхнюю панель */
	private boolean pressedFlag = false;

	/** Флаг для процесса записи данных в БД */
	private BooleanProperty loadFlag;

	/** Список subordinationElements */
	private ObservableList<SubElType> subEls = FXCollections.observableArrayList();

	@Override
	public void initialize() {
		loadFlag = new SimpleBooleanProperty(this, "loadFlag", false);

		// Связываем текстовые поля с данными модели
		Bindings.bindBidirectional(loginField.textProperty(), model.getLogin());
		Bindings.bindBidirectional(passwordField.textProperty(), model.getPassword());
		Bindings.bindBidirectional(nameField.textProperty(), model.getName());
		Bindings.bindBidirectional(surnameField.textProperty(), model.getSurname());
		Bindings.bindBidirectional(patronymicField.textProperty(), model.getPatronymic());
		Bindings.bindBidirectional(positionField.textProperty(), model.getPosition());

		subEls = FXCollections.observableArrayList();
		subEls.addAll(SubElType.values());
		subElBox.setItems(subEls);
		subElBox.setConverter(new StringConverter<SubElType>() {

			/**
			 * @see javafx.util.StringConverter#toString(Object)
			 */
			@Override
			public String toString(SubElType object) {
				return object.getName();
			}

			/**
			 * @see javafx.util.StringConverter#fromString(String)
			 */
			@Override
			public SubElType fromString(String string) {
				return null;
			}
		});
		subElBox.setOnAction(event -> {
			model.setSelectedSubElType(subElBox.getSelectionModel().getSelectedItem());
			model.setSubElsPropertyValue(subElBox.getSelectionModel().getSelectedItem().getName());
		});

		// Устанавливаем запрещаюший стиль для текстовых полей
		loginField.getStyleClass().add("textFieldErr");
		passwordField.getStyleClass().add("textFieldErr");
		nameField.getStyleClass().add("textFieldErr");
		surnameField.getStyleClass().add("textFieldErr");
		patronymicField.getStyleClass().add("textFieldErr");
		positionField.getStyleClass().add("textFieldErr");
		subElBox.getStyleClass().add("textFieldErr");
		applyButton.setDisable(true);

		// Изменяем стили текстовых полей в зависимости от наличия данных
		model.getLogin().addListener(new ChangeStyleListener(loginField));
		model.getPassword().addListener(new ChangeStyleListener(passwordField));
		model.getName().addListener(new ChangeStyleListener(nameField));
		model.getSurname().addListener(new ChangeStyleListener(surnameField));
		model.getPatronymic().addListener(new ChangeStyleListener(patronymicField));
		model.getPosition().addListener(new ChangeStyleListener(positionField));
		model.getSubElsProperty().addListener(new ChangeStyleListener(subElBox));

		// Загружаем данные по нажатию кнопки "принять"
		applyButton.setOnMouseClicked(event -> {

			// Анимация загрузки
			LoadAnimation loadAnimation = new LoadAnimation(585, 380, 25, 5);
			rootPane.getChildren().addAll(loadAnimation.getCircles());
			loadAnimation.start();

			loadFlag.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
				if (newValue) {
					loadAnimation.stop();
					Platform.runLater(
							() -> rootPane.getChildren().removeAll(loadAnimation.getCircles()));
					loadFlag.set(false);
				}
			});
			Thread thread = new Thread(() -> {
				try {
					model.loadInDB(buffer);
					loadFlag.setValue(true);
					applyButton.setDisable(true);

					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Запись данных");
						alert.setHeaderText("Данные успешно сохранены!");
						alert.show();
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			thread.setDaemon(true);
			thread.start();

		});

		// Очищаем все текстовые поля
		cancelButton.setOnMouseClicked(event -> {
			loginField.clear();
			passwordField.clear();
			nameField.clear();
			surnameField.clear();
			patronymicField.clear();
			positionField.clear();
			subElBox.getSelectionModel().clearSelection();
			model.getSubElsProperty().setValue("");
			if (!applyButton.isDisabled()) {
				applyButton.setDisable(false);
			}

		});

		// Закрываем окно
		exitButton.setOnMouseClicked(event -> {
			stage.close();
		});

		closeButton.setOnMouseClicked(event -> {
			stage.close();
		});

		iconofyButton.setOnMouseClicked(event -> {
			stage.setIconified(true);
		});

		picButton.setOnMouseClicked(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Выбор документа");
			File file = fileChooser.showOpenDialog(stage);
			if (null != file) {
				imageView.setImage(new Image("file:/" + file.getAbsolutePath()));
			}

			try (FileInputStream fileIn = new FileInputStream(file.getAbsolutePath())) {
				buffer = new byte[fileIn.available()];
				fileIn.read(buffer, 0, fileIn.available());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		});
		// ==========
		// Управление пользователями
		// ==========
		loginColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane internalPane = new AnchorPane();
			TextField textField = new TextField();
			textField.setText(user.getValue().getLogin());
			textField.textProperty().addListener(e -> {
				user.getValue().setLogin(textField.getText());
			});
			textField.setOnMouseClicked(e -> {
				usersTable.getSelectionModel().select(user.getValue());
				model.addChangedUser(user.getValue());
			});
			internalPane.getChildren().add(textField);
			internalPane.setLeftAnchor(textField, 0.0);
			internalPane.setRightAnchor(textField, 0.0);
			property.set(internalPane);
			return property;
		});
		passwordColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane passwordFieldPane = new AnchorPane();
			PasswordField passwordField = new PasswordField();
			String sourcePassword = user.getValue().getPassword();
			passwordField.setText(user.getValue().getPassword());
			passwordField.textProperty().addListener(e -> {
				if (passwordField.getText() != sourcePassword) {
					try {
						user.getValue()
								.setPassword(Utils.getInstance().sha(passwordField.getText()));
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					user.getValue().setPassword(sourcePassword);
				}
				model.addChangedUser(user.getValue());
			});
			passwordField.setOnMouseClicked(e -> {
				usersTable.getSelectionModel().select(user.getValue());
			});
			passwordFieldPane.getChildren().add(passwordField);
			passwordFieldPane.setLeftAnchor(passwordField, 0.0);
			passwordFieldPane.setRightAnchor(passwordField, 0.0);
			property.set(passwordFieldPane);
			return property;
		});
		fioColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane internalPane = new AnchorPane();
			TextField textField = new TextField();
			textField.setText(user.getValue().getName());
			textField.textProperty().addListener(e -> {
				user.getValue().setName(textField.getText());
				model.addChangedUser(user.getValue());
			});
			textField.setOnMouseClicked(e -> {
				usersTable.getSelectionModel().select(user.getValue());
			});
			internalPane.getChildren().add(textField);
			internalPane.setLeftAnchor(textField, 0.0);
			internalPane.setRightAnchor(textField, 0.0);
			property.set(internalPane);
			return property;
		});
		positionColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane internalPane = new AnchorPane();
			TextField textField = new TextField();
			textField.setText(user.getValue().getPosition());
			textField.textProperty().addListener(e -> {
				user.getValue().setPosition(textField.getText());
				model.addChangedUser(user.getValue());
			});
			textField.setOnMouseClicked(e -> {
				usersTable.getSelectionModel().select(user.getValue());
			});
			internalPane.getChildren().add(textField);
			internalPane.setLeftAnchor(textField, 0.0);
			internalPane.setRightAnchor(textField, 0.0);
			property.set(internalPane);
			return property;
		});

		subordinationColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane internalPane = new AnchorPane();
			ComboBox<SubElType> comboBox = new ComboBox<>();
			comboBox.setItems(subEls);
			comboBox.setConverter(new StringConverter<SubElType>() {

				/**
				 * @see javafx.util.StringConverter#toString(Object)
				 */
				@Override
				public String toString(SubElType object) {
					return object.getName();
				}

				/**
				 * @see javafx.util.StringConverter#fromString(String)
				 */
				@Override
				public SubElType fromString(String string) {
					return null;
				}
			});
			comboBox.getSelectionModel()
					.select(SubElType.getSubElbySUID(user.getValue().getPositionSuid()));
			comboBox.valueProperty().addListener(e -> {
				usersTable.getSelectionModel().select(user.getValue());
				user.getValue().setPositionSuid(
						comboBox.getSelectionModel().getSelectedItem().getSubElSuid());
				model.addChangedUser(user.getValue());
			});
			internalPane.getChildren().add(comboBox);
			internalPane.setLeftAnchor(comboBox, 0.0);
			internalPane.setRightAnchor(comboBox, 0.0);
			property.set(internalPane);
			return property;
		});
		onlineColumn.setCellValueFactory(user -> {
			ObjectProperty<AnchorPane> property = new SimpleObjectProperty<>();
			AnchorPane internalPane = new AnchorPane();
			ComboBox<String> comboBox = new ComboBox<>();
			comboBox.getItems().add("Онлайн");
			comboBox.getItems().add("Оффлайн");
			comboBox.getSelectionModel()
					.select((user.getValue().getOnline() ? "Онлайн" : "Оффлайн"));
			comboBox.valueProperty().addListener(e -> {
				usersTable.getSelectionModel().select(user.getValue());
				user.getValue().setOnline(
						(comboBox.getSelectionModel().getSelectedItem() == "Онлайн") ? true
								: false);
				model.addChangedUser(user.getValue());
			});
			internalPane.getChildren().add(comboBox);
			internalPane.setLeftAnchor(comboBox, 0.0);
			internalPane.setRightAnchor(comboBox, 0.0);
			property.set(internalPane);
			return property;
		});
		usersTable.setItems(model.getData());
		umChangeOnline.setOnMouseClicked(e -> {
			model.changeOnline(usersTable.getSelectionModel().getSelectedItem());
			usersTable.refresh();
		});
		umSave.setOnMouseClicked(e -> {
			model.loadUsersUpdates();
		});
		umAdd.setOnMouseClicked(e -> {
			adminTabPane.getSelectionModel().select(registryTab);
		});
		umChangePhoto.setOnMouseClicked(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Выбор фотографии");
			File file = fileChooser.showOpenDialog(stage);
			try (FileInputStream fileIn = new FileInputStream(file.getAbsolutePath())) {
				byte[] localBuffer = new byte[fileIn.available()];
				fileIn.read(localBuffer, 0, fileIn.available());
				usersTable.getSelectionModel().getSelectedItem().setImage(localBuffer);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		});
		umDelete.setOnMouseClicked(e -> {
			model.deleteUser(usersTable.getSelectionModel().getSelectedItem());
			usersTable.refresh();
		});
		// добавляет признак того, что содержание представления было изменено
		model.getChangeExistProperty()
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						if (newValue) {
							userManagerTab.setText("* Управление пользователями");
						} else {
							userManagerTab.setText("Управление пользователями");
						}
					});

				});
	}

	@Override
	protected RegistryModel createPresentaionModel() {
		return new RegistryModel();
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
	 * Метод для переноса окна
	 */
	@FXML
	public void moveWindowMousePress() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		mousePointX = location.getX();
		mousePointY = location.getY();
		windowPointX = stage.getX();
		windowPointY = stage.getY();
		if (pressedFlag) {
			rootPane.getStyleClass().add("textFieldNotErr");
		} else {
			rootPane.getStyleClass().add("textFieldErr");
		}

		moveLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			Point location1 = MouseInfo.getPointerInfo().getLocation();
			stage.setX((int) (windowPointX + (location1.getX() - mousePointX)));
			stage.setY((int) (windowPointY + (location1.getY() - mousePointY)));
		});

		moveLabel.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (pressedFlag) {
				rootPane.getStyleClass().remove("textFieldNotErr");
			} else {
				rootPane.getStyleClass().remove("textFieldErr");
			}
		});
	}

	/**
	 * Класс
	 */
	private class ChangeStyleListener implements ChangeListener<String> {

		private Control field;

		public ChangeStyleListener(Control field) {
			super();
			this.field = field;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			if (newValue.isEmpty()) {
				field.getStyleClass().add("textFieldErr");
			} else {
				field.getStyleClass().remove("textFieldErr");
			}

			if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()
					|| nameField.getText().isEmpty() || surnameField.getText().isEmpty()
					|| patronymicField.getText().isEmpty() || positionField.getText().isEmpty()
					|| (null == subElBox.getSelectionModel().getSelectedItem())) {
				applyButton.setDisable(true);
				pressedFlag = false;
			} else {
				applyButton.setDisable(false);
				pressedFlag = true;
			}

		}
	}

}
