package ru.devazz.view.dialogs;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jfxtras.scene.control.LocalDateTimeTextField;
import ru.devazz.entities.Task;
import ru.devazz.model.DecisionDialogViewModel;
import ru.devazz.utils.DesktopOpen;
import ru.devazz.utils.dialogs.DialogUtils;
import ru.devazz.view.AbstractView;

/**
 * Представление диалога принятия решения
 */
public class DecisionDialogView extends AbstractView<DecisionDialogViewModel> {

	/** Корневой контейнер */
	@FXML
	private ScrollPane rootPane;

	/** Дата завершения задачи */
	@FXML
	private LocalDateTimeTextField endDate;

	/** Поле "Документ" */
	@FXML
	private TextField documentTextField;

	/** Лейбл заголовок */
	@FXML
	private TextField taskName;

	/** Комментарий к задаче */
	@FXML
	private TextArea note;

	/** Экземпляр диалога */
	private Dialog<Task> dialog;

	@Override
	public void initialize() {
		bindView();
	}

	/**
	 * Связывает представление с моделью
	 */
	private void bindView() {

		// Содержание компонентов
		Bindings.bindBidirectional(taskName.textProperty(), model.getNameLabelProperty());
		Bindings.bindBidirectional(endDate.localDateTimeProperty(), model.getEndDateProperty());
		Bindings.bindBidirectional(note.textProperty(), model.getNoteLabelProperty());
		Bindings.bindBidirectional(documentTextField.textProperty(),
				model.getDocumentStringProperty());

	}

	/**
	 * Инициализирует контент представления после отображения диалога
	 */
	public void initContent() {
		endDate.setLocalDateTime(model.getTaskEndLocalDateTime());
	}

	/**
	 * Отобразить документ
	 */
	@FXML
	private void showDocument() {
		if (null != model.getTask().getDocument()) {
			new DesktopOpen("file:///" + model.getTask().getDocument().getAbsolutePath());
		} else {
			DialogUtils.getInstance().showAlertDialog("Невозможно открыть документ",
													  "Документ не был прикреплен", AlertType.ERROR);
		}
	}

	/**
	 * Отправить задачу на доработку
	 */
	@FXML
	private void sendToRework() {
		try {
			model.sendToReworkTask();
		} catch (Exception e) {
			// TODO Логирование
		} finally {
			dialog.setResult(model.getTask());
			dialog.close();
		}

	}

	/**
	 * Принять задчу
	 */
	@FXML
	private void accept() {
		try {
			model.acceptTask();
		} catch (Exception e) {
			// TODO Логирование
		} finally {
			dialog.setResult(model.getTask());
			dialog.close();
		}
	}

	/**
	 * Отклонить задачу
	 */
	@FXML
	private void reject() {
		try {
			model.rejectTask();
		} catch (Exception e) {
			// TODO Логирование
		} finally {
			dialog.setResult(model.getTask());
			dialog.close();
		}
	}

	@Override
	protected DecisionDialogViewModel createPresentaionModel() {
		return new DecisionDialogViewModel();
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

}
