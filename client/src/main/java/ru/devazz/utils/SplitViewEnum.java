package ru.devazz.utils;

/**
 * Перечисление номеров сплит панелей для представлений
 */
public enum SplitViewEnum {

	/** Правая горизонтальная сплит панель */
	RIGHT_HORIZONTAL_SPLIT_PANEL(2),

	/** Левая сплит панель */
	COMMON_SPLIT_PANEL(4),

	/** Правая сплит панель */
	RIGHT_SPLIT_PANEL(3),

	/** Левая вертикальная сплит панель */
	LEFT_VERTICAL_SPLIT_PANEL(0),

	/** Левая горизонтальная сплит панель */
	LEFT_HORIZONTAL_SPLIT_PANEL(1);

	/** Позиция разделителя сплит-панели */
	double positionSplitPaneDivider;

	/** Номер сплит панели */
	int numSplitPanel;

	/**
	 * Конструктор
	 *
	 * @param aNumSplitPanel номер сплит панели
	 */
	SplitViewEnum(int aNumSplitPanel) {
		numSplitPanel = aNumSplitPanel;
	}

	public double getPositionSplitPaneDivider() {
		return positionSplitPaneDivider;
	}

	public void setPositionSplitPaneDivider(double positionSplitPaneDivider) {
		this.positionSplitPaneDivider = positionSplitPaneDivider;
	}

	/**
	 * Возвращает {@link #numSplitPanel}
	 *
	 * @return {@link #numSplitPanel}
	 */
	public int getNumSpleetPanel() {
		return numSplitPanel;
	}

}
