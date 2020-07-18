package ru.devazz.widgets;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import jfxtras.scene.control.LocalDateTimeTextField;
import ru.devazz.model.CustomTimeIntervalModel;
import ru.devazz.view.AbstractView;

/**
 * Представление компонента выбора временного интервала
 */
public class CustomTimeIntervalView extends AbstractView<CustomTimeIntervalModel> {

	/** Панель выбора дат для фильтра "Выбрать временной промежуток" */
	@FXML
	private HBox customFilterDatePanel;

	/** Начальная дата фильтрации по временному интервалу */
	@FXML
	private LocalDateTimeTextField startDateTimeIntervalTextField;

	/** Конечная дата фильтрации по временному интервалу */
	@FXML
	private LocalDateTimeTextField endDateTimeIntervalTextField;

	/** Кнопка фильтрации по заданному промежутку */
	@FXML
	private Button filterTimeIntervalButton;

	/** Очистка выбранного интервала */
	@FXML
	private Button clearCustomTimeintervalButton;

	/** Поток выполнения действия поиска */
	private Runnable searchRunnable;

	@Override
	public void initialize() {
		Bindings.bindBidirectional(startDateTimeIntervalTextField.textProperty(),
				model.getFromTimeIntervalProperty());
		Bindings.bindBidirectional(endDateTimeIntervalTextField.textProperty(),
				model.getToTimeIntervalProperty());
		Bindings.bindBidirectional(filterTimeIntervalButton.disableProperty(),
				model.getDisableSearchButton());

		endDateTimeIntervalTextField.getStyleClass().add("dateTimeTextFieldErr");
		startDateTimeIntervalTextField.getStyleClass().add("dateTimeTextFieldErr");

		model.getToTimeIntervalProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					endDateTimeIntervalTextField.setPromptText("");
					String errStyle = "dateTimeTextFieldErr";
					String normal = "dateTimeTextFieldNormal";
					if (newValue.isEmpty()) {
						endDateTimeIntervalTextField.getStyleClass().add(errStyle);
						endDateTimeIntervalTextField.getStyleClass().remove(normal);
					} else {
						endDateTimeIntervalTextField.setPromptText(newValue);
						endDateTimeIntervalTextField.getStyleClass().remove(errStyle);
						endDateTimeIntervalTextField.getStyleClass().add(normal);
					}
				});

		model.getFromTimeIntervalProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						startDateTimeIntervalTextField.setPromptText("");
						String errStyle = "dateTimeTextFieldErr";
						String normal = "dateTimeTextFieldNormal";

						if (newValue.isEmpty()) {
							startDateTimeIntervalTextField.getStyleClass().add(errStyle);
							startDateTimeIntervalTextField.getStyleClass().remove(normal);
						} else {
							startDateTimeIntervalTextField.setPromptText(newValue);
							startDateTimeIntervalTextField.getStyleClass().remove(errStyle);
							startDateTimeIntervalTextField.getStyleClass().add(normal);
						}

					});

				});
	}

	/**
	 * Очищает поля выбранного временного интервала
	 */
	@FXML
	private void clear() {
		clearData();
		startDateTimeIntervalTextField.setLocalDateTime(null);
		endDateTimeIntervalTextField.setLocalDateTime(null);
	}

	/**
	 * Выполняет поиск записей
	 *
	 */
	@FXML
	private void search() {
		Thread thread = new Thread(searchRunnable);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Возвращает {@link#searchRunnable}
	 *
	 * @return the {@link#searchRunnable}
	 */
	public Runnable getSearchRunnable() {
		return searchRunnable;
	}

	/**
	 * Устанавливает значение полю {@link#searchRunnable}
	 *
	 * @param searchRunnable значение поля
	 */
	public void setSearchRunnable(Runnable searchRunnable) {
		this.searchRunnable = searchRunnable;
	}

	/**
	 * Возвращает {@link#customFilterDatePanel}
	 *
	 * @return the {@link#customFilterDatePanel}
	 */
	public HBox getCustomFilterDatePanel() {
		return customFilterDatePanel;
	}

	/**
	 * Очищает поля ввода дат
	 */
	public void clearData() {
		model.clearDateFields();
	}

	@Override
	protected CustomTimeIntervalModel createPresentationModel() {
		return new CustomTimeIntervalModel();
	}

}
