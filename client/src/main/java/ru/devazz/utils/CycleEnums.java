package ru.devazz.utils;

import ru.devazz.server.api.model.enums.CycleTypeTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Перечисления циклического назначения. Синглтон
 */
public class CycleEnums {

	/** Единственный экзмепляр класса */
	private static CycleEnums instance;

	/**
	 * Конструктор
	 */
	private CycleEnums() {
		super();
	}

	/**
	 * Возвращаяет единственный экземпляр класса
	 *
	 * @return экземпляр класса
	 */
	public static CycleEnums getInstance() {
		if (null == instance) {
			instance = new CycleEnums();
		}
		return instance;
	}

	public enum PropertyType {

		DISABLED,

		VALUE,

		DISABLED_CHECK_BOX,

		DISABLED_SPINNER,

		DISABLED_TIME_FIELD,

		DISABLED_COMBO,

		CHECK_BOX_VALUE,

		SPINNER_VALUE,

		TIME_FIELD_VALUE,

		COMBO_VALUE;

		/**
		 * Возвращает список типов - значений контролов
		 *
		 * @return список типов - значений контролов
		 */
		public static List<PropertyType> getValueTypes() {
			List<PropertyType> list = new ArrayList<>();
			list.add(CHECK_BOX_VALUE);
			list.add(COMBO_VALUE);
			list.add(SPINNER_VALUE);
			list.add(TIME_FIELD_VALUE);
			return list;
		}

		/**
		 * Возвращает список типов - значений доступности
		 *
		 * @return список типов - значений доступности
		 */
		public static List<PropertyType> getDisabledTypes() {
			List<PropertyType> list = new ArrayList<>();
			list.add(DISABLED_CHECK_BOX);
			list.add(DISABLED_COMBO);
			list.add(DISABLED_SPINNER);
			list.add(DISABLED_TIME_FIELD);
			return list;
		}
	}

	public interface NodePropertyType {

		public PropertyType getPropertyType();

		public PropertyType getElementType();

		public String getNameElement();

	}

	public enum CheckBoxDisabledType implements NodePropertyType {
		//	@formatter:off
		CHECK_BOX_EVERY_WEEK_DISABLED_PROPERTY("everyWeekCheckBox"),
		CHECK_BOX_EVERY_MONTH_DISABLED_PROPERTY("everyMonthCheckBox"),
		CHECK_BOX_INT_MINUTE_DISABLED_PROPERTY("intMinuteCheckBox"),
		CHECK_BOX_INT_HOURS_DISABLED_PROPERTY("intHoursCheckBox"),
		CHECK_BOX_INT_DAY_DISABLED_PROPERTY("intDayCheckBox"),
		CHECK_BOX_INT_WEEK_DISABLED_PROPERTY("intWeekCheckBox"),
		CHECK_BOX_INT_MONTH_DISABLED_PROPERTY("intMonthCheckBox"),
		CHECK_BOX_INT_YEAR_DISABLED_PROPERTY("intYearCheckBox");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private CheckBoxDisabledType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.DISABLED;
		}

		public static CheckBoxDisabledType getTypeByElementName(String aNameElement) {
			CheckBoxDisabledType type = null;
			for (CheckBoxDisabledType value : CheckBoxDisabledType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.DISABLED_CHECK_BOX;
		}
	}

	public enum CheckBoxValueType implements NodePropertyType {
		//	@formatter:off
		CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY("everyWeekCheckBox", "everyWeekComboBox", "everyWeekTimeField", "everyWeekNumberSpinner"),
		CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY("everyMonthCheckBox", "everyMonthComboBox","everyMonthTimeField", "everyMonthSpinner"),
		CHECK_BOX_INT_MINUTE_VALUE_PROPERTY("intMinuteCheckBox", "intMinuteSpinner"),
		CHECK_BOX_INT_HOURS_VALUE_PROPERTY("intHoursCheckBox", "intHoursSpinner"),
		CHECK_BOX_INT_DAY_VALUE_PROPERTY("intDayCheckBox", "intDaySpinner", "intDayTimeField"),
		CHECK_BOX_INT_WEEK_VALUE_PROPERTY("intWeekCheckBox", "intWeekSpinner", "intWeekTimeField"),
		CHECK_BOX_INT_MONTH_VALUE_PROPERTY("intMonthCheckBox", "intMonthSpinner", "intMonthTimeField"),
		CHECK_BOX_INT_YEAR_VALUE_PROPERTY("intYearCheckBox", "intYearSpinner", "intYearTimeField");
		//	@formatter:on

		private String nameElement;

		private String[] controlNodes;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private CheckBoxValueType(String name, String... aControls) {
			this.nameElement = name;
			controlNodes = aControls;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		/**
		 * Возвращает {@link#controlNodes}
		 *
		 * @return the {@link#controlNodes}
		 */
		public String[] getControlNodes() {
			return controlNodes;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.VALUE;
		}

		public static CheckBoxValueType getTypeByElementName(String aNameElement) {
			CheckBoxValueType type = null;
			for (CheckBoxValueType value : CheckBoxValueType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.CHECK_BOX_VALUE;
		}

	}

	public enum SpinnerDisabledType implements NodePropertyType {
		//	@formatter:off
		SPINNER_EVERY_WEEK_NUMBER_DISABLED_PROPERTY("everyWeekNumberSpinner"),
		SPINNER_EVERY_MONTH_DISABLED_PROPERTY("everyMonthSpinner"),
		SPINNER_INT_MINUTE_DISABLED_PROPERTY("intMinuteSpinner"),
		SPINNER_INT_HOUR_DISABLED_PROPERTY("intHoursSpinner"),
		SPINNER_INT_DAY_DISABLED_PROPERTY("intDaySpinner"),
		SPINNER_INT_WEEK_DISABLED_PROPERTY("intWeekSpinner"),
		SPINNER_INT_MONTH_DISABLED_PROPERTY("intMonthSpinner"),
		SPINNER_INT_YEAR_DISABLED_PROPERTY("intYearSpinner");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private SpinnerDisabledType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.DISABLED;
		}

		public static SpinnerDisabledType getTypeByElementName(String aNameElement) {
			SpinnerDisabledType type = null;
			for (SpinnerDisabledType value : SpinnerDisabledType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.DISABLED_SPINNER;
		}

	}

	public enum SpinnerValueType implements NodePropertyType {
		//	@formatter:off
		SPINNER_EVERY_WEEK_NUMBER_VALUE_PROPERTY("everyWeekNumberSpinner"),
		SPINNER_EVERY_MONTH_VALUE_PROPERTY("everyMonthSpinner"),
		SPINNER_INT_MINUTE_VALUE_PROPERTY("intMinuteSpinner"),
		SPINNER_INT_HOUR_VALUE_PROPERTY("intHoursSpinner"),
		SPINNER_INT_DAY_VALUE_PROPERTY("intDaySpinner"),
		SPINNER_INT_WEEK_VALUE_PROPERTY("intWeekSpinner"),
		SPINNER_INT_MONTH_VALUE_PROPERTY("intMonthSpinner"),
		SPINNER_INT_YEAR_VALUE_PROPERTY("intYearSpinner");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private SpinnerValueType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.VALUE;
		}

		public static SpinnerValueType getTypeByElementName(String aNameElement) {
			SpinnerValueType type = null;
			for (SpinnerValueType value : SpinnerValueType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.SPINNER_VALUE;
		}

	}

	public enum TimeFieldDisabledType implements NodePropertyType {
		//	@formatter:off
		TIME_FIELD_EVERY_WEEK_DISABLED_PROPERTY("everyWeekTimeField"),
		TIME_FIELD_EVERY_MONTH_DISABLED_PROPERTY("everyMonthTimeField"),
		TIME_FIELD_INT_DAY_DISABLED_PROPERTY("intDayTimeField"),
		TIME_FIELD_INT_WEEK_DISABLED_PROPERTY("intWeekTimeField"),
		TIME_FIELD_INT_MONTH_DISABLED_PROPERTY("intMonthTimeField"),
		TIME_FIELD_INT_YEAR_DISABLED_PROPERTY("intYearTimeField");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private TimeFieldDisabledType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.DISABLED;
		}

		public static TimeFieldDisabledType getTypeByElementName(String aNameElement) {
			TimeFieldDisabledType type = null;
			for (TimeFieldDisabledType value : TimeFieldDisabledType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.DISABLED_TIME_FIELD;
		}
	}

	public enum TimeFieldValueType implements NodePropertyType {
		//	@formatter:off
		TIME_FIELD_EVERY_WEEK_VALUE_PROPERTY("everyWeekTimeField"),
		TIME_FIELD_EVERY_MONTH_VALUE_PROPERTY("everyMonthTimeField"),
		TIME_FIELD_INT_DAY_VALUE_PROPERTY("intDayTimeField"),
		TIME_FIELD_INT_WEEK_VALUE_PROPERTY("intWeekTimeField"),
		TIME_FIELD_INT_MONTH_VALUE_PROPERTY("intMonthTimeField"),
		TIME_FIELD_INT_YEAR_VALUE_PROPERTY("intYearTimeField");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private TimeFieldValueType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.VALUE;
		}

		public static TimeFieldValueType getTypeByElementName(String aNameElement) {
			TimeFieldValueType type = null;
			for (TimeFieldValueType value : TimeFieldValueType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.TIME_FIELD_VALUE;
		}
	}

	public enum ComboBoxDisabledType implements NodePropertyType {
		//	@formatter:off
		COMBO_BOX_EVERY_WEEK_DISABLED_PROPERTY("everyWeekComboBox"),
		COMBO_BOX_EVERY_MONTH_DISABLED_PROPERTY("everyMonthComboBox");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private ComboBoxDisabledType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.DISABLED;
		}

		public static ComboBoxDisabledType getTypeByElementName(String aNameElement) {
			ComboBoxDisabledType type = null;
			for (ComboBoxDisabledType value : ComboBoxDisabledType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.DISABLED_COMBO;
		}
	}

	public enum ComboBoxValueType implements NodePropertyType {
		//	@formatter:off
		COMBO_BOX_EVERY_WEEK_VALUE_PROPERTY("everyWeekComboBox"),
		COMBO_BOX_EVERY_MONTH_VALUE_PROPERTY("everyMonthComboBox");
		//	@formatter:on

		private String nameElement;

		/**
		 * Конструктор
		 *
		 * @param name
		 */
		private ComboBoxValueType(String name) {
			this.nameElement = name;
		}

		/**
		 * Возвращает {@link#nameElement}
		 *
		 * @return the {@link#nameElement}
		 */
		@Override
		public String getNameElement() {
			return nameElement;
		}

		@Override
		public PropertyType getPropertyType() {
			return PropertyType.VALUE;
		}

		public static ComboBoxValueType getTypeByElementName(String aNameElement) {
			ComboBoxValueType type = null;
			for (ComboBoxValueType value : ComboBoxValueType.values()) {
				if (value.getNameElement().equals(aNameElement)) {
					type = value;
					break;
				}
			}
			return type;
		}

		@Override
		public PropertyType getElementType() {
			return PropertyType.COMBO_VALUE;
		}
	}

	/**
	 * Возвращает тип родительского чек-бокса по идентификатору контрола
	 *
	 * @param aControlId идентификатор контрола
	 * @return тип родительского чек-бокса
	 */
	public CheckBoxValueType getParentCheckBox(String aControlId) {
		CheckBoxValueType result = null;
		boolean breakFlag = false;
		for (CheckBoxValueType parent : CheckBoxValueType.values()) {
			if (breakFlag) {
				break;
			}
			for (String control : parent.getControlNodes()) {
				if (control.equals(aControlId)) {
					result = parent;
					breakFlag = true;
					break;
				}
			}

		}
		return result;
	}

	/**
	 * Возвращает экземпляр перечисления по имени элемента
	 *
	 * @param aType тип требуемого перечисления
	 * @param aElementName наименование элемента
	 * @return экземпляр перечисления по имени элемента
	 */
	@SuppressWarnings("unchecked")
	public <T extends NodePropertyType> T getEnumElement(PropertyType aType, String aElementName) {
		T result = null;
		if (null != aType) {
			switch (aType) {
			case CHECK_BOX_VALUE:
				result = (T) CheckBoxValueType.getTypeByElementName(aElementName);
				break;
			case DISABLED_CHECK_BOX:
				result = (T) CheckBoxDisabledType.getTypeByElementName(aElementName);
				break;
			case DISABLED_COMBO:
				result = (T) ComboBoxDisabledType.getTypeByElementName(aElementName);
				break;
			case DISABLED_SPINNER:
				result = (T) SpinnerDisabledType.getTypeByElementName(aElementName);
				break;
			case DISABLED_TIME_FIELD:
				result = (T) TimeFieldDisabledType.getTypeByElementName(aElementName);
				break;
			case SPINNER_VALUE:
				result = (T) SpinnerValueType.getTypeByElementName(aElementName);
				break;
			case COMBO_VALUE:
				result = (T) ComboBoxValueType.getTypeByElementName(aElementName);
				break;
			case TIME_FIELD_VALUE:
				result = (T) TimeFieldValueType.getTypeByElementName(aElementName);
				break;
			default:
				break;
			}
		}
		return result;
	}

	/**
	 * Возвращает тип чек-бокса по типу циклического назначения
	 *
	 * @param aCycleType тип циклического назначения
	 * @return тип чек-бокса
	 */
	public CheckBoxValueType getNodeTypeByCycleType(CycleTypeTask aCycleType) {
		CheckBoxValueType result = null;
		if (null != aCycleType) {
			switch (aCycleType) {
			case DAY_OF_MONTH:
				result = CheckBoxValueType.CHECK_BOX_EVERY_MONTH_VALUE_PROPERTY;
				break;
			case DAY_OF_WEEK:
				result = CheckBoxValueType.CHECK_BOX_EVERY_WEEK_VALUE_PROPERTY;
				break;
			case INT_DAY:
				result = CheckBoxValueType.CHECK_BOX_INT_DAY_VALUE_PROPERTY;
				break;
			case INT_HOURS:
				result = CheckBoxValueType.CHECK_BOX_INT_HOURS_VALUE_PROPERTY;
				break;
			case INT_MINUTE:
				result = CheckBoxValueType.CHECK_BOX_INT_MINUTE_VALUE_PROPERTY;
				break;
			case INT_MONTH:
				result = CheckBoxValueType.CHECK_BOX_INT_MONTH_VALUE_PROPERTY;
				break;
			case INT_WEEK:
				result = CheckBoxValueType.CHECK_BOX_INT_WEEK_VALUE_PROPERTY;
				break;
			case INT_YEAR:
				result = CheckBoxValueType.CHECK_BOX_INT_YEAR_VALUE_PROPERTY;
				break;
			default:
				break;
			}
		}
		return result;
	}

}
