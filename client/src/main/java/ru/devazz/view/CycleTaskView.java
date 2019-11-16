package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import jfxtras.scene.control.LocalTimeTextField;
import ru.sciencesquad.hqtasks.server.utils.DayOfWeek;
import ru.siencesquad.hqtasks.ui.model.CycleTaskViewModel;
import ru.siencesquad.hqtasks.ui.utils.CycleEnums;
import ru.siencesquad.hqtasks.ui.utils.CycleEnums.PropertyType;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Представление формы управления циклическим назначением задач
 */
public class CycleTaskView extends AbstractView<CycleTaskViewModel> {

	/** Корневая панель */
	@FXML
	private ScrollPane rootPane;

	/** Чек-бок назначения задачи каждую неделю */
	@FXML
	private CheckBox everyWeekCheckBox;

	/** Чек-бок назначения задачи каждый месяц */
	@FXML
	private CheckBox everyMonthCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в минутах */
	@FXML
	private CheckBox intMinuteCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в часах */
	@FXML
	private CheckBox intHoursCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в днях */
	@FXML
	private CheckBox intDayCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в месяцах */
	@FXML
	private CheckBox intMonthCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в неделях */
	@FXML
	private CheckBox intWeekCheckBox;

	/** Чек-бок назначения задачи через заданный промежуток времени в годах */
	@FXML
	private CheckBox intYearCheckBox;

	/**
	 * Спиннер выбора номера недели для назначения по промежутку (неделя, месяц,
	 * год)
	 */
	@FXML
	private Spinner<Integer> everyWeekNumberSpinner;

	/** Спиннер выбора промежутка времени для ежемесячного назначения */
	@FXML
	private Spinner<Integer> everyMonthSpinner;

	/** Спиннер выбора промежутка времени для назначения по минутам */
	@FXML
	private Spinner<Integer> intMinuteSpinner;

	/** Спиннер выбора промежутка времени для назначения по часам */
	@FXML
	private Spinner<Integer> intHoursSpinner;

	/** Спиннер выбора промежутка времени для назначения по дням */
	@FXML
	private Spinner<Integer> intDaySpinner;

	/** Спиннер выбора промежутка времени для назначения по месяцам */
	@FXML
	private Spinner<Integer> intMonthSpinner;

	/** Спиннер выбора промежутка времени для назначения по неделям */
	@FXML
	private Spinner<Integer> intWeekSpinner;

	/** Спиннер выбора промежутка времени для назначения по годам */
	@FXML
	private Spinner<Integer> intYearSpinner;

	/** Комбо-бокс выбора дня недели для еженедельного назначения */
	@FXML
	private ComboBox<DayOfWeek> everyWeekComboBox;

	/** Комбо-бокс выбора месяца назначения для назначения на конкретное число */
	@FXML
	private ComboBox<Month> everyMonthComboBox;

	/** Поле ввода времени для еженедельного назначения */
	@FXML
	private LocalTimeTextField everyWeekTimeField;

	/** Поле ввода времени для ежемесячного назначения */
	@FXML
	private LocalTimeTextField everyMonthTimeField;

	/**
	 * Поле ввода времени старта для назначения задачи через заданный промежуток
	 * времени по дням
	 */
	@FXML
	private LocalTimeTextField intDayTimeField;

	/**
	 * Поле ввода времени старта для назначения задачи через заданный промежуток
	 * времени по неделям
	 */
	@FXML
	private LocalTimeTextField intWeekTimeField;

	/**
	 * Поле ввода времени старта для назначения задачи через заданный промежуток
	 * времени по месяцам
	 */
	@FXML
	private LocalTimeTextField intMonthTimeField;

	/**
	 * Поле ввода времени старта для назначения задачи через заданный промежуток
	 * времени по годам
	 */
	@FXML
	private LocalTimeTextField intYearTimeField;

	/** Флаг блокирования скролл-бара */
	private Boolean blockScroll = false;

	/** Позиция скролл-бара на момент перевода фокуса на спиннер */
	private double vValueScroll;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		bindView();
		model.addDisableListeners();
		model.addValueListeners();
	}

	/**
	 * Связывает представление с моделью
	 */
	private void bindView() {

		for (CheckBox box : getCheckBoxList()) {
			Bindings.bindBidirectional(box.disableProperty(),
					model.createProperty(PropertyType.DISABLED, CycleEnums.getInstance()
							.getEnumElement(PropertyType.DISABLED_CHECK_BOX, box.getId())));
			Bindings.bindBidirectional(box.selectedProperty(),
					model.createProperty(PropertyType.CHECK_BOX_VALUE, CycleEnums.getInstance()
							.getEnumElement(PropertyType.CHECK_BOX_VALUE, box.getId())));
		}

		for (Spinner<Integer> spinner : getSpinners()) {
			spinnerParamsTune(spinner);
			Bindings.bindBidirectional(spinner.disableProperty(),
					model.createProperty(PropertyType.DISABLED, CycleEnums.getInstance()
							.getEnumElement(PropertyType.DISABLED_SPINNER, spinner.getId())));
			Bindings.bindBidirectional(spinner.getValueFactory().valueProperty(),
					model.createProperty(PropertyType.SPINNER_VALUE, CycleEnums.getInstance()
							.getEnumElement(PropertyType.SPINNER_VALUE, spinner.getId())));
		}

		for (LocalTimeTextField field : getTimeFields()) {
			Bindings.bindBidirectional(field.disableProperty(),
					model.createProperty(PropertyType.DISABLED, CycleEnums.getInstance()
							.getEnumElement(PropertyType.DISABLED_TIME_FIELD, field.getId())));
			Bindings.bindBidirectional(field.localTimeProperty(),
					model.createProperty(PropertyType.TIME_FIELD_VALUE, CycleEnums.getInstance()
							.getEnumElement(PropertyType.TIME_FIELD_VALUE, field.getId())));
		}
		Bindings.bindBidirectional(everyWeekComboBox.disableProperty(),
				model.createProperty(PropertyType.DISABLED, CycleEnums.getInstance()
						.getEnumElement(PropertyType.DISABLED_COMBO, everyWeekComboBox.getId())));
		Bindings.bindBidirectional(everyWeekComboBox.valueProperty(),
				model.createProperty(PropertyType.COMBO_VALUE, CycleEnums.getInstance()
						.getEnumElement(PropertyType.COMBO_VALUE, everyWeekComboBox.getId())));
		Bindings.bindBidirectional(everyMonthComboBox.disableProperty(),
				model.createProperty(PropertyType.DISABLED, CycleEnums.getInstance()
						.getEnumElement(PropertyType.DISABLED_COMBO, everyMonthComboBox.getId())));
		Bindings.bindBidirectional(everyMonthComboBox.valueProperty(),
				model.createProperty(PropertyType.COMBO_VALUE, CycleEnums.getInstance()
						.getEnumElement(PropertyType.COMBO_VALUE, everyMonthComboBox.getId())));

		everyWeekComboBox.setItems(FXCollections.observableArrayList(DayOfWeek.values()));
		everyWeekComboBox.setConverter(new StringConverter<DayOfWeek>() {

			/**
			 * @see javafx.util.StringConverter#toString(Object)
			 */
			@Override
			public String toString(DayOfWeek object) {
				return object.getName();
			}

			/**
			 * @see javafx.util.StringConverter#fromString(String)
			 */
			@Override
			public DayOfWeek fromString(String string) {
				return DayOfWeek.getDayByName(string);
			}
		});
		everyMonthComboBox.setItems(FXCollections.observableArrayList(Month.values()));
		everyMonthComboBox.setConverter(new StringConverter<Month>() {

			/**
			 * @see javafx.util.StringConverter#toString(Object)
			 */
			@Override
			public String toString(Month object) {
				String result = "";
				switch (object) {
				case JANUARY:
					result = "Январь";
					break;
				case FEBRUARY:
					result = "Февраль";
					break;
				case MARCH:
					result = "Март";
					break;
				case APRIL:
					result = "Апрель";
					break;
				case MAY:
					result = "Май";
					break;
				case JUNE:
					result = "Июнь";
					break;
				case JULY:
					result = "Июль";
					break;
				case AUGUST:
					result = "Август";
					break;
				case SEPTEMBER:
					result = "Сентябрь";
					break;
				case OCTOBER:
					result = "Октябрь";
					break;
				case NOVEMBER:
					result = "Ноябрь";
					break;
				case DECEMBER:
					result = "Декабрь";
					break;
				}
				return result;
			}

			/**
			 * @see javafx.util.StringConverter#fromString(String)
			 */
			@Override
			public Month fromString(String string) {
				Month result = null;
				switch (string) {
				case "Январь":
					result = Month.JANUARY;
					break;
				case "Февраль":
					result = Month.FEBRUARY;
					break;
				case "Март":
					result = Month.MARCH;
					break;
				case "Апрель":
					result = Month.APRIL;
					break;
				case "Май":
					result = Month.MAY;
					break;
				case "Июнь":
					result = Month.JUNE;
					break;
				case "Июль":
					result = Month.JULY;
					break;
				case "Август":
					result = Month.AUGUST;
					break;
				case "Сентябрь":
					result = Month.SEPTEMBER;
					break;
				case "Октябрь":
					result = Month.OCTOBER;
					break;
				case "Ноябрь":
					result = Month.NOVEMBER;
					break;
				case "Декабрь":
					result = Month.DECEMBER;
					break;
				}
				return result;
			}
		});

	}

	/**
	 * Настройка параметров спиннера
	 *
	 * @param spinner настраиваемый спиннер
	 */
	private void spinnerParamsTune(Spinner<Integer> spinner) {
		SpinnerValueFactory<Integer> countEntriesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
				1, model.getMaxValueSpinnerById(spinner.getId()), 1);
		spinner.setValueFactory(countEntriesFactory);
		spinner.setOnScroll(event -> {
			if (event.getDeltaY() > 0) {
				spinner.increment();
			} else if (event.getDeltaY() < 0) {
				spinner.decrement();
			}
		});
		spinner.getEditor().setAlignment(Pos.CENTER);
		spinner.hoverProperty().addListener((s, ov, nv) -> {
			vValueScroll = rootPane.vvalueProperty().doubleValue();
			if (nv) {
				blockScroll = true;
				return;
			}
			String text = spinner.getEditor().getText();
			if (text.isEmpty()) {
				spinner.getEditor().setText("1");
			}
			blockScroll = false;
		});
		spinner.focusedProperty().addListener((s, ov, nv) -> {
			if (nv) {
				vValueScroll = rootPane.vvalueProperty().doubleValue();
				blockScroll = true;
				return;
			}
			blockScroll = false;
			commitEditorText(spinner);
		});
		rootPane.vvalueProperty()
				.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
					if (blockScroll) {
						rootPane.vvalueProperty().set(vValueScroll);
					}
				});
		spinner.getEditor().textProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) {
						if (!newValue.matches("\\d*")) {
							spinner.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
						} else if (newValue.length() > 4) {
							spinner.getEditor().setText(newValue.substring(0, 4));
						}
					} else if (!oldValue.isEmpty()) {
						if (newValue.isEmpty()) {
							spinner.getEditor().setText("1");
						}
						if (!oldValue.matches("\\d*")) {
							String text = oldValue.replaceAll("[^\\d]", "");
							spinner.getEditor().setText(text.isEmpty() ? "1" : text);
						} else if (newValue.length() > 4) {
							spinner.getEditor().setText(oldValue.substring(0, 4));
						}
					}
				});
	}

	/**
	 * Коммит значения spinner`а
	 */
	private <T> void commitEditorText(Spinner<T> spinner) {
		if (!spinner.isEditable()) {
			return;
		}
		String text = spinner.getEditor().getText();
		if (!text.isEmpty()) {
			SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
			if (valueFactory != null) {
				StringConverter<T> converter = valueFactory.getConverter();
				if (converter != null) {
					T value = converter.fromString(text);
					valueFactory.setValue(value);
				}
			}
		}
	}

	/**
	 * Возвращает список чек-боксов
	 *
	 * @return список чек-боксов
	 */
	public List<CheckBox> getCheckBoxList() {
		List<CheckBox> list = new ArrayList<>();
		list.add(everyMonthCheckBox);
		list.add(everyWeekCheckBox);
		list.add(intMinuteCheckBox);
		list.add(intHoursCheckBox);
		list.add(intDayCheckBox);
		list.add(intMonthCheckBox);
		list.add(intWeekCheckBox);
		list.add(intYearCheckBox);
		return list;
	}

	/**
	 * Возвращает список спиннеров
	 *
	 * @return список спиннеров
	 */
	public List<Spinner<Integer>> getSpinners() {
		List<Spinner<Integer>> list = new ArrayList<>();
		list.add(everyMonthSpinner);
		list.add(everyWeekNumberSpinner);
		list.add(intMinuteSpinner);
		list.add(intHoursSpinner);
		list.add(intDaySpinner);
		list.add(intMonthSpinner);
		list.add(intWeekSpinner);
		list.add(intYearSpinner);
		return list;
	}

	/**
	 * Возвращает список полей ввода времени
	 *
	 * @return список полей ввода времени
	 */
	public List<LocalTimeTextField> getTimeFields() {
		List<LocalTimeTextField> list = new ArrayList<>();
		list.add(everyMonthTimeField);
		list.add(everyWeekTimeField);
		list.add(intDayTimeField);
		list.add(intMonthTimeField);
		list.add(intWeekTimeField);
		list.add(intYearTimeField);
		return list;
	}

	/**
	 * Установка режима редактирования
	 *
	 * @param aEditMode {@code true} - если форму можно редактировать
	 */
	public void setEditMode(Boolean aEditMode) {
		model.setEditModePropertyValue(aEditMode);
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
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected CycleTaskViewModel createPresentaionModel() {
		return new CycleTaskViewModel();
	}

}
