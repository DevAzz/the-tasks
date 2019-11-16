package ru.devazz.utils;

/**
 * Перечисление элементов подчиненности
 */
public enum SubElType {

	RCU_HEAD("НАЧ. РЦУ", 1L, -1L),

	OPO_HEAD("НАЧ. ОПО", 3L, 2L),

	BU_HEAD("ЗНЦ-НО БУ", 4L, 2L),

	OUPD_HEAD("НАЧ. ЦУПД", 6L, 1L),

	TSICS_HEAD("ТСИКС", 5L, 1L),

	DUTY_OFFICIER("ОД", 2L, 1L),

	SPOD_BU("СПОД по  БУ", 17L, 19L),

	SPOD_PVO("СПОД по ПВО и А", 7L, 19L),

	SPOD_PD("СПОД по ПД", 8L, 4L),

	POD_PD("ПОД по ПД", 9L, 9L),

	POD_FOIV("ПОД по ФОИВ", 10L, 9L),

	POD_SV_BVS("ПОД по СВ и БВС", 11L, 9L),

	PO_MTO("ПОД по МТО", 12L, 9L),

	SPOD_PPR("СПОД по ППР", 13L, 6L),

	POD_SV("ПОД по СВ", 14L, 14L),

	POD_RZ_RK("ПОД по РЗ и РЭК", 15L, 14L),

	ODSO_OFFICIER("Оф. ОДСО", 16L, 5L);

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

	/**
	 * Получение SubordinationElement Type по SUID
	 *
	 * @param aSuid SUID боевого поста
	 * @return subElementType
	 */
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
