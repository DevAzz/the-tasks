package ru.devazz.server.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Сущность отчета
 */
@EqualsAndHashCode
@ToString
@Data
@Builder
public class ReportModel implements Serializable, IEntity {

	/** Идентификатор сущности отчета */
	private Long suid;

	/** Наименование наименование должности */
	private String postName;

	/** Идентификатор должности */
	private Long postSuid;

	/** Дата начала */
	private Date dateStart;

	/** Дата завершения */
	private Date dateEnd;

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

	public void setDateEnd(Date dateEnd) {
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
