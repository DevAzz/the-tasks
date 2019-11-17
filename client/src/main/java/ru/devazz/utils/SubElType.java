package ru.devazz.utils;

/**
 * Перечисление элементов подчиненности
 */
public enum SubElType {

	HEAD("НАЧ. ОРГ.", 1L, -1L),

	SUB_HEAD("ЗАМ. НАЧ. ОРГ", 3L, 2L);

	/** Наименование элемента подчиненности */
	private String name;

	/** Идентификатор элемента подчиненности */
	private Long subElSuid;

	/** Идентификатор подчинения (родительского элемента) */
	private Long pricipalSuid;

	/**
	 * Конструктор
	 *
	 * @param name
	 * @param pricipalSuid
	 */
	private SubElType(String name, Long aSubElSuid, Long pricipalSuid) {
		this.name = name;
		this.subElSuid = aSubElSuid;
		this.pricipalSuid = pricipalSuid;
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
	 * Возвращает {@link#pricipalSuid}
	 *
	 * @return the {@link#pricipalSuid}
	 */
	public Long getPricipalSuid() {
		return pricipalSuid;
	}

	/**
	 * Возвращает {@link#subElSuid}
	 *
	 * @return the {@link#subElSuid}
	 */
	public Long getSubElSuid() {
		return subElSuid;
	}

	public static SubElType getSubElbySUID(Long aSuid) {
		SubElType subElType = null;
		for (SubElType value : SubElType.values()) {
			Long suid = value.subElSuid;
			if (suid.equals(aSuid)) {
				subElType = value;
				break;
			}
		}
		return subElType;
	}
}
