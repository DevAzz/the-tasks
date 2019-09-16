package ru.devazz.service;

import ru.devazz.entity.ReportEntity;

import java.util.Date;

/**
 * Интерфейс работы с отчетами
 *
 */
public interface IReportService extends ICommonService {

	/**
	 * Строит сущность отчета по заданным параметрам
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aStartDate дата начала
	 * @param aEndDate дата завершения
	 * @return сущность отчета
	 * @throws Exception в случае ошибки
	 */
	ReportEntity createReportEntity(Long aPositionSuid, Date aStartDate, Date aEndDate) throws Exception;

}
