package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.sciencesquad.hqtasks.server.exceptions.AuthorizeUserOnlineException;
import ru.siencesquad.hqtasks.ui.model.AuthPresentationModel;
import ru.siencesquad.hqtasks.ui.utils.LoadAnimation;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

/**
 * Контроллер диалога авторизации
 */
public class AuthView extends AbstractView<AuthPresentationModel> {

	/** Коревой контейнер */
	@FXML
	private AnchorPane rootPane;

	/** Картинка для анимации загрузки */
	@FXML
	private ImageView loadImage;

	/** Текстовое поле ввода логина */
	@FXML
	private TextField login;

	/** Текстовое поле ввода пароля */
	@FXML
	private PasswordField password;

	/** Лейбл, отражающий процесс входа в систему */
	@FXML
	private Label enterLabel;

	/** Кнопка входа */
	@FXML
	private Button enterButton;

	/** Анимация загрузки */
	private LoadAnimation loadAnimation;

	/**
	 * Метод входа в систему
	 */
	@FXML
	public void enter() {
		enterLabel.setText("Вход в систему");

		loadAnimation.start();

		model.setEnterDisabled(true);
		enterLabel.requestFocus();
		Thread thread = new Thread(() -> {
			model.setAuthProcessFlag(true);
			try {
				boolean result = model.authorization();
				if (result) {
					fireClose();
					model.setAuthProcessFlag(false);
				} else {
					loadAnimation.stop();
					cauthError("Невозможно войти в систему", "Неверный логин или пароль",
							AlertType.WARNING);
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
				if (e instanceof AuthorizeUserOnlineException) {
					cauthError("Ошибка подключения", e.getMessage(), AlertType.ERROR);
				} else {
					cauthError("Ошибка подключения", "Невозможно подключиться к серверу",
							AlertType.ERROR);
				}

				loadAnimation.stop();
				try {
					Utils.getInstance().updateUserOnlineFlag(false);
				} catch (Exception e1) {
					// TODO Логирование
					e1.printStackTrace();
				}
			}

		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Обработка ошибки
	 *
	 * @param aTitle заголовок диалога
	 * @param aBody основное сообщение диалога
	 * @param aType тип диалога
	 */
	private void cauthError(String aTitle, String aBody, AlertType aType) {
		Platform.runLater(() -> {
			DialogUtils.getInstance().showAlertDialog(aTitle, aBody, aType);
			model.setAuthProcessFlag(false);
			enterLabel.setText("");
			model.setEnterDisabled(false);
			rootPane.requestFocus();
		});
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected AuthPresentationModel createPresentaionModel() {
		return new AuthPresentationModel();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(login.textProperty(), model.loginProperty());
		Bindings.bindBidirectional(password.textProperty(), model.passwordProperty());
		Bindings.bindBidirectional(enterButton.disableProperty(), model.enterDisabledProperty());

		String errStyle = "textFieldErr";

		login.getStyleClass().add(errStyle);
		password.getStyleClass().add(errStyle);

		model.loginProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (newValue.isEmpty()) {
						login.getStyleClass().add(errStyle);
					} else {
						login.getStyleClass().remove(errStyle);
					}
					windowBorderColor();
				});

		model.passwordProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (newValue.isEmpty()) {
						password.getStyleClass().add(errStyle);
					} else {
						password.getStyleClass().remove(errStyle);
					}
					windowBorderColor();
				});

		rootPane.setOnKeyPressed(event -> {
			if (KeyCode.ENTER.equals(event.getCode())
					&& !model.enterDisabledProperty().getValue()) {
				enter();
			}
		});
		loadAnimation = new LoadAnimation(141, 247, 20, 4);
		rootPane.getChildren().addAll(loadAnimation.getCircles());
	}

	/**
	 * Изменение цвета рамки окна в зависимости от пустоты полей ввода
	 */
	public void windowBorderColor() {
		if ((!model.loginProperty().getValue().isEmpty())
				&& (!model.passwordProperty().getValue().isEmpty())) {
			rootPane.getStyleClass().removeAll("textFieldErr");
			rootPane.getStyleClass().add("rootBorder");
		} else {
			rootPane.getStyleClass().removeAll("rootBorder");
			rootPane.getStyleClass().add("textFieldErr");
		}
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#setStage(javafx.stage.Stage)
	 */
	@Override
	public void setStage(Stage aStage) {
		super.setStage(aStage);
		stage.addEventHandler(ActionEvent.ACTION, event -> {
			login.requestFocus();
		});
	}

	/**
	 * Закрывает окно
	 */
	@Override
	@FXML
	protected void close() {
		stage.close();
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public AnchorPane getRootPane() {
		return rootPane;
	}

}
