package ru.devazz.view.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import jfxtras.scene.control.ListView;
import ru.siencesquad.hqtasks.ui.entities.DefaultTask;
import ru.siencesquad.hqtasks.ui.model.dialogmodel.DefaultTaskDialogModel;
import ru.siencesquad.hqtasks.ui.view.AbstractView;

public class DefaultTasksDialog extends AbstractView<DefaultTaskDialogModel> {
	/** Подложка диалога */
	@FXML
	private AnchorPane rootPaneDialog;

	/** Список с типовыми задачами */
	@FXML
	private ListView<DefaultTask> listTasks;

	/** Переменная для хранения текущего объекта Dialog */
	private Dialog<DefaultTask> dialog;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	@FXML
	public void initialize() {
		// Биндинг списка типовых задач
		Bindings.bindBidirectional(listTasks.itemsProperty(), model.getDefaultTaskListProperity());
		listTasks.setOnMouseClicked(e -> {
			model.setSelectedDefaultTask(listTasks.getSelectedItem());
			if (e.getClickCount() == 2) {
				dialog.setResult(model.getSelectedDefaultTask());
				dialog.close();
			}
		});

	}

	/**
	 * Возвращает подложку диалога
	 *
	 * @return AnchorPane rootPaneDialog
	 */
	public AnchorPane getRootPaneDialog() {
		return rootPaneDialog;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected DefaultTaskDialogModel createPresentaionModel() {
		return new DefaultTaskDialogModel();
	}

	/**
	 * Возвращает {@link#dialog}
	 *
	 * @return the {@link#dialog}
	 */
	public Dialog<DefaultTask> getDialog() {
		return dialog;
	}

	/**
	 * Устанавливает значение полю {@link#dialog}
	 *
	 * @param dialog значение поля
	 */
	public void setDialog(Dialog<DefaultTask> dialog) {
		this.dialog = dialog;
	}

}
