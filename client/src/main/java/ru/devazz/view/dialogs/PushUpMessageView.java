package ru.devazz.view.dialogs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.util.Duration;
import ru.siencesquad.hqtasks.ui.model.PushUpMessageViewModel;
import ru.siencesquad.hqtasks.ui.utils.PushUpTypes;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;
import ru.siencesquad.hqtasks.ui.view.AbstractView;
import ru.siencesquad.hqtasks.ui.view.RootView.GoOverTaskListener;

public class PushUpMessageView extends AbstractView<PushUpMessageViewModel> {

	/** Корневой контейнер */
	@FXML
	private AnchorPane root;

	/** Заголовок сообщения */
	@FXML
	private Label caption;
	/** Текст сообщения */
	@FXML
	private Label text;
	/** Иконка сообщения */
	@FXML
	private ImageView icon;
	/** Кнопка для перехода к основной программе */
	@FXML
	private Button pushUpButton;

	/** Слушатель перехода на вновь созданную задачу */
	private GoOverTaskListener goOverTaskListener;

	/** Инициализация */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		Bindings.bindBidirectional(caption.textProperty(), model.getCaptionProperity());
		Bindings.bindBidirectional(text.textProperty(), model.getTextProperity());
	}

	/**
	 * Переход к созданной задаче
	 */
	@FXML
	private void goOverCreatedTask() {
		goOverTaskListener.goOver();
	}

	/**
	 * Устанавливает значение полю {@link#goOverTaskListener}
	 *
	 * @param goOverTaskListener значение поля
	 */
	public void setGoOverTaskListener(GoOverTaskListener goOverTaskListener) {
		this.goOverTaskListener = goOverTaskListener;
	}

	/**
	 * Возарящает объект кнопки
	 *
	 * @return Button
	 */
	public Button getPushUpButton() {
		return pushUpButton;
	}

	/**
	 * Получает объект кнопки
	 *
	 * @param pushUpButton
	 */
	public void setPushUpButton(Button pushUpButton) {
		this.pushUpButton = pushUpButton;
	}

	/**
	 * Получение подложки
	 *
	 * @return
	 */
	public AnchorPane getRoot() {
		return root;
	}

	/**
	 * Установка подложки
	 *
	 * @param root -anchorpane
	 */
	public void setRoot(AnchorPane root) {
		this.root = root;
	}

	/**
	 * Возвращяет заголовок (label)
	 *
	 * @return label
	 */
	public Label getCaption() {
		return caption;
	}

	/**
	 * Устанавливает label заголовка
	 *
	 * @param caption label
	 */
	public void setCaption(Label caption) {
		this.caption = caption;
	}

	/**
	 * Возвращяет текст (label)
	 *
	 * @return label текст
	 */
	public Label getText() {
		return text;
	}

	/**
	 * Устанавливает label текста
	 *
	 * @param text - label
	 */
	public void setText(Label text) {
		this.text = text;
	}

	/**
	 * Возвращает контейнер изображения
	 *
	 * @return
	 */
	public ImageView getIcon() {
		return icon;
	}

	/**
	 * Устанавливает контейнер изображения
	 *
	 * @param icon - ImageView
	 */
	public void setIcon(ImageView icon) {
		this.icon = icon;
	}

	/**
	 * Функция прячет popup сообщение
	 */
	@FXML
	public void hide() {
		if (stage.getX() < Screen.getPrimary().getVisualBounds().getWidth()) {
			Timeline hideLine = new Timeline(new KeyFrame(Duration.seconds(0.005), evt -> {
				if (stage.getX() < (model.getWindowWidth() - 366)) {
					stage.setX(model.getWindowWidth() - 366);
				}
				stage.setX(stage.getX() + 4.06);
			}));
			hideLine.setCycleCount(91);
			hideLine.play();
			hideLine.setOnFinished(e -> {
				stage.setX(model.getWindowWidth());
				DialogUtils.getInstance().createdPushUpCounterDecrement();
				stage.close();
			});
		}
	}

	@Override
	protected PushUpMessageViewModel createPresentaionModel() {
		return new PushUpMessageViewModel();
	}

	/**
	 * Устанавливает тип уведомления
	 *
	 * @param type тип уведомления
	 */
	public void setType(PushUpTypes type) {
		pushUpButton.setVisible(true);
		switch (type) {
		case TIME_LEFT_OVER_PUSH:
			icon.setImage(new Image("/css/popupTime.png"));
			break;
		case NEW_PUSH:
			icon.setImage(new Image("/css/popupAdd.png"));
			break;
		case ATTENTION_PUSH:
			icon.setImage(new Image("/css/popupAttention.png"));
			pushUpButton.setVisible(false);
			break;
		case HELLO_PUSH:
			icon.setImage(new Image("/css/starRED.png"));
			pushUpButton.setVisible(false);
			break;
		case DELETED_PUSH:
			icon.setImage(new Image("/css/deletePush.png"));
			pushUpButton.setVisible(false);
			break;
		case DONE_PUSH:
			icon.setImage(new Image("/css/checked.png"));
			break;
		case CLOSED_PUSH:
			icon.setImage(new Image("/css/lock.png"));
			break;
		case FAILED_PUSH:
			icon.setImage(new Image("/css/faildPush.png"));
			break;
		case OVERDUE_PUSH:
			icon.setImage(new Image("/css/overduePush.png"));
			break;
		case REWORK_PUSH:
			icon.setImage(new Image("/css/reworkPush.png"));
			break;
		case USER_ONLINE_PUSH:
			icon.setImage(new Image("/css/userOnline.png"));
			pushUpButton.setVisible(false);
			break;
		default:
			pushUpButton.setVisible(false);
			icon.setImage(new Image("/css/starRED.png"));
		}

	}

}
