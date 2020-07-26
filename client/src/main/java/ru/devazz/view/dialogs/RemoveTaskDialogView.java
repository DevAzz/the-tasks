package ru.devazz.view.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import ru.devazz.model.RemoveTaskDialogModel;
import ru.devazz.view.AbstractView;

/**
 * Представление диалога удаления задачи
 */
public class RemoveTaskDialogView extends AbstractView<RemoveTaskDialogModel> {

	/** Корневая панель */
	@FXML
	private BorderPane rootPane;

	/** Текстовое поле наименования задачи */
	@FXML
	private TextArea taskNameTextField;

	/** Экземпляр диалога */
	private Dialog<Boolean> dialog;

	@Override
	public void initialize() {
		Bindings.bindBidirectional(taskNameTextField.textProperty(), model.getNameTaskProperty());
	}

	/**
	 * Удаление задачи
	 */
	@FXML
	private void removeTask() {
		model.removeTask();
		dialog.setResult(true);
		dialog.close();
	}

	/**
	 * Отмена удаления
	 */
	@FXML
	private void cancel() {
		dialog.setResult(false);
		dialog.close();
	}

	public BorderPane getRootPane() {
		return rootPane;
	}

	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public void setDialog(Dialog<Boolean> dialog) {
		this.dialog = dialog;
	}

	@Override
	protected RemoveTaskDialogModel createPresentationModel() {
		return new RemoveTaskDialogModel();
	}

}
