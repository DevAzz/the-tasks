package ru.devazz.server.api.model;

import ru.devazz.server.api.model.enums.FilterType;
import ru.devazz.server.api.model.enums.SortType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Абстрактный фильтр
 */
public class Filter implements Serializable {

	/** Карта типов фильтрации */
	private Map<FilterType, List<String>> filterTypeMap = new HashMap<>();

	/** Тип сортировки */
	private SortType sortType;

	/** Признак наличия сортировки */
	private Boolean containsSort = false;

	/** Дата начала */
	private Date startDate;

	/** Дата завршения */
	private Date endDate;

	/**
	 * Возвращает {@link#filterTypeMap}
	 *
	 * @return the {@link#filterTypeMap}
	 */
	public Map<FilterType, List<String>> getFilterTypeMap() {
		return filterTypeMap;
	}

	/**
	 * Устанавливает значение полю {@link#filterTypeMap}
	 *
	 * @param filterTypeMap значение поля
	 */
	public void setFilterTypeMap(Map<FilterType, List<String>> filterTypeMap) {
		this.filterTypeMap = filterTypeMap;
	}

	/**
	 * Возвращает {@link#containsSort}
	 *
	 * @return the {@link#containsSort}
	 */
	public Boolean getContainsSort() {
		return containsSort;
	}

	/**
	 * Устанавливает значение полю {@link#containsSort}
	 *
	 * @param containsSort значение поля
	 */
	public void setContainsSort(Boolean containsSort) {
		this.containsSort = containsSort;
	}

	/**
	 * Возвращает {@link#sortType}
	 *
	 * @return the {@link#sortType}
	 */
	public SortType getSortType() {
		return sortType;
	}

	/**
	 * Устанавливает значение полю {@link#sortType}
	 *
	 * @param sortType значение поля
	 */
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	/**
	 * Возвращает {@link#startDate}
	 *
	 * @return the {@link#startDate}
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Устанавливает значение полю {@link#startDate}
	 *
	 * @param startDate значение поля
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Возвращает {@link#endDate}
	 *
	 * @return the {@link#endDate}
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Устанавливает значение полю {@link#endDate}
	 *
	 * @param endDate значение поля
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
