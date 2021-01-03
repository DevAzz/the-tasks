package ru.devazz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import ru.devazz.utils.Utils;
import ru.devazz.view.RegistryView;

/**
 * Приложение регистрации пользователей
 */
@SpringBootApplication
@EnableJms
public class RegistryApp extends AbstractJavaFxApplicationSupport {

	@Override
	public void start(Stage primaryStage) throws Exception {
		RegistryView view = Utils.getInstance().loadView(RegistryView.class);
		view.setStage(primaryStage);
		Scene scene = new Scene(view.getRootPane());
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Точка входа
	 *
	 * @param args аргументы приложения
	 * @throws Exception в случае если не введены параметры
	 */
	public static void main(String[] args) throws Exception {
		launchApp(RegistryApp.class, args);
	}
}
