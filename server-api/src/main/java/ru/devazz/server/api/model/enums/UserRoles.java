package ru.devazz.server.api.model.enums;

/**
 * Пречисление пользовательских ролей
 */
public enum UserRoles {

	/** Начальник */
	SUB_EL_NAME("SubElName", 1);

	/** Наименование роли */
	private String name;

	/** Должностной индекс */
	private int positionIndex;

	/**
	 * Конструктор
	 *
	 * @param name
	 */
	private UserRoles(String name, int aPositionIndex) {
		this.name = name;
		this.positionIndex = aPositionIndex;
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
	 * Возвращает {@link#positionIndex}
	 *
	 * @return the {@link#positionIndex}
	 */
	public int getPositionIndex() {
		return positionIndex;
	}

	/**
	 * Возвращает тип по его наименованию
	 *
	 * @param aName наименование типа
	 * @return тип
	 */
	public static UserRoles getUserRoleByName(String aName) {
		UserRoles result = null;
		UserRoles[] arr = UserRoles.values();
		for (UserRoles userRole : arr) {
			if (userRole.name.equals(aName)) {
				result = userRole;
				break;
			}
		}
		return result;
	}

}
