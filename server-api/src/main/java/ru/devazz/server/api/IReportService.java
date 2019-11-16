package ru.devazz.server.api;

import ru.devazz.server.api.model.ReportModel;

import java.util.Date;

/**
 * Интерфейс работы с отчетами
 *
 */
public interface IReportService extends ICommonService {

	/**
	 * Строит сущность отчета по заданным параметрам
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aStartDate дата начала
	 * @param aEndDate дата завершения
	 * @return сущность отчета
	 * @throws Exception в случае ошибки
	 */
	ReportModel createReportEntity(Long aPositionSuid, Date aStartDate, Date aEndDate) throws Exception;

}
