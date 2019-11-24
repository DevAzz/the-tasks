package ru.devazz;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import ru.devazz.utils.PushUpTypes;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;
import ru.devazz.view.AuthView;
import ru.devazz.view.RootView;

import java.io.IOException;

/**
 * Основной класс - точка входа в приложение
 */
@SpringBootApplication
@EnableJms
public class MainApp extends AbstractJavaFxApplicationSupport {

	/** Окно приложения */
	private Stage primaryStage;

	/** Окно приложения */
	private Scene scene;

	/** Корневой контейнер приложения */
	private BorderPane rootLayout;

	/** Высота окна */
	private double windowHeight = 850.0;

	/** Ширина окна */
	private double windowWidth = 1300.0;

	/**
	 * Точка входа
	 *
	 * @param args аргументы приложения
	 * @throws Exception в случае если не введены параметры
	 */
	public static void main(String[] args) throws Exception {
		launchApp(MainApp.class, args);
	}

	/**
	 * Инициализация корневого контейнера
	 */
	public void initRootLayout() {
		try {
			primaryStage.initStyle(StageStyle.TRANSPARENT);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/rootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			rootLayout.setPrefSize(512, 467);

			rootLayout.getStyleClass().add("background");
			primaryStage.setOpacity(0);

			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			scene.setFill(Color.TRANSPARENT);

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Отображает корневое представление приложения
	 */
	public void showRootView() {
		try {
			// Создание нового stage чтобы убрать голубизну
			Stage rStage = new Stage();
			rStage.setScene(scene);
			rStage.initStyle(StageStyle.UNDECORATED);

			RootView view = Utils.getInstance().loadView(RootView.class);
			view.setStage(rStage);
			rootLayout.setPrefSize(windowWidth, windowHeight);
			rootLayout.setCenter(view.getRootComposite());
			rStage.sizeToScene();

			// Плавное закрытие окна
			view.addCloseListener(() -> {
				Timeline hideLine = new Timeline(new KeyFrame(Duration.seconds(0.025),
						evt -> rStage
								.setOpacity((rStage.getOpacity() >= 0.1) ? rStage.getOpacity() - 0.1
										: rStage.getOpacity())));
				hideLine.setCycleCount(28);
				hideLine.play();
				hideLine.setOnFinished(e -> {
					rStage.close();
					System.exit(0);
				});
			});

			// Анимация появления окна
			rStage.setOpacity(0);
			boolean lowScreenResolution = false;
			Rectangle2D screenSize = Screen.getPrimary().getBounds();
			if ((screenSize.getWidth() <= windowWidth)
					|| (screenSize.getHeight() <= windowHeight)) {
				lowScreenResolution = true;
				rStage.setX(0);
				rStage.setY(0);
				rootLayout.setPrefSize(screenSize.getWidth(), screenSize.getHeight());
				windowWidth = screenSize.getWidth();
				windowHeight = screenSize.getHeight();
			}

			// открытие нового stage
			rStage.show();

			Timeline showLine = new Timeline(new KeyFrame(Duration.seconds(0.03),
					evt -> rStage
							.setOpacity((rStage.getOpacity() <= 0.9) ? rStage.getOpacity() + 0.1
									: rStage.getOpacity())));
			showLine.setCycleCount(28);
			showLine.setDelay(Duration.seconds(1));
			showLine.play();

			if (lowScreenResolution) {
				DialogUtils.getInstance().showAlertDialog("Низкое разрешение экрана",
														  "Разрешение слишком низкое, некоторые элементы управления могут не поместиться на экран",
														  AlertType.WARNING);
			}
		} catch (Throwable e) {
			// TODO Сделать логирование
			e.printStackTrace();
		}
	}

	/**
	 * Отображает представление авторизации
	 */
	public void showAuthView() {
		try {
			// Show Animation
			// BackGround Animation Show
			Timeline showLine = new Timeline(
					new KeyFrame(Duration.seconds(0.03),
							evt -> primaryStage.setOpacity((primaryStage.getOpacity() <= 0.9)
									? primaryStage.getOpacity() + 0.1
									: primaryStage.getOpacity())));
			showLine.setCycleCount(20);
			showLine.setDelay(Duration.seconds(1.5));
			showLine.play();
			showLine.setOnFinished(e -> {
				// Clear Transparency
				primaryStage.setOpacity(1);
				scene.setFill(Color.web("#3c3f41"));
			});
			// Remove BG and resize
			rootLayout.getStyleClass().remove("background");
			rootLayout.setPrefSize(284, 350);

			// Set AuthView to Center
			Rectangle2D rect = Screen.getPrimary().getVisualBounds();
			primaryStage.setY((rect.getHeight() / 2) - (rootLayout.getPrefHeight() / 2));
			primaryStage.setX((rect.getWidth() / 2) - (rootLayout.getPrefWidth() / 2));

			AuthView view = Utils.getInstance().loadView(AuthView.class);
			view.setStage(primaryStage);
			view.addCloseListener(() -> {
				Timeline hideLine = new Timeline(
						new KeyFrame(Duration.seconds(0.06),
								evt -> primaryStage.setOpacity((primaryStage.getOpacity() >= 0.1)
										? primaryStage.getOpacity() - 0.1
										: primaryStage.getOpacity())));
				hideLine.setCycleCount(28);
				hideLine.setDelay(Duration.seconds(1));
				hideLine.play();
				hideLine.setOnFinished(e -> {
					primaryStage.hide();
				});
				Platform.runLater(this::showRootView);
			});

			rootLayout.setCenter(view.getRootPane());
			primaryStage.sizeToScene();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage aPrimaryStage) throws Exception {
		this.primaryStage = aPrimaryStage;
		initRootLayout();
		showAuthView();
	}

}
