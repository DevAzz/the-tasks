package ru.devazz.utils.dialogs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.*;
import javafx.util.Duration;
import ru.devazz.entities.DefaultTask;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.entities.Task;
import ru.devazz.utils.PushUpTypes;
import ru.devazz.utils.Utils;
import ru.devazz.view.RootView;
import ru.devazz.view.dialogs.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Утилиты диалогов
 */
public class DialogUtils {

	/** Единственный экземпляр класса */
	private static DialogUtils instance;

	/** Счетчик уведомлений */
	private int pushUpCounter = 0;

	/** Счетчик созданных уведомлений */
	private int createdPushUpCounter = 0;

	/**
	 * Конструктор
	 */
	private DialogUtils() {
	}

	/**
	 * Возвращает единственный экземпляр класса
	 *
	 * @return единственный экземпляр класса DialogUtils
	 */
	public static DialogUtils getInstance() {
		if (null == instance) {
			instance = new DialogUtils();
		}
		return instance;
	}

	/**
	 * Увелечение счетчика созданных уведомлений на 1
	 */
	public void createdPushUpCounterIncrement() {
		createdPushUpCounter++;
	}

	/**
	 * Уменьшение счетчика созданных уведомлений на 1
	 */
	public void createdPushUpCounterDecrement() {
		if (createdPushUpCounter > 0) {
			createdPushUpCounter--;
		}
	}

	/**
	 * Увелечение счетчика уведомлений на 1
	 */
	public void pushUpCounterIncrement() {
		pushUpCounter++;
	}

	/**
	 * Уменьшение счетчика уведомлений на 1
	 */
	public void pushUpCounterDecrement() {
		if (pushUpCounter > 0) {
			pushUpCounter--;
		}
	}

	/**
	 * Получение значений счетчика уведомлений
	 *
	 * @return the pushUpCounter
	 */
	public int getPushUpCounter() {
		return pushUpCounter;
	}

	/**
	 * Устанавливает значение счетчика уведомлений
	 *
	 * @param pushUpCounter the pushUpCounter to set
	 */
	public void setPushUpCounter(int pushUpCounter) {
		this.pushUpCounter = pushUpCounter;
	}

	/**
	 * Отображает диалог завершения задачи
	 *
	 * @param aStage родительское окно
	 * @return факт удаления задачи
	 * @throws IOException в случае ошибки
	 */
	public Boolean showRemoveTaskDialog(Stage aStage, Task aTask) throws IOException {
		Boolean resultValue = false;
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setResizable(true);

		RemoveTaskDialogView view = Utils.getInstance().loadView(RemoveTaskDialogView.class);
		view.getModel().setTask(aTask);
		view.setDialog(dialog);
		view.setStage(aStage);

		dialog.getDialogPane().setContent(view.getRootPane());
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(aStage);
		dialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/css/main.css").toExternalForm());

		Window window = dialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(event -> window.hide());

		Optional<Boolean> result = dialog.showAndWait();
		if (result.isPresent()) {
			resultValue = result.get();
		}
		return resultValue;
	}

	/**
	 * Отображает диалог завершения задачи
	 *
	 * @param aStage родительское окно
	 * @param aConsumer действия по закрытию диалога
	 * @throws IOException в случае ошибки
	 */
	public void showCompletionTaskDialog(Stage aStage, Consumer<Task> aConsumer, Task aTask)
			throws IOException {
		Dialog<Task> dialog = new Dialog<>();
		dialog.setResizable(true);

		TaskCompletionDialogView view = Utils.getInstance()
				.loadView(TaskCompletionDialogView.class);
		view.getModel().setTask(aTask);
		view.setDialog(dialog);
		view.setStage(aStage);

		dialog.setResultConverter(dialogButton -> {
			return view.getModel().getTask();
		});

		dialog.getDialogPane().setContent(view.getRootPane());
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(aStage);
		dialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/css/main.css").toExternalForm());

		Window window = dialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(event -> window.hide());

		Optional<Task> result = dialog.showAndWait();
		if (null != result) {
			result.ifPresent(aConsumer);
		}
	}

	/**
	 * Отображает диалог принятия решения по документу
	 *
	 * @param aStage родительское окно
	 * @param aConsumer действия по закрытию диалога
	 * @throws IOException в случае ошибки
	 */
	public void showDecisionDialog(Stage aStage, Consumer<Task> aConsumer, Task aTask)
			throws IOException {
		Dialog<Task> dialog = new Dialog<>();
		dialog.setResizable(true);

		DecisionDialogView view = Utils.getInstance().loadView(DecisionDialogView.class);
		view.getModel().setTask(aTask);
		view.initContent();
		view.setDialog(dialog);

		dialog.setResultConverter(dialogButton -> {
			return view.getModel().getTask();
		});

		dialog.getDialogPane().setContent(view.getRootPane());
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(aStage);
		dialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/css/main.css").toExternalForm());

		Window window = dialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(event -> window.hide());

		Optional<Task> result = dialog.showAndWait();
		if (null != result) {
			result.ifPresent(aConsumer);
		}
	}

	/**
	 * Отображает диалог выбора должностей
	 *
	 * @param aStage Окно
	 * @param aSelectedList список выделенных элементов подчиненности
	 * @param aConsumer действия по закрытию диалога
	 * @param aMultiplySelectionMode режим множественного выбора
	 */
	public void showSelectSubElsDialogWithSelectedSubs(Stage aStage,
			ObservableList<SubordinationElement> aSelectedList,
			Consumer<? super List<SubordinationElement>> aConsumer, Boolean aMultiplySelectionMode,
			Boolean aSearchFlag) {
		try {
			Dialog<List<SubordinationElement>> dialog = new Dialog<>();
			dialog.setTitle("Выбор подразделений");
			ButtonType selectButtonType = new ButtonType("Выбрать", ButtonData.OK_DONE);
			ButtonType cancelButtonType = new ButtonType("Отмена", ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, selectButtonType);

			SubFilterDialogView view = Utils.getInstance().loadView(SubFilterDialogView.class);
			view.getModel().setSearchFlag(aSearchFlag);
			view.getModel().getSelectedSubList().addAll(aSelectedList);
			view.initListView(aMultiplySelectionMode);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == selectButtonType) {
					return view.getModel().getSelectedSubList();
				}
				return null;
			});
			dialog.getDialogPane().setContent(view.getRootPane());
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(aStage);
			dialog.getDialogPane().getStylesheets()
					.add(getClass().getResource("/css/main.css").toExternalForm());

			Optional<List<SubordinationElement>> result = dialog.showAndWait();
			if (null != result) {
				result.ifPresent(aConsumer);
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Отображает диалог выбора должностей
	 *
	 * @param aStage Окно
	 * @param aConsumer действия по закрытию диалога
	 * @param aMultiplySelectionMode
	 */
	public void showSelectSubElsDialog(Stage aStage,
			Consumer<? super List<SubordinationElement>> aConsumer, Boolean aMultiplySelectionMode,
			Boolean aSearchFlag) {
		try {
			Dialog<List<SubordinationElement>> dialog = new Dialog<>();
			dialog.setTitle("Выбор подразделений");
			ButtonType selectButtonType = new ButtonType("Выбрать", ButtonData.OK_DONE);
			ButtonType cancelButtonType = new ButtonType("Отмена", ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, selectButtonType);

			SubFilterDialogView view = Utils.getInstance().loadView(SubFilterDialogView.class);
			view.getModel().setSearchFlag(aSearchFlag);
			view.initListView(aMultiplySelectionMode);

			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == selectButtonType) {
					return view.getModel().getSelectedSubList();
				}
				return null;
			});
			dialog.getDialogPane().setContent(view.getRootPane());
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(aStage);
			dialog.getDialogPane().getStylesheets()
					.add(getClass().getResource("/css/main.css").toExternalForm());

			Optional<List<SubordinationElement>> result = dialog.showAndWait();
			if (null != result) {
				result.ifPresent(aConsumer);
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Отображает диалог выбора
	 *
	 * @param aTitle заголовок
	 * @param aText основной текст
	 * @param aConsumer действия по закрытию
	 */
	public void showChoiceDialog(String aTitle, String aText, Consumer<ButtonType> aConsumer) {

		ButtonType selectButtonType = new ButtonType("Да", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Нет", ButtonData.CANCEL_CLOSE);

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(aTitle);
		alert.setHeaderText(aText);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getButtonTypes().clear();
		dialogPane.getButtonTypes().addAll(cancelButtonType, selectButtonType);

		dialogPane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		Optional<ButtonType> result = alert.showAndWait();
		if (null != result) {
			result.ifPresent(aConsumer);
		}
	}

	/**
	 *
	 * Отображает диалогь вывода информации
	 *
	 * @param caption заголовог
	 * @param text содержание
	 * @param type тип диалога
	 */
	public void showAlertDialog(String caption, String text, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(caption);
		alert.setHeaderText(text);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");
		alert.show();
	}

	/**
	 * Создание единственного экземпляра popup сообщения и его показ
	 *
	 * @param caption - заголовок
	 * @param text - текст
	 * @param type - тип уведомления (OVERDUEPUSH/NEWPUSH/ATTENTIONPUSH) - время
	 *            истекает, новая задача, просто сообщение
	 * @param aListener Обработчик перехода к задаче
	 */
	public void showPushUp(String caption, String text, PushUpTypes type,
			RootView.GoOverTaskListener aListener) {
		Thread thread = new Thread(() -> Platform.runLater(() -> {
			try {
				if (createdPushUpCounter == 0) {
					setPushUpCounter(0);
				}
				// Создание Сообщения
				Stage pushUpStage = new Stage();
				pushUpStage.initStyle(StageStyle.TRANSPARENT);
				pushUpStage.setAlwaysOnTop(true);

				PushUpMessageView pushUpView = Utils.getInstance()
						.loadView(PushUpMessageView.class);
				Scene pushUpScene = new Scene(pushUpView.getRoot());

				pushUpStage.setScene(pushUpScene);

				pushUpView.setStage(pushUpStage);
				// Установка слушателя
				pushUpView.setGoOverTaskListener(aListener);

				// Установка положения сообщения
				Rectangle2D rect = Screen.getPrimary().getVisualBounds();
				double windowWidth = rect.getWidth();
				pushUpStage.setX(rect.getWidth());
				pushUpStage
						.setY(30 + (160 * (getPushUpCounter() % ((rect.getHeight() / 160) - 1))));
				pushUpCounterIncrement();

				// Установка параметров
				pushUpView.getCaption().setText(caption);
				pushUpView.getText().setText(text);
				pushUpView.setType(type);
				pushUpView.getModel().setWindowWidth(windowWidth);

				createdPushUpCounterIncrement();
				// Отображение
				pushUpStage.show();

				// Анимация появления
				Timeline showLine = new Timeline(new KeyFrame(Duration.seconds(0.005), evt -> {
					if (pushUpStage.getX() <= 0) {
						pushUpStage.setX(windowWidth);
					}
					if (pushUpStage.getX() > (windowWidth - 366)) {
						pushUpStage.setX(pushUpStage.getX() - 4.06);
					}
				}));
				showLine.setCycleCount(91);
				showLine.play();
				showLine.setOnFinished(e2 -> {
					new Thread(() -> {
						try {
							Thread.currentThread().setName("Popup close thread");
							Thread.sleep(6000);
							Platform.runLater(() -> pushUpView.hide());
						} catch (InterruptedException e1) {
							// TODO Логирование
							e1.printStackTrace();
						}
					}).start();
				});
			} catch (Exception e3) {
				e3.printStackTrace();
			}

		}));
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Отображение диалога выбора типовых задач
	 *
	 * @param aStage - Stage на который вызывается диалог
	 * @param consumer - действие по закрытию диалога
	 * @param executorSUID - SUID должности
	 */
	public void showSelectDefaultTaskDialog(Stage aStage, Consumer<? super DefaultTask> consumer,
			Long executorSUID) {
		try {
			Dialog<DefaultTask> dialog = new Dialog<>();
			ButtonType selectButtonType = new ButtonType("Выбрать", ButtonData.OK_DONE);
			ButtonType cancelButtonType = new ButtonType("Отмена", ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, selectButtonType);

			DefaultTasksDialog view = Utils.getInstance().loadView(DefaultTasksDialog.class);
			view.getModel().loadDefaultTasksBySub(executorSUID);
			view.setDialog(dialog);

			dialog.getDialogPane().setContent(view.getRootPaneDialog());
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(aStage);
			dialog.getDialogPane().getStylesheets()
					.add(getClass().getResource("/css/main.css").toExternalForm());
			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == selectButtonType) {
					return view.getModel().getSelectedDefaultTask();
				}
				return null;
			});
			Optional<DefaultTask> result = dialog.showAndWait();

			if (null != result) {
				result.ifPresent(consumer);
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}
}
