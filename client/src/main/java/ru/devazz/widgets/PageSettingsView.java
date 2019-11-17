package ru.devazz.widgets;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import ru.devazz.model.PageSettingsModel;
import ru.devazz.view.AbstractView;

import java.util.Map;

/**
 * Представление формы настроек паджинации
 */
public class PageSettingsView extends AbstractView<PageSettingsModel> {

	/** Ключ минимального числа записей на странице */
	public static final String MIN_PAGE_ENTRIES_COUNT_KEY = "minPageEntriesCountKey";

	/** Ключ максимального числа записей на странице */
	public static final String MAX_PAGE_ENTRIES_COUNT_KEY = "maxPageEntriesCountKey";

	/** Ключ минимального числа страниц в блоке */
	public static final String MIN_PAGES_COUNT_KEY = "minPagesCountKey";

	/** Ключ максимального числа страниц в блоке */
	public static final String MAX_PAGES_COUNT_KEY = "maxPagesCountKey";

	/** Ключ дефолтного количества страниц в блоке */
	public static final String DEFAULT_PAGES_COUNT = "defaultPagesCountKey";

	/** Ключ дефолтного количества записей на странице */
	public static final String DEFAULT_PAGES_ENTRIES_COUNT = "defaultPagesEntriesCount";

	/** Спиннер выбора количества записей на странице */
	@FXML
	private Spinner<Integer> countPageEntriesSpinner;

	/** Спиннер выбора количества страниц в блоке */
	@FXML
	private Spinner<Integer> countPagesSpinner;

	/** Кнопка, сохраняющая настройки паджинации */
	@FXML
	private Button savePageSettingsButton;

	/** Кнопка восстанавливающая настройки паджинации по умолчанию */
	@FXML
	private Button defaultPageSettingsButton;

	/** Корневая панель */
	@FXML
	private AnchorPane rootPageSettingsPane;

	/** Инициализационная карта */
	private Map<String, Integer> initMap;

	/** Действие сохранения настроек паджинации */
	private Runnable saveRunnable;

	@Override
	public void initialize() {
	}

	/**
	 * Инициализирует форму
	 *
	 * @param aInitMap карта инициализации
	 */
	public void initContent(Map<String, Integer> aInitMap, Runnable aSaveRunnable) {
		if (null != aInitMap) {
			initMap = aInitMap;
			saveRunnable = aSaveRunnable;

			// @formatter:off
			SpinnerValueFactory<Integer> countEntriesFactory = new SpinnerValueFactory.
					IntegerSpinnerValueFactory(
					aInitMap.get(MIN_PAGE_ENTRIES_COUNT_KEY),
					aInitMap.get(MAX_PAGE_ENTRIES_COUNT_KEY),
					aInitMap.get(DEFAULT_PAGES_ENTRIES_COUNT));
			// @formatter:on
			countPageEntriesSpinner.setValueFactory(countEntriesFactory);
			countPageEntriesSpinner.setOnScroll(event -> {
				if (event.getDeltaY() > 0) {
					countPageEntriesSpinner.increment();
				} else if (event.getDeltaY() < 0) {
					countPageEntriesSpinner.decrement();
				}
			});
			countPageEntriesSpinner.focusedProperty().addListener((s, ov, nv) -> {
				if (nv) {
					return;
				}
				commitEditorText(countPageEntriesSpinner);
			});
			countPageEntriesSpinner.getEditor().textProperty()
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						if (!newValue.isEmpty()) {
							if (!newValue.matches("\\d*")) {
								countPageEntriesSpinner.getEditor()
										.setText(newValue.replaceAll("[^\\d]", ""));
							}
						} else {
							countPageEntriesSpinner.getEditor().setText(oldValue);
						}
					});

			// @formatter:off
			SpinnerValueFactory<Integer> countPagesFactory = new SpinnerValueFactory.
					IntegerSpinnerValueFactory(
					aInitMap.get(MIN_PAGES_COUNT_KEY),
					aInitMap.get(MAX_PAGES_COUNT_KEY),
					aInitMap.get(DEFAULT_PAGES_COUNT));
			// @formatter:on
			countPagesSpinner.setValueFactory(countPagesFactory);
			countPagesSpinner.setOnScroll(event -> {
				if (event.getDeltaY() > 0) {
					countPagesSpinner.increment();
				} else if (event.getDeltaY() < 0) {
					countPagesSpinner.decrement();
				}
			});
			countPagesSpinner.focusedProperty().addListener((s, ov, nv) -> {
				if (nv) {
					return;
				}
				commitEditorText(countPagesSpinner);
			});
			countPagesSpinner.getEditor().textProperty()
					.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						if (!newValue.isEmpty()) {
							if (!newValue.matches("\\d*")) {
								countPagesSpinner.getEditor()
										.setText(newValue.replaceAll("[^\\d]", ""));
							}
						} else {
							countPagesSpinner.getEditor().setText(oldValue);
						}
					});

			model.initProperties(initMap);

			Bindings.bindBidirectional(countPageEntriesSpinner.getValueFactory().valueProperty(),
					model.getCountPageEntriesProperty());
			Bindings.bindBidirectional(countPagesSpinner.getValueFactory().valueProperty(),
					model.getCountPagesProperty());
		}
	}

	/**
	 * Коммит значения spinner`а
	 */
	private <T> void commitEditorText(Spinner<T> spinner) {
		if (!spinner.isEditable()) {
			return;
		}
		String text = spinner.getEditor().getText();
		SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
		if (valueFactory != null) {
			StringConverter<T> converter = valueFactory.getConverter();
			if (converter != null) {
				T value = converter.fromString(text);
				valueFactory.setValue(value);
			}
		}
	}

	/**
	 * Устанавливает настройки паджинации по умолчанию
	 */
	@FXML
	public void defaultPageSettings() {
		countPageEntriesSpinner.getValueFactory()
				.setValue(initMap.get(DEFAULT_PAGES_ENTRIES_COUNT));
		countPagesSpinner.getValueFactory().setValue(initMap.get(DEFAULT_PAGES_COUNT));
		if (null != saveRunnable) {
			Platform.runLater(saveRunnable);
		}
	}

	/**
	 * Сохранение настроек паджинации
	 */
	@FXML
	public void saveSettings() {
		if (null != saveRunnable) {
			Platform.runLater(saveRunnable);
		}
	}

	@Override
	protected PageSettingsModel createPresentaionModel() {
		return new PageSettingsModel();
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public AnchorPane getRootPane() {
		return rootPageSettingsPane;
	}

}
