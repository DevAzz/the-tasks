package ru.devazz.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Сущность отчета
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportModel implements Serializable, IEntity {

	/** Идентификатор сущности отчета */
	private Long suid;

	/** Наименование наименование должности */
	private String postName;

	/** Идентификатор должности */
	private Long postSuid;

	/** Дата начала */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime dateStart;

	/** Дата завершения */
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime dateEnd;

	/** Пространство дат */
	private String dateSpace;

	/** Количество задач выволненых в срок */
	private Long successDoneAmount;

	/** Количество задач выполненых с опозданием */
	private Long overdueDoneAmount;

	/** Количество завершенных задач */
	private Long closedAmount;

	/** Количество просроченных задач */
	private Long overdueAmount;

	/** Количества проваленных задач */
	private Long failedAmount;

	/** Количества задач в работе */
	private Long inWorkAmount;

	/** Количество задач на доработке */
	private Long reworkAmount;

	public void setDateEnd(LocalDateTime dateEnd) {
		this.dateEnd = dateEnd;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		try {
			setDateSpace(formatter.format(dateStart) + " - " + formatter.format(dateEnd));
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	public String getName() {
		return "Отчет по: " + postName;
	}

}
