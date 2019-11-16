package ru.devazz.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.util.converter.LocalTimeStringConverter;
import ru.devazz.entities.Task;
import ru.devazz.utils.CycleEnums;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import

/**
 * Модель представления управления цикличным назначением задач
 */
public class CycleTaskViewModel extends PresentationModel<ICommonService, IEntity> {

	/** Конвертер времени назначения */
	private static LocalTimeStringConverter CONVERTER = new LocalTimeStringConverter(
			DateTimeFormatter.ofPattern("HH:mm"), null);

	/** Задача */
	private Task task;

	/** Свойство доступности редактирования представления */
	private BooleanProperty editModeProperty;

	/** Карта свойств элементов управления */
	private Map<CycleEnums.NodePropertyType, Property<?>> propertyMap;

	/** Выбранный интервал */
	private Integer interval;

	/** Номер недели в месяце */
	private Integer weekOfMonthNumber;

	/** Выбранное время */
	private LocalTime localTime;

	/** Выбранный день недели */
	private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

	/** Выбранный месяц назначения */
	private Month month = null;

	/** Карта выбранных типов и значения циклического времени */
	private Map<CycleTypeTask, String> selectedTypes;

	@Override
	protected void initModel() {
		propertyMap = new HashMap<>();
		selectedTypes = new HashMap<>();

		editModeProperty = new SimpleBooleanProperty(this, "editModeProperty", false);

		editModeProperty.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			boolean disabled = !newValue;
			boolean selected = false;
			if (null != task) {
				for (Entry<CycleEnums.NodePropertyType, Property<Boolean>> entry : getMapProperty(
						CycleEnums.PropertyType.DISABLED, Boolean.class).entrySet()) {
					if (CycleEnums.CheckBoxDisabledType.class.isInstance(entry.getKey())) {
						entry.getValue().setValue(disabled);
					} else {
						selected = false;
						if (null != task.getCycleType()) {
							for (CycleEnums.CheckBoxValueType selectedBoxes : getSelectedCheckBoxTypeList()) {
								for (String value : selectedBoxes.getControlNodes()) {
									if (value.equals(entry.getKey().getNameElement())) {
										selected = true;
										break;
									}
								}
								if (selected) {
									entry.getValue().setValue(disabled);
								}
							}

						}
					}
				}
			}
		});
	}

	/**
	 * Проверка выделенного чек-бокса на валидность использования с остальными
	 *
	 * @param aType тип проверяемого чек-бокса
	 * @return {@code true} - в случае, если выбранный чек-бокс подходит
	 */
	private boolean checkSelectedCheckBox(CheckBoxValueType aType) {
		boolean result = false;
		List<CheckBoxValueType> list = getSelectedCheckBoxTypeList();
		if (null != aType) {
			switch (aType) {
			case CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY:
				result = !(list.contains(CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_DAY_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_HOURS_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_MINUTE_VALUE_PROPERTY));
				break;
			case CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY:
				result = !(list.contains(CheckBoxValueType.CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_DAY_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_HOURS_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_INT_MINUTE_VALUE_PROPERTY));
				break;
			case CHECK_BOX_INT_DAY_VALUE_PROPERTY:
			case CHECK_BOX_INT_HOURS_VALUE_PROPERTY:
			case CHECK_BOX_INT_MINUTE_VALUE_PROPERTY:
				result = !(list.contains(CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY)
						|| list.contains(CheckBoxValueType.CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY));
				break;
			case CHECK_BOX_INT_MONTH_VALUE_PROPERTY:
			case CHECK_BOX_INT_WEEK_VALUE_PROPERTY:
			case CHECK_BOX_INT_YEAR_VALUE_PROPERTY:
				result = !(list.contains(CycleEnums.CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY)
						&& list.contains(CycleEnums.CheckBoxValueType.CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY));
				break;
			}
		}
		return result;
	}

	/**
	 * Возвращает список типов выделенных чек-боксов
	 *
	 * @return список типов выделенных чек-боксов
	 */
	private List<CycleEnums.CheckBoxValueType> getSelectedCheckBoxTypeList() {
		List<CycleEnums.CheckBoxValueType> listType = new ArrayList<>();
		for (Entry<CycleEnums.CheckBoxValueType, Property<Boolean>> entry : getMapProperty(
				CycleEnums.CheckBoxValueType.class, Boolean.class).entrySet()) {
			if (entry.getValue().getValue()) {
				listType.add(entry.getKey());
			}
		}
		return listType;
	}

	/**
	 * Добавляет слушателей взаимного блокирования элементов
	 */
	public void addDisableListeners() {
		for (Entry<CycleEnums.CheckBoxValueType, Property<Boolean>> propertyEntry : getMapProperty(
				CycleEnums.CheckBoxValueType.class, Boolean.class).entrySet()) {
			propertyEntry.getValue()
					.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
						CycleEnums.CheckBoxValueType selectedType = propertyEntry.getKey();
						String nameElement = selectedType.getNameElement();
						if (newValue && !checkSelectedCheckBox(selectedType)) {
							for (Entry<CycleEnums.NodePropertyType, Property<Boolean>> value : getMapPropertyElement(
									CycleEnums.PropertyType.CHECK_BOX_VALUE, Boolean.class).entrySet()) {
								if (!value.getKey().getNameElement().equals(nameElement)) {
									value.getValue().setValue(false);
								}
							}
						}
						setControlDisable(propertyEntry.getKey(), newValue);
					});
		}
	}

	/**
	 * Создает слушатель изменений свойств для контролов представления
	 *
	 * @param aListenerType тип слушателя
	 * @param aType тип контрола
	 * @return слушатель изменений свойств
	 */
	private <T> ChangeListener<T> createChangeListener(Class<T> aListenerType,
			CycleEnums.NodePropertyType aType) {
		return (observable, oldValue, newValue) -> {
			CycleEnums.CheckBoxValueType parentType = CycleEnums.getInstance()
					.getParentCheckBox(aType.getNameElement());
			String cycleTime = "";
			CycleTypeTask cycleTypeTask = null;
			boolean isCheckBox = (null != parentType);
			parentType = isCheckBox ? parentType : (CheckBoxValueType) aType;
			if (null != parentType) {
				switch (parentType) {
				case CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.DAY_OF_MONTH;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != interval) && (null != localTime)) {
						cycleTime = cycleTypeTask.name()
								+ ((null != month) ? " " + month.name() : "") + " "
								+ CONVERTER.toString(localTime) + " " + interval;
					}
					break;
				case CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.DAY_OF_WEEK;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != dayOfWeek) && (null != localTime)) {
						cycleTime = cycleTypeTask.name() + " " + dayOfWeek.getName() + " "
								+ CONVERTER.toString(localTime)
								+ ((null != weekOfMonthNumber) ? " " + weekOfMonthNumber : "");
					}
					break;
				case CHECK_BOX_INT_DAY_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_DAY;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != interval) && (null != localTime)) {
						cycleTime = cycleTypeTask.name() + " " + interval + " "
								+ CONVERTER.toString(localTime);
					}
					break;
				case CHECK_BOX_INT_HOURS_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_HOURS;
					setCycleTime(aType, aType.getElementType(), newValue);
					if (null != interval) {
						cycleTime = cycleTypeTask.name() + " " + String.valueOf(interval);
					}
					break;
				case CHECK_BOX_INT_MINUTE_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_MINUTE;
					setCycleTime(aType, aType.getElementType(), newValue);
					if (null != interval) {
						cycleTime = cycleTypeTask.name() + " " + String.valueOf(interval);
					}
					break;
				case CHECK_BOX_INT_MONTH_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_MONTH;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != interval) && (null != localTime)) {
						cycleTime = cycleTypeTask.name() + " " + interval + " "
								+ CONVERTER.toString(localTime);
					}
					break;
				case CHECK_BOX_INT_WEEK_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_WEEK;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != interval) && (null != localTime)) {
						cycleTime = cycleTypeTask.name() + " " + interval + " "
								+ CONVERTER.toString(localTime);
					}
					break;
				case CHECK_BOX_INT_YEAR_VALUE_PROPERTY:
					cycleTypeTask = CycleTypeTask.INT_YEAR;
					setCycleTime(aType, aType.getElementType(), newValue);
					if ((null != interval) && (null != localTime)) {
						cycleTime = cycleTypeTask.name() + " " + interval + " "
								+ CONVERTER.toString(localTime);
					}
					break;
				}
			}
			if (getProperty(parentType, Boolean.class).getValue()) {
				selectedTypes.put(cycleTypeTask, cycleTime);
			} else {
				selectedTypes.remove(cycleTypeTask);
			}

		};
	}

	/**
	 * Сохраняет параметры цикличности в задаче. Метод необходимо вызывать при
	 * сохранении задачи
	 */
	public void saveCyclicity() {
		if (!selectedTypes.isEmpty()) {
			if (1 < selectedTypes.size()) {
				String resultCycleType = "";
				for (Entry<CycleTypeTask, String> entry : selectedTypes.entrySet()) {
					resultCycleType += entry.getValue() + ";";
				}
				task.setCycleTime(resultCycleType);
				task.setCycleType(CycleTypeTask.COMPOSITE_INTERVAL);
			} else {
				Iterator<Entry<CycleTypeTask, String>> it = selectedTypes.entrySet().iterator();
				if (it.hasNext()) {
					Entry<CycleTypeTask, String> entry = it.next();
					task.setCycleType(entry.getKey());
					task.setCycleTime(entry.getValue());
				}

			}
		}
	}

	/**
	 * Запись выбранного значения циклического времени
	 *
	 * @param aType тип данных
	 * @param aValue значение
	 */
	private void setCycleTime(NodePropertyType aNodeType, PropertyType aType, Object aValue) {
		if (null != aType) {
			switch (aType) {
			case COMBO_VALUE:
				if (ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY.equals(aNodeType)) {
					setDayOfWeek(DayOfWeek.class.cast(aValue));
				} else {
					setMonth(Month.class.cast(aValue));
				}
				break;
			case SPINNER_VALUE:
				if (SpinnerValueType.SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY.equals(aNodeType)) {
					setWeekOfMonthNumber((Integer) aValue);
				} else {
					setInterval((Integer) aValue);
				}
				break;
			case TIME_FIELD_VALUE:
				setLocalTime((LocalTime) aValue);
				break;
			case CHECK_BOX_VALUE:
				CheckBoxValueType type = (CheckBoxValueType) aNodeType;
				NodePropertyType controlType = null;
				boolean stop = false;
				for (String name : type.getControlNodes()) {
					while (!stop) {
						for (PropertyType propertyType : PropertyType.getValueTypes()) {
							controlType = CycleEnums.getInstance().getEnumElement(propertyType,
									name);
							if (null != controlType) {
								stop = true;
								break;
							}
						}
					}
					stop = false;
					if (SpinnerValueType.class.isInstance(controlType)) {
						if (SpinnerValueType.SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY
								.equals(controlType)) {
							setWeekOfMonthNumber((Integer) propertyMap.get(controlType).getValue());
						} else {
							setInterval((Integer) propertyMap.get(controlType).getValue());
						}
					} else if (TimeFieldValueType.class.isInstance(controlType)) {
						setLocalTime((LocalTime) propertyMap.get(controlType).getValue());
					} else if (ComboBoxValueType.class.isInstance(controlType)) {
						if (ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY
								.equals(controlType)) {
							setDayOfWeek((DayOfWeek) propertyMap.get(controlType).getValue());
						} else {
							setMonth((Month) propertyMap.get(controlType).getValue());
						}
					}
				}
			default:
				break;
			}
		}
	}

	/**
	 * Добавляет слушателей изменения значения контрола
	 */
	@SuppressWarnings("unchecked")
	public void addValueListeners() {
		for (Entry<NodePropertyType, Property<? extends Object>> propertyEntry : getMapProperty(
				PropertyType.VALUE).entrySet()) {
			setDefaultValue(propertyEntry.getKey());
		}
		for (Entry<NodePropertyType, Property<? extends Object>> propertyEntry : getMapProperty(
				PropertyType.VALUE).entrySet()) {
			if (ComboBoxValueType.COMBO_BOX_EVERY_MONTH_VALUE_PROPERTY
					.equals(propertyEntry.getKey())) {
				propertyEntry.getValue()
						.addListener((ChangeListener<Object>) createChangeListener(
								Object.class.cast(getMonthClass()).getClass(),
								propertyEntry.getKey()));
			} else {
				propertyEntry.getValue()
						.addListener((ChangeListener<Object>) createChangeListener(
								propertyEntry.getValue().getValue().getClass(),
								propertyEntry.getKey()));
			}

		}
	}

	private Class<Month> getMonthClass() {
		return Month.class;
	}

	/**
	 * Деактивация элементов управления
	 *
	 * @param aType тип чек-бокса
	 * @param aBoxValue значание чек-бокса
	 */
	private void setControlDisable(NodePropertyType aType, Boolean aBoxValue) {
		for (Entry<NodePropertyType, Property<Boolean>> value : getMapProperty(
				PropertyType.DISABLED, Boolean.class, PropertyType.DISABLED_CHECK_BOX).entrySet()) {
			NodePropertyType parentCheckBox = CycleEnums.getInstance()
					.getParentCheckBox(value.getKey().getNameElement());
			if (aType.equals(parentCheckBox)) {
				if (editModeProperty.get()) {
					value.getValue().setValue(!getProperty(aType, Boolean.class).getValue());
				}
			}
		}
		// Настройка доступности спиннера выбора номера недели. Он может быть
		// доступен только при использовании с интервалами по месяцам и годам
		List<CheckBoxValueType> selectedList = getSelectedCheckBoxTypeList();
		boolean disableNumberWeekSpinner = ((CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY
				.equals(aType)
				&& selectedList.contains(CheckBoxValueType.CHECK_BOX_INT_WEEK_VALUE_PROPERTY))
				|| (CheckBoxValueType.CHECK_BOX_INT_WEEK_VALUE_PROPERTY.equals(aType)
						&& selectedList
								.contains(CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY)));
		if (disableNumberWeekSpinner) {
			getProperty(SpinnerDisabledType.SPINNER_EVERY_WEEK_NUMBER_DISABLED_PROPERTY,
					Boolean.class).setValue(disableNumberWeekSpinner);
		}
		if (!aBoxValue && CheckBoxValueType.CHECK_BOX_INT_WEEK_VALUE_PROPERTY.equals(aType)) {
			boolean everyWeekEnabled = getProperty(
					CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY, Boolean.class)
							.getValue();
			getProperty(SpinnerDisabledType.SPINNER_EVERY_WEEK_NUMBER_DISABLED_PROPERTY,
					Boolean.class).setValue(!everyWeekEnabled);
		}
	}

	/**
	 * Установка значений по умолчанию
	 *
	 * @param aType тип элемента
	 */
	private void setDefaultValue(NodePropertyType aType) {
		if (aType.getPropertyType().equals(PropertyType.VALUE)) {
			if (SpinnerValueType.class.isInstance(aType)) {
				getProperty(CycleEnums.getInstance().getEnumElement(PropertyType.SPINNER_VALUE,
						aType.getNameElement()), Integer.class).setValue(1);
				if (SpinnerValueType.SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY.equals(aType)) {
					setWeekOfMonthNumber(null);
				} else {
					setInterval(1);
				}
			} else if (TimeFieldValueType.class.isInstance(aType)) {
				SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
				LocalTime time = CONVERTER.fromString(parser.format(new Date()));
				getProperty(CycleEnums.getInstance().getEnumElement(PropertyType.TIME_FIELD_VALUE,
						aType.getNameElement()), LocalTime.class).setValue(time);
				setLocalTime(time);
			} else if (ComboBoxValueType.class.isInstance(aType)) {
				if (ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY.equals(aType)) {
					getProperty(CycleEnums.getInstance().getEnumElement(PropertyType.COMBO_VALUE,
							aType.getNameElement()), DayOfWeek.class).setValue(DayOfWeek.MONDAY);
					setDayOfWeek(DayOfWeek.MONDAY);
				} else {
					getProperty(CycleEnums.getInstance().getEnumElement(PropertyType.COMBO_VALUE,
							aType.getNameElement()), Month.class).setValue(null);
					setDayOfWeek(null);
				}
			}
		}
	}

	/**
	 * Возвращаяет свойство
	 *
	 * @param aNodeType тип свойства
	 * @return свойство
	 */
	@SuppressWarnings("unchecked")
	public <T> Property<T> getProperty(NodePropertyType aNodeType, Class<T> aTypeValue) {
		return (Property<T>) propertyMap.get(aNodeType);
	}

	/**
	 * Возвращает список свойств по типу данных значения свойства
	 *
	 * @param aType тип данных значения свойства
	 * @return список свойств
	 */
	@SuppressWarnings("unchecked")
	private <N extends NodePropertyType, T> Map<N, Property<T>> getMapProperty(Class<N> aClassType,
			Class<T> aValueType) {
		Map<N, Property<T>> mapResult = new HashMap<>();
		for (Entry<NodePropertyType, Property<?>> entry : propertyMap.entrySet()) {
			if (aClassType.equals(entry.getKey().getClass())) {
				mapResult.put((N) entry.getKey(), (Property<T>) entry.getValue());
			}
		}
		return mapResult;
	}

	/**
	 * Возвращает список свойств по типу данных значения свойства
	 *
	 * @param aType тип данных значения свойства
	 * @param aValueType тип значения
	 * @param aExcludedElementType исключенные типы
	 * @return список свойств
	 *
	 */
	@SuppressWarnings("unchecked")
	private <T> Map<NodePropertyType, Property<T>> getMapProperty(PropertyType aType,
			Class<T> aValueType, PropertyType... aExcludedElementType) {
		Map<NodePropertyType, Property<T>> mapResult = new HashMap<>();
		PropertyType type = null;
		PropertyType elementType = null;
		for (Entry<NodePropertyType, Property<?>> entry : propertyMap.entrySet()) {
			type = entry.getKey().getPropertyType();
			elementType = entry.getKey().getElementType();
			boolean isExcluded = false;
			for (PropertyType excludedValue : aExcludedElementType) {
				if (excludedValue.equals(elementType)) {
					isExcluded = true;
					break;
				}
			}
			if (type.equals(aType) && !isExcluded) {
				mapResult.put(entry.getKey(), (Property<T>) entry.getValue());
			}
		}
		return mapResult;
	}

	/**
	 * Возвращает список свойств по типу данных значения свойства
	 *
	 * @param aType тип данных значения свойства
	 * @param aValueType тип значения
	 * @param aExcludedElementType исключенные типы
	 * @return список свойств
	 *
	 */
	private Map<NodePropertyType, Property<? extends Object>> getMapProperty(PropertyType aType,
			PropertyType... aExcludedElementType) {
		Map<NodePropertyType, Property<? extends Object>> mapResult = new HashMap<>();
		PropertyType type = null;
		PropertyType elementType = null;
		for (Entry<NodePropertyType, Property<? extends Object>> entry : propertyMap.entrySet()) {
			type = entry.getKey().getPropertyType();
			elementType = entry.getKey().getElementType();
			boolean isExcluded = false;
			for (PropertyType excludedValue : aExcludedElementType) {
				if (excludedValue.equals(elementType)) {
					isExcluded = true;
					break;
				}
			}
			if (type.equals(aType) && !isExcluded) {
				mapResult.put(entry.getKey(), entry.getValue());
			}
		}
		return mapResult;
	}

	/**
	 * Возвращает список свойств по типу данных значения свойства
	 *
	 * @param aType тип данных значения свойства
	 * @return список свойств
	 */
	@SuppressWarnings("unchecked")
	private <T> Map<NodePropertyType, Property<T>> getMapPropertyElement(PropertyType aType,
			Class<T> aValueType) {
		Map<NodePropertyType, Property<T>> mapResult = new HashMap<>();
		for (Entry<NodePropertyType, Property<?>> entry : propertyMap.entrySet()) {
			if (entry.getKey().getElementType().equals(aType)) {
				mapResult.put(entry.getKey(), (Property<T>) entry.getValue());
			}
		}
		return mapResult;
	}

	/**
	 * Создает свойство
	 *
	 * @param aNodeType тип свойства
	 * @param aPropertyTypeClass тип значения свойства
	 * @return созданное свойство
	 */
	@SuppressWarnings("unchecked")
	public <T> Property<T> createProperty(PropertyType aType, NodePropertyType aNodeType) {
		Property<T> property = null;
		switch (aType) {
		case CHECK_BOX_VALUE:
			property = (Property<T>) new SimpleBooleanProperty(this, aNodeType.toString(), false);
			break;
		case DISABLED:
			property = (Property<T>) new SimpleBooleanProperty(this, aNodeType.toString(), true);
			break;
		case SPINNER_VALUE:
			property = (Property<T>) new SimpleIntegerProperty(this, aNodeType.toString(), 0);
			break;
		case COMBO_VALUE:
			if (ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY.equals(aNodeType)) {
				property = (Property<T>) new SimpleObjectProperty<>(this, aNodeType.toString(),
						DayOfWeek.MONDAY);
			} else {
				property = (Property<T>) new SimpleObjectProperty<>(this, aNodeType.toString(),
						null);
			}
			break;
		case TIME_FIELD_VALUE:
			SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
			property = (Property<T>) new SimpleObjectProperty<>(this, aNodeType.toString(),
					CONVERTER.fromString(parser.format(new Date())));
			break;
		default:
			break;
		}
		propertyMap.put(aNodeType, property);
		return property;

	}

	/**
	 * Возвращает {@link#editModeProperty}
	 *
	 * @return the {@link#editModeProperty}
	 */
	public BooleanProperty getEditModeProperty() {
		return editModeProperty;
	}

	/**
	 * Устанавливает значение полю {@link#editModeProperty}
	 *
	 * @param editModeValue значение поля
	 */
	public void setEditModePropertyValue(Boolean editModeValue) {
		this.editModeProperty.set(editModeValue);
	}

	/**
	 * Возвращает {@link#task}
	 *
	 * @return the {@link#task}
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Устанавливает значение полю {@link#task}
	 *
	 * @param task значение поля
	 */
	@SuppressWarnings("unchecked")
	public void setTask(Task task) {
		this.task = task;
		if (null != task.getCycleType()) {
			Property<Boolean> property = (Property<Boolean>) propertyMap
					.get(CycleEnums.getInstance().getNodeTypeByCycleType(task.getCycleType()));
			if (null != property) {
				property.setValue(true);
			}

			Map<NodePropertyType, ?> map = parseCycleTime(task.getCycleTime(), task.getCycleType());
			Property<Object> propertyObject = null;
			for (Entry<NodePropertyType, ?> entry : map.entrySet()) {
				propertyObject = (Property<Object>) propertyMap.get(entry.getKey());
				propertyObject.setValue(entry.getValue());

			}
		}
	}

	/**
	 * Разбирает строку с временем циклического назначения
	 *
	 * @param aCycleTime время циклического назначения
	 * @param aType тип циклического назначения
	 * @return карта результатов, где ключ - тип элемента управления, а значение -
	 *         значение этого котрола
	 */
	private Map<NodePropertyType, ?> parseCycleTime(String aCycleTime, CycleTypeTask aType) {
		Map<NodePropertyType, Object> mapResult = new HashMap<>();
		String delimeter = " ";
		if (null != aCycleTime) {
			String[] cycleArr = aCycleTime.split(delimeter);
			if (0 != cycleArr.length) {
				switch (aType) {
				case DAY_OF_MONTH:
					if (4 == cycleArr.length) {
						Month monthValue = Month.valueOf(cycleArr[1]);
						mapResult.put(ComboBoxValueType.COMBO_BOX_EVERY_MONTH_VALUE_PROPERTY,
								monthValue);
						mapResult.put(TimeFieldValueType.TIME_FIELD_EVERY_MONTH_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						mapResult.put(SpinnerValueType.SPINNER_EVERY_MONTH_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[3]));

					} else {
						mapResult.put(TimeFieldValueType.TIME_FIELD_EVERY_MONTH_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[1]));
						mapResult.put(SpinnerValueType.SPINNER_EVERY_MONTH_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[2]));

					}
					break;
				case DAY_OF_WEEK:
					mapResult.put(ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY,
							DayOfWeek.getDayByName(cycleArr[1]));
					mapResult.put(TimeFieldValueType.TIME_FIELD_EVERY_WEEK_VALUE_PROPERTY,
							CONVERTER.fromString(cycleArr[2]));
					if (4 == cycleArr.length) {
						mapResult.put(SpinnerValueType.SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY,
								Integer.valueOf((cycleArr[3])));
					}
					break;
				case INT_DAY:
					mapResult.put(SpinnerValueType.SPINNER_INT_DAY_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					mapResult.put(TimeFieldValueType.TIME_FIELD_INT_DAY_VALUE_PROPERTY,
							CONVERTER.fromString(cycleArr[2]));
					break;
				case INT_HOURS:
					mapResult.put(SpinnerValueType.SPINNER_INT_HOUR_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					break;
				case INT_MINUTE:
					mapResult.put(SpinnerValueType.SPINNER_INT_MINUTE_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					break;
				case INT_MONTH:
					mapResult.put(SpinnerValueType.SPINNER_INT_MONTH_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					mapResult.put(TimeFieldValueType.TIME_FIELD_INT_MONTH_VALUE_PROPERTY,
							CONVERTER.fromString(cycleArr[2]));
					break;
				case INT_WEEK:
					mapResult.put(SpinnerValueType.SPINNER_INT_WEEK_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					mapResult.put(TimeFieldValueType.TIME_FIELD_INT_WEEK_VALUE_PROPERTY,
							CONVERTER.fromString(cycleArr[2]));
					break;
				case INT_YEAR:
					mapResult.put(SpinnerValueType.SPINNER_INT_YEAR_VALUE_PROPERTY,
							Integer.parseInt(cycleArr[1]));
					mapResult.put(TimeFieldValueType.TIME_FIELD_INT_YEAR_VALUE_PROPERTY,
							CONVERTER.fromString(cycleArr[2]));
					break;
				case COMPOSITE_INTERVAL:
					parseCompositeInterval(aCycleTime.split(";"), mapResult);
					break;
				default:
					break;
				}
			}
		}
		return mapResult;
	}

	/**
	 * Разбирает массив строк циклического времени для настраеваемого типа
	 * циклического назначения
	 *
	 * @param aCycleArr массив строк циклического времени
	 * @param aMap результатов, где ключ - тип элемента управления, а значение -
	 *            значение этого котрола
	 */
	private void parseCompositeInterval(String[] aCycleArr, Map<NodePropertyType, Object> aMap) {
		for (String value : aCycleArr) {
			CycleTypeTask type = null;
			for (String cycleTime : value.split(" ")) {
				try {
					type = CycleTypeTask.valueOf(cycleTime);
				} catch (Exception e) {
					// Игнорим исключение
					if (null != type) {
						break;
					}
				} finally {
					// Игнорим исключение
					if (null != type) {
						break;
					}
				}
			}
			if (null != type) {
				String[] cycleArr = value.split(" ");
				if (0 != cycleArr.length) {
					switch (type) {
					case DAY_OF_MONTH:
						aMap.put(CheckBoxValueType.CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY, true);
						if (4 == cycleArr.length) {
							Month monthValue = Month.valueOf(cycleArr[1]);
							aMap.put(ComboBoxValueType.COMBO_BOX_EVERY_MONTH_VALUE_PROPERTY,
									monthValue);
							aMap.put(TimeFieldValueType.TIME_FIELD_EVERY_MONTH_VALUE_PROPERTY,
									CONVERTER.fromString(cycleArr[2]));
							aMap.put(SpinnerValueType.SPINNER_EVERY_MONTH_VALUE_PROPERTY,
									Integer.parseInt(cycleArr[3]));

						} else {
							aMap.put(TimeFieldValueType.TIME_FIELD_EVERY_MONTH_VALUE_PROPERTY,
									CONVERTER.fromString(cycleArr[1]));
							aMap.put(SpinnerValueType.SPINNER_EVERY_MONTH_VALUE_PROPERTY,
									Integer.parseInt(cycleArr[2]));
						}
						break;
					case DAY_OF_WEEK:
						aMap.put(CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY, true);
						aMap.put(ComboBoxValueType.COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY,
								DayOfWeek.getDayByName(cycleArr[1]));
						aMap.put(TimeFieldValueType.TIME_FIELD_EVERY_WEEK_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						aMap.put(SpinnerValueType.SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[3]));
						break;
					case INT_DAY:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_DAY_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_DAY_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						aMap.put(TimeFieldValueType.TIME_FIELD_INT_DAY_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						break;
					case INT_HOURS:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_HOURS_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_HOUR_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						break;
					case INT_MINUTE:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_MINUTE_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_MINUTE_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						break;
					case INT_MONTH:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_MONTH_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_MONTH_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						aMap.put(TimeFieldValueType.TIME_FIELD_INT_MONTH_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						break;
					case INT_WEEK:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_WEEK_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_WEEK_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						aMap.put(TimeFieldValueType.TIME_FIELD_INT_WEEK_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						break;
					case INT_YEAR:
						aMap.put(CheckBoxValueType.CHECK_BOX_INT_YEAR_VALUE_PROPERTY, true);
						aMap.put(SpinnerValueType.SPINNER_INT_YEAR_VALUE_PROPERTY,
								Integer.parseInt(cycleArr[1]));
						aMap.put(TimeFieldValueType.TIME_FIELD_INT_YEAR_VALUE_PROPERTY,
								CONVERTER.fromString(cycleArr[2]));
						break;
					default:
						break;
					}
				}
			}
		}
	}

	/**
	 * Возвращает максимальное значение спиннера по его идентификатору
	 *
	 * @param aId идентификатор спиннера
	 * @return максимальное значение
	 */
	public int getMaxValueSpinnerById(String aId) {
		int result = 0;
		if (null != aId) {
			switch (aId) {
			case "everyWeekNumberSpinner":
				result = 5;
				break;
			case "everyMonthSpinner":
				result = 31;
				break;
			case "intMinuteSpinner":
				result = 60;
				break;
			case "intHoursSpinner":
				result = 1000;
				break;
			case "intDaySpinner":
				result = 1000;
				break;
			case "intWeekSpinner":
				result = 100;
				break;
			case "intMonthSpinner":
				result = 100;
				break;
			case "intYearSpinner":
				result = 100;
				break;
			default:
				break;
			}
		}
		return result;
	}

	/**
	 * Возвращает {@link#interval}
	 *
	 * @return the {@link#interval}
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * Устанавливает значение полю {@link#interval}
	 *
	 * @param interval значение поля
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * Возвращает {@link#weekOfMonthNumber}
	 *
	 * @return the {@link#weekOfMonthNumber}
	 */
	public Integer getWeekOfMonthNumber() {
		return weekOfMonthNumber;
	}

	/**
	 * Устанавливает значение полю {@link#weekOfMonthNumber}
	 *
	 * @param weekOfMonthNumber значение поля
	 */
	public void setWeekOfMonthNumber(Integer weekOfMonthNumber) {
		this.weekOfMonthNumber = weekOfMonthNumber;
	}

	/**
	 * Возвращает {@link#localTime}
	 *
	 * @return the {@link#localTime}
	 */
	public LocalTime getLocalTime() {
		return localTime;
	}

	/**
	 * Устанавливает значение полю {@link#localTime}
	 *
	 * @param localTime значение поля
	 */
	public void setLocalTime(LocalTime localTime) {
		this.localTime = localTime;
	}

	/**
	 * Возвращает {@link#dayOfWeek}
	 *
	 * @return the {@link#dayOfWeek}
	 */
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * Устанавливает значение полю {@link#dayOfWeek}
	 *
	 * @param dayOfWeek значение поля
	 */
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * Возвращает {@link#month}
	 *
	 * @return the {@link#month}
	 */
	public Month getMonth() {
		return month;
	}

	/**
	 * Устанавливает значение полю {@link#month}
	 *
	 * @param month значение поля
	 */
	public void setMonth(Month month) {
		this.month = month;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
