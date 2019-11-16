package ru.devazz.utils;

/**
 * Перечисление воинских званий
 */
public enum MilitaryRank {

	SOLDIER("Рядовой"),

	LANCE_CORPORAL("Ефрейтор"),

	LANCE_SERGEANT("Мл. Сержант"),

	SERGEANT("Сержант"),

	STAFF_SERGEANT("Ст. Сержант"),

	SERGEANT_MAJOR("Старшина"),

	WARRANT_OFFICER("Прапорщик"),

	SENIOR_WARRANT_OFFICER("Ст. Прапорщик"),

	SUBLIEUTENANT("Мл. Лейтенант"),

	LIEUTENANT("Лейненант"),

	SENIOR_LIEUTENANT("Ст. Лейтенант"),

	CAPTAIN("Капитан"),

	MAJOR("Майор"),

	LIEUTENANT_COLONEL("Подполковник"),

	COLONEL("Полковник"),

	MAJOR_GENERAL("Генерал-Майор"),

	LIEUTENANT_GENERAL("Генерал-Лейтенант"),

	COLONEL_GENERAL("Генерал-Полковник");

	/** Наименование звания */
	private String name;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private MilitaryRank(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the {@link#name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Получение сущности звания по названию
	 *
	 * @param aName - звание
	 * @return сущьность звания
	 */
	public static MilitaryRank getRankByName(String aName) {
		MilitaryRank rank = null;
		for (MilitaryRank value : MilitaryRank.values()) {
			String name = value.name;
			if (name.toLowerCase().equals(aName.toLowerCase())) {
				rank = value;
				break;
			}
		}
		return rank;
	}
}
