package ru.devazz.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import ru.devazz.server.api.model.enums.FilterType;
import ru.devazz.server.api.model.enums.SortType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Абстрактный фильтр
 */
@Getter
@Setter
public class Filter implements Serializable {

	/** Карта типов фильтрации */
	private Map<FilterType, List<String>> filterTypeMap = new HashMap<>();

	/** Тип сортировки */
	private SortType sortType;

	/** Признак наличия сортировки */
	private Boolean containsSort = false;

	/** Дата начала */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime startDate;

	/** Дата завршения */
	private LocalDateTime endDate;
}
