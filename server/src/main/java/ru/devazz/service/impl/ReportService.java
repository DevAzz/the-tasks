package ru.devazz.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.devazz.server.api.IReportService;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.ITaskHistoryService;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.ReportModel;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.FilterType;
import ru.devazz.server.api.model.enums.TaskHistoryType;
import ru.devazz.server.api.model.enums.TaskTimeInterval;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация сервиса взаимодействия с отчетами
 */
@Service
@AllArgsConstructor
public class ReportService implements IReportService {

	/** Сервис работы с должностями */
	private ISubordinationElementService subElService;

	/** Сервис задач */
	private ITaskService taskService;

	/** Сервис истории задач */
	private ITaskHistoryService taskHistoryService;

	@Override
	public ReportModel createReportEntity(Long aPositionSuid, LocalDateTime aStartDate, LocalDateTime aEndDate)
			throws Exception {
		ReportModel report = null;
		SubordinationElementModel subElEntity = subElService.get(aPositionSuid);
		if (null != subElEntity) {
			List<TaskModel> tasksLists = taskService.getAllUserTasksExecutorWithFilter(
					subElEntity.getSuid(), getTasksFilter(aStartDate));
			long successDoneTasks = 0L;
			long closedTasks = 0L;
			long overdueDoneTasks = 0L;
			long overdueTasks = 0L;
			long failedTasks = 0L;
			long inWorksTasks = 0L;
			long reworkTasks = 0L;

			for (TaskModel entity : tasksLists) {
				overdueDoneTasks += getTaskOverdueDoneAmount(entity, aStartDate, aEndDate);
				successDoneTasks += getTaskSuccessDoneAmount(entity, aStartDate, aEndDate);
				overdueTasks += getTaskOverdueAmount(entity, aStartDate, aEndDate);
				reworkTasks += getTaskReworkAmount(entity, aStartDate, aEndDate);
				closedTasks += getTaskClosedAmount(entity, aStartDate, aEndDate);
				failedTasks += getTaskFailedAmount(entity, aStartDate, aEndDate);
				inWorksTasks++;
			}

			//@formatter:off
			report = ReportModel.builder()
					.suid((long) (Math.random() * 10000000L) + 1000000L)
					.postSuid(aPositionSuid)
					.postName(subElEntity.getName())
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
	private Filter getHistoryFilter(TaskHistoryType aType, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Filter getTasksFilter(LocalDateTime aStartDate) {
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
	private Integer getTaskOverdueDoneAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Integer getTaskSuccessDoneAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Integer getTaskClosedAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Integer getTaskFailedAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Integer getTaskOverdueAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
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
	private Integer getTaskReworkAmount(TaskModel aTask, LocalDateTime aStartDate, LocalDateTime aEndDate) {
		return taskHistoryService.getTaskHistory(aTask.getSuid(),
				getHistoryFilter(TaskHistoryType.TASK_REWORK, aStartDate, aEndDate)).size();
	}

}
