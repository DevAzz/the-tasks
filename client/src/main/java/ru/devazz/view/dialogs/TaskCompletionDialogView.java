package ru.devazz.view.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import ru.devazz.entities.Task;
import ru.devazz.model.TaskCompletionDialogViewModel;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;
import ru.devazz.view.AbstractView;

import java.io.File;

/**
 * Представление диалога завершения задачи
 */
public class TaskCompletionDialogView extends AbstractView<TaskCompletionDialogViewModel> {

	/** Корневой контейнер */
	@FXML
	private ScrollPane rootPane;

	/** Поле "Документ" */
	@FXML
	private TextField documentTextField;

	/** Лейбл заголовок */
	@FXML
	private TextField taskName;

	/** Экземпляр диалога */
	private Dialog<Task> dialog;

	@Override
	public void initialize() {
		bindView();

	}

	/**
	 * Связывание представления с моделью
	 */
	private void bindView() {
		// Содержание компонентов
		Bindings.bindBidirectional(taskName.textProperty(), model.getNameLabelProperty());
		Bindings.bindBidirectional(documentTextField.textProperty(), model.getDocumentStringProperty());
	}

	/**
	 * Выполняет ззавершение задачи
	 */
	@FXML
	private void completeTask() {
		try {
			model.completeTask();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Логирование
		} finally {
			dialog.setResult(model.getTask());
			dialog.close();
		}
	}

	/**
	 * Добавляет документ
	 */
	@FXML
	private void addDocument() {
		// Отправка документа на сервер
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Выбор документа");
		File file = fileChooser.showOpenDialog(stage);
		if (Utils.getInstance().isCorrectFileType(file.getAbsolutePath())) {
			if (null != file) {
				model.setDocumentStringPropertyValue(file.getAbsolutePath());
			}
		} else {
			DialogUtils.getInstance().showAlertDialog("Ошибка выбора файла",
													  "Выбран исполняемый файл. \n\rДобавление исполняемых файлов запрещено", AlertType.WARNING);
		}

	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public ScrollPane getRootPane() {
		return rootPane;
	}

	/**
	 * Устанавливает значение полю {@link#dialog}
	 *
	 * @param dialog значение поля
	 */
	public void setDialog(Dialog<Task> dialog) {
		this.dialog = dialog;
	}

	@Override
	protected TaskCompletionDialogViewModel createPresentationModel() {
		return new TaskCompletionDialogViewModel();
	}

}
