package ru.devazz.service.impl;

import ru.devazz.entity.ReportEntity;
import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.entity.TaskEntity;
import ru.devazz.service.IReportService;
import ru.devazz.service.ISubordinationElementService;
import ru.devazz.service.ITaskHistoryService;
import ru.devazz.service.ITaskService;
import ru.devazz.utils.Filter;
import ru.devazz.utils.FilterType;
import ru.devazz.utils.TaskHistoryType;
import ru.devazz.utils.TaskTimeInterval;

import java.util.*;

/**
 * Реализация сервиса взаимодействия с отчетами
 */
public class ReportServiceBean implements IReportService {

	/** Сервис работы с должностями */
	private ISubordinationElementService subElService;

	/** Сервис задач */
	private ITaskService taskService;

	/** Сервис истории задач */
	private ITaskHistoryService taskHistoryService;

	@Override
	public ReportEntity createReportEntity(Long aPositionSuid, Date aStartDate, Date aEndDate)
			throws Exception {
		ReportEntity report = null;
		SubordinationElementEntity subElEntity = subElService.get(aPositionSuid);
		if (null != subElEntity) {
			List<TaskEntity> tasksLists = taskService.getAllUserTasksExecutorWithFilter(
					subElEntity.getSuid(), getTasksFilter(aStartDate));
			Long successDoneTasks = 0L;
			Long closedTasks = 0L;
			Long overdueDoneTasks = 0L;
			Long overdueTasks = 0L;
			Long failedTasks = 0L;
			long inWorksTasks = 0L;
			Long reworkTasks = 0L;

			for (TaskEntity entity : tasksLists) {
				overdueDoneTasks += getTaskOverdueDoneAmount(entity, aStartDate, aEndDate);
				successDoneTasks += getTaskSuccessDoneAmount(entity, aStartDate, aEndDate);
				overdueTasks += getTaskOverdueAmount(entity, aStartDate, aEndDate);
				reworkTasks += getTaskReworkAmount(entity, aStartDate, aEndDate);
				closedTasks += getTaskClosedAmount(entity, aStartDate, aEndDate);
				failedTasks += getTaskFailedAmount(entity, aStartDate, aEndDate);
				inWorksTasks++;
			}

			//@formatter:off
			report = ReportEntity.builder()
					.suid((long) (Math.random() * 10000000L) + 1000000L)
					.battlePostSuid(aPositionSuid)
					.battlePostName(subElEntity.getName())
					.reworkAmount(reworkTasks)
					.overdueDoneAmount(overdueDoneTasks)
					.closedAmount(closedTasks)
					.successDoneAmount(successDoneTasks)
					.overdueDoneAmount(overdueTasks)
					.failedAmount(failedTasks)
					.inWorkAmount(inWorksTasks)
					.dateStart(aStartDate)
					.dateEnd(aEndDate).build();
			//@formatter:on
		}
		return report;
	}

	/**
	 * Возвращает фильтр по заданному промежутку времени и типу для истории задач
	 *
	 * @param aType тип исторической записи
	 * @param aStartDate дата начала
	 * @param aEndDate дата завершения
	 * @return сформированный фильтр
	 */
	private Filter getHistoryFilter(TaskHistoryType aType, Date aStartDate, Date aEndDate) {
		Filter filter = new Filter();

		List<String> historyTypeFilterList = new ArrayList<>();
		historyTypeFilterList.add(aType.getTypeSuid());

		List<String> dateFilterList = new ArrayList<>();
		dateFilterList.add(TaskTimeInterval.CUSTOM_TIME_INTERVAL.getMenuSuid());

		Map<FilterType, List<String>> mapFilter = new HashMap<>();
		mapFilter.put(FilterType.FILTER_BY_HISTORY_TYPE, historyTypeFilterList);
		mapFilter.put(FilterType.FILTER_BY_DATE, dateFilterList);

		filter.setStartDate(aStartDate);
		filter.setEndDate(aEndDate);
		filter.setFilterTypeMap(mapFilter);
		return filter;
	}

	/**
	 * Возвращает фильтр по заданному промежутку времени для задач
	 *
	 * @param aStartDate дата начала временного интервала
	 * @return сформированный фильтр
	 */
	private Filter getTasksFilter(Date aStartDate) {
		Filter filter = new Filter();

		List<String> dateFilterList = new ArrayList<>();
		dateFilterList.add(TaskTimeInterval.PARTICULAR_TIME_INTERVAL.getMenuSuid());

		Map<FilterType, List<String>> mapFilter = new HashMap<>();
		mapFilter.put(FilterType.FILTER_BY_DATE, dateFilterList);

		filter.setStartDate(aStartDate);
		filter.setFilterTypeMap(mapFilter);
		return filter;
	}

	/**
	 * Проверяет завершенную задачу на факт завершения после просрочки
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskOverdueDoneAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService
				.getTaskHistory(aTask.getSuid(),
						getHistoryFilter(TaskHistoryType.TASK_OVERDUE_DONE, aStartDate, aEndDate))
				.size();
	}

	/**
	 * Проверяет завершенную задачу на факт успешного завершения
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskSuccessDoneAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService.getTaskHistory(aTask.getSuid(),
				getHistoryFilter(TaskHistoryType.TASK_DONE, aStartDate, aEndDate)).size();
	}

	/**
	 * Проверяет завершенную задачу на факт закрытия
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskClosedAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService.getTaskHistory(aTask.getSuid(),
				getHistoryFilter(TaskHistoryType.TASK_CLOSED, aStartDate, aEndDate)).size();
	}

	/**
	 * Проверяет завершенную задачу на факт провала
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskFailedAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService.getTaskHistory(aTask.getSuid(),
				getHistoryFilter(TaskHistoryType.TASK_FAILED, aStartDate, aEndDate)).size();
	}

	/**
	 * Проверяет завершенную задачу на факт просрочки
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskOverdueAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService
				.getTaskHistory(aTask.getSuid(),
						getHistoryFilter(TaskHistoryType.TASK_OVERDUE, aStartDate, aEndDate))
				.size();
	}

	/**
	 * Проверяет завершенную задачу на факт нахождения на доработке
	 *
	 * @param aTask проверяемая задача
	 * @param aStartDate дата начала отчета
	 * @param aEndDate дата завершения отчета
	 * @return {@code true} - в случае, если задача была завершена после просрочки
	 */
	private Integer getTaskReworkAmount(TaskEntity aTask, Date aStartDate, Date aEndDate) {
		return taskHistoryService.getTaskHistory(aTask.getSuid(),
				getHistoryFilter(TaskHistoryType.TASK_REWORK, aStartDate, aEndDate)).size();
	}

}
