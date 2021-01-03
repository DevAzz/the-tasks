package ru.devazz.service.impl;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.ITaskHistoryService;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.DefaultTaskModel;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.TaskHistoryModel;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.*;
import ru.devazz.utils.Utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервис отслеживания состояния задач
 */
@Component
public class TasksInspector {

	/** Сервис задач */
	private ITaskService taskService;

	/** Сервис элементов дерева подчиненности */
	private ISubordinationElementService subelService;

	/** Сервис работы с историческими записями */
	private ITaskHistoryService historyService;

	/** Экземпляр потока проверки времени истечения задачи */
	private TimeLeftTasksRunnable timeLeftTasksRunnable;

	/** Экземпляр потока создания типовых задач */
	private DefaultTasksRunnable defaultTasksRunnable;

	/** Задачи по которым уже было выведено уведомление об истечении времени */
	private List<Long> timeLeftOverTasks = new ArrayList<>();

	private JmsTemplate broker;

	public TasksInspector(ITaskService taskService,
						  ISubordinationElementService subelService,
						  ITaskHistoryService historyService,
						  JmsTemplate broker) {
		this.taskService = taskService;
		this.subelService = subelService;
		this.historyService = historyService;
		this.broker = broker;
	}

	/**
	 * Инициализация инспектора
	 */
	@PostConstruct
	public void initialize() {
		timeLeftTasksRunnable = new TimeLeftTasksRunnable();
		Thread thread = new Thread(timeLeftTasksRunnable);
		thread.setDaemon(true);
		thread.start();
		defaultTasksRunnable = new DefaultTasksRunnable();
		Thread threadDefaultTasks = new Thread(defaultTasksRunnable);
		threadDefaultTasks.setDaemon(true);
		threadDefaultTasks.start();
	}

	/**
	 * Обработка завершения работы сервиса
	 */
	@PreDestroy
	public void terminate() {
		defaultTasksRunnable.setCloseFlag(true);
		timeLeftTasksRunnable.setCloseFlag(true);
	}

	/**
	 * Класс создающий типовые задачи
	 */
	private class DefaultTasksRunnable implements Runnable {

		private Boolean closeFlag = false;

		public void setCloseFlag(Boolean closeFlag) {
			this.closeFlag = closeFlag;
		}

		/**
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			while (true) {
				if (closeFlag) {
					break;
				}
				try {
					inspectDefaultTask();
					Thread.sleep(60000L);
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Класс обработки времени истечения задачи
	 */
	private class TimeLeftTasksRunnable implements Runnable {

		/** Флаг завершения выполнения обработки задач */
		private Boolean closeFlag = false;

		/**
		 * Устанавливает значение полю {@link#closeFlag}
		 *
		 * @param closeFlag значение поля
		 */
		public void setCloseFlag(Boolean closeFlag) {
			this.closeFlag = closeFlag;
		}

		/**
		 * @see Runnable#run()
		 */
		@Override
		public void run() {
			try {
				Thread.sleep(30000L);
				while (true) {
					List<TaskModel> taskList = taskService.getAll(null);
					for (TaskModel entity : taskList) {
						boolean nonArchiveTask = !(TaskStatus.DONE.equals(entity.getStatus()))
								&& !(TaskStatus.CLOSED.equals(entity.getStatus()))
								&& !(TaskStatus.FAILD.equals(entity.getStatus()));

						if (isTimeAfter(entity) && !(timeLeftOverTasks.contains(entity.getSuid()))
								&& !(TaskStatus.OVERDUE.equals(entity.getStatus()))
								&& nonArchiveTask) {
							timeLeftOverTasks.add(entity.getSuid());
							broker.convertAndSend(QueueNameEnum.TASKS_QUEUE, taskService
									.getEventByEntity(SystemEventType.OVERDUE, entity));
							Thread.sleep(100L);
						}

						if ((LocalDateTime.now().isAfter(entity.getEndDate()))
								&& !(TaskStatus.OVERDUE.equals(entity.getStatus()))
								&& nonArchiveTask) {
							entity.setStatus(TaskStatus.OVERDUE);
							taskService.update(entity, false);

							broker.convertAndSend(QueueNameEnum.TASKS_QUEUE, taskService
									.getEventByEntity(SystemEventType.OVERDUE, entity));

							//	@formatter:off
							TaskHistoryModel historyModel = TaskHistoryModel.builder()
									.taskSuid(entity.getSuid())
									.date(LocalDateTime.now())
									.build();
							//	@formatter:on
							historyModel.setActorSuid(entity.getAuthorSuid());
							historyModel.setHistoryType(TaskHistoryType.TASK_OVERDUE);
							SubordinationElementModel subEl = subelService
									.get(entity.getExecutorSuid());
							historyModel.setText(
									"Пользователь " + subEl.getName() + " просрочил задачу");
							historyModel.setName("Задача просрочена");
							historyService.add(historyModel, true);
							Thread.sleep(100L);
						}

						if ((LocalDateTime.now().isAfter(entity.getEndDate()))
								&& !TaskStatus.WORKING.equals(entity.getStatus())) {
							cycleRemappingTask(entity,
									!(TaskStatus.CLOSED.equals(entity.getStatus()))
											&& !(TaskStatus.FAILD.equals(entity.getStatus())));
							Thread.sleep(100L);
						}
					}
					Thread.sleep(1000L);

					if (closeFlag) {
						break;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Удаляет идентификатор из {@link#timeLeftOverTasks}
	 *
	 * @param aSuid идентификатор задачи
	 */
	public void removeTaskSuidFromTimeLeftOverTasksCollection(Long aSuid) {
		timeLeftOverTasks.remove(aSuid);
	}

	/**
	 * Переназначение цикличных задач
	 *
	 * @param aEntity сущность задачи
	 * @param aNonArchiveTask не архивная задача
	 */
	private void cycleRemappingTask(TaskModel aEntity, boolean aNonArchiveTask) {
		if ((null != aEntity) && aNonArchiveTask) {

			LocalDateTime startDate = aEntity.getStartDate();
			LocalDateTime endDate = aEntity.getEndDate();

			Calendar calendarStart = GregorianCalendar.getInstance();
			calendarStart.setTime(java.sql.Timestamp.valueOf(startDate));

			Calendar calendarEnd = GregorianCalendar.getInstance();
			calendarEnd.setTime(java.sql.Timestamp.valueOf(endDate));

			if ((null != aEntity.getCycleType()) && (null != aEntity.getCycleTime())) {

				createHistoryEntry(aEntity);

				switch (aEntity.getCycleType()) {
				case COMPOSITE_INTERVAL:
					parseCompositeInterval(aEntity.getCycleTime().split(";"), calendarStart,
							calendarEnd);
					break;
				default:
					parseUsualInterval(aEntity.getCycleType(), aEntity.getCycleTime().split(" "),
							calendarStart, calendarEnd);
					break;
				}

				if (TaskType.DEFAULT.equals(aEntity.getTaskType())) {
					DefaultTaskModel defaultTask = taskService
							.getDefaultTaskBySUID(aEntity.getSuid());
					if ((null != defaultTask) && defaultTask.getNextDay()) {
						calendarStart.add(Calendar.DAY_OF_MONTH, 1);
						calendarEnd.add(Calendar.DAY_OF_MONTH, 1);
					}
				}

				aEntity.setStartDate(new java.sql.Timestamp(calendarStart.getTime().getTime()).toLocalDateTime());
				aEntity.setEndDate(new java.sql.Timestamp(calendarEnd.getTime().getTime()).toLocalDateTime());
				aEntity.setStatus(TaskStatus.WORKING);
				taskService.update(aEntity, true);
			}
		}
	}

	private void createHistoryEntry(TaskModel aEntity) {
		//	@formatter:off
		TaskHistoryModel historyEntity = TaskHistoryModel.builder()
				.taskSuid(aEntity.getSuid())
				.date(LocalDateTime.now())
				.build();
		//	@formatter:on

		historyEntity.setActorSuid(aEntity.getAuthorSuid());
		historyEntity.setHistoryType(TaskHistoryType.TASK_REMAPPING);
		historyEntity.setText("Система переназначила задачу");
		historyEntity.setName("Задача переназначена");
		historyService.add(historyEntity, true);
	}

	/**
	 * Разбирает массив строк циклического времени для обыкновенного типа
	 * циклического назначения
	 *
	 * @param aType тип циклического назначения
	 * @param aCycleArr массив циклического времени
	 * @param aStartDate календарь старта задачи
	 * @param aEndDate календарь завершения задачи
	 */
	private void parseUsualInterval(CycleTypeTask aType, String[] aCycleArr, Calendar aStartDate,
									Calendar aEndDate) {
		Integer interval = 0;

		long offset = 0L;
		switch (aType) {
		case INT_DAY:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал дней
			interval = Integer.parseInt(aCycleArr[1]);
			aStartDate.add(Calendar.DAY_OF_MONTH, interval);

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());
				aStartDate.add(Calendar.DAY_OF_MONTH, interval);

				// Добавляем время старта задачи
				aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
				aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			}

			break;
		case DAY_OF_WEEK:
			aStartDate.setFirstDayOfWeek(Calendar.MONDAY);
			aStartDate.setMinimalDaysInFirstWeek(7);
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();
			Calendar tempDayOfWeekCalendar = GregorianCalendar.getInstance();
			tempDayOfWeekCalendar.setTime(aStartDate.getTime());

			Integer weekInterval = null;

			if (4 == aCycleArr.length) {
				weekInterval = Integer.valueOf(aCycleArr[3]);
			}

			if (null != weekInterval) {
				// Перещелкиваем неделю по заданному интервалу
				aStartDate.set(Calendar.WEEK_OF_MONTH, weekInterval);
			}

			DayOfWeek day = DayOfWeek.getDayByName(aCycleArr[1]);
			if (null != day) {
				switch (day) {
				case MONDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					break;
				case TUESDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
					break;
				case WEDNESDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
					break;
				case THURSDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
					break;
				case FRIDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
					break;
				case SATURDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
					break;
				case SUNDAY:
					aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					break;
				}

				// Если изначальная дата (день недели) больше чем назначаемая, переключаем
				// неделю в месяце
				if (tempDayOfWeekCalendar.getTimeInMillis() >= aStartDate.getTimeInMillis()) {
					// Перещелкиваем неделю
					aStartDate.set(Calendar.MONTH, tempDayOfWeekCalendar.get(Calendar.MONTH) + 1);
					if (null != weekInterval) {
						// Перещелкиваем неделю по заданному интервалу
						aStartDate.set(Calendar.WEEK_OF_MONTH, weekInterval);
						switch (day) {
						case MONDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
							break;
						case TUESDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
							break;
						case WEDNESDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
							break;
						case THURSDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
							break;
						case FRIDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
							break;
						case SATURDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
							break;
						case SUNDAY:
							aStartDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
							break;
						}
					}

				}

			}

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			break;
		case INT_WEEK:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал недель
			interval = Integer.parseInt(aCycleArr[1]);

			aStartDate.set(Calendar.WEEK_OF_MONTH,
					aStartDate.get(Calendar.WEEK_OF_MONTH) + interval);

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			break;
		case DAY_OF_MONTH:
			Integer dayOfMonth = null;
			String time = "";
			Month month = null;
			if (4 == aCycleArr.length) {
				month = Month.valueOf(aCycleArr[1]);
				time = aCycleArr[2];
				dayOfMonth = Integer.parseInt(aCycleArr[3]);
			} else {
				time = aCycleArr[1];
				dayOfMonth = Integer.parseInt(aCycleArr[2]);
			}

			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Временный календарь для переключения дат
			Calendar tempDayOfMonthCalendar = GregorianCalendar.getInstance();
			tempDayOfMonthCalendar.setTime(aStartDate.getTime());

			// Установка числа месяца
			aStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			if (null != month) {
				switch (month) {
				case JANUARY:
					aStartDate.set(Calendar.MONTH, Calendar.JANUARY);
					break;
				case FEBRUARY:
					aStartDate.set(Calendar.MONTH, Calendar.FEBRUARY);
					break;
				case MARCH:
					aStartDate.set(Calendar.MONTH, Calendar.MARCH);
					break;
				case APRIL:
					aStartDate.set(Calendar.MONTH, Calendar.APRIL);
					break;
				case MAY:
					aStartDate.set(Calendar.MONTH, Calendar.MAY);
					break;
				case JUNE:
					aStartDate.set(Calendar.MONTH, Calendar.JUNE);
					break;
				case JULY:
					aStartDate.set(Calendar.MONTH, Calendar.JULY);
					break;
				case AUGUST:
					aStartDate.set(Calendar.MONTH, Calendar.AUGUST);
					break;
				case SEPTEMBER:
					aStartDate.set(Calendar.MONTH, Calendar.SEPTEMBER);
					break;
				case OCTOBER:
					aStartDate.set(Calendar.MONTH, Calendar.OCTOBER);
					break;
				case NOVEMBER:
					aStartDate.set(Calendar.MONTH, Calendar.NOVEMBER);
					break;
				case DECEMBER:
					aStartDate.set(Calendar.MONTH, Calendar.DECEMBER);
					break;
				}
			}

			// Добавление месяца в случае, если текущее число больше назначаемого
			if ((null == month) && (tempDayOfMonthCalendar.get(Calendar.DAY_OF_MONTH) > aStartDate
					.get(Calendar.DAY_OF_MONTH))) {
				aStartDate.add(Calendar.MONTH, 1);
			} else if ((null != month) && (tempDayOfMonthCalendar.get(Calendar.MONTH) > aStartDate
					.get(Calendar.MONTH))) {
				aStartDate.add(Calendar.YEAR, 1);
			}

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());
				// Установка числа месяца
				aStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				// Добавление месяца в случае, если текущее число больше назначаемого
				if (tempDayOfMonthCalendar.get(Calendar.DAY_OF_MONTH) > aStartDate
						.get(Calendar.DAY_OF_MONTH)) {
					aStartDate.add(Calendar.MONTH, 1);
				}

				// Добавляем время старта задачи
				aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
				aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);

			}

			break;
		case INT_MONTH:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал месяцев
			interval = Integer.parseInt(aCycleArr[1]);

			aStartDate.add(Calendar.MONTH, interval);

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());
				aStartDate.add(Calendar.MONTH, interval);

				// Добавляем время старта задачи
				aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
				aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			}
			break;
		case INT_HOURS:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал часов
			interval = Integer.parseInt(aCycleArr[1]);

			aStartDate.add(Calendar.HOUR_OF_DAY, interval);
			aEndDate.add(Calendar.HOUR_OF_DAY, interval);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());
				aStartDate.add(Calendar.HOUR_OF_DAY, interval);

				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			}

			break;
		case INT_MINUTE:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал минут
			interval = Integer.parseInt(aCycleArr[1]);

			aStartDate.add(Calendar.MINUTE, interval);
			aEndDate.add(Calendar.MINUTE, interval);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());
				aStartDate.add(Calendar.MINUTE, interval);

				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			}

			break;
		case INT_YEAR:
			// Считаем предыдущую продолжительность задачи
			offset = aEndDate.getTimeInMillis() - aStartDate.getTimeInMillis();

			// Добавляем интервал годов
			interval = Integer.parseInt(aCycleArr[1]);

			aStartDate.add(Calendar.YEAR, interval);

			// Добавляем время старта задачи
			aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
			aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));

			// Вычисляем дату завершения
			aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);

			if (new Date().getTime() > aStartDate.getTime().getTime()) {
				aStartDate.setTime(new Date());

				aStartDate.add(Calendar.YEAR, interval);
				// Добавляем время старта задачи
				aStartDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aCycleArr[2].split(":")[0]));
				aStartDate.set(Calendar.MINUTE, Integer.parseInt(aCycleArr[2].split(":")[1]));
				// Вычисляем дату завершения
				aEndDate.setTimeInMillis(aStartDate.getTimeInMillis() + offset);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * Разбирает массив строк циклического времени для настраеваемого типа
	 * циклического назначения
	 *
	 * @param aCycleArr массив строк циклического времени
	 * @param aStartDate календарь старта задачи
	 * @param aEndDate календарь завершения задачи
	 */
	private void parseCompositeInterval(String[] aCycleArr, Calendar aStartDate,
			Calendar aEndDate) {
		sortCycleArr(aCycleArr);
		for (String value : aCycleArr) {
			CycleTypeTask type = null;
			for (String cycleTime : value.split(" ")) {
				try {
					type = CycleTypeTask.valueOf(cycleTime);
				} catch (Exception e) {
					// Игнорим исключение
					if (null != type) {
						break;
					}
				} finally {
					// Игнорим исключение
					if (null != type) {
						break;
					}
				}
			}
			if (null != type) {
				String[] cycleArr = value.split(" ");
				if (0 != cycleArr.length) {
					parseUsualInterval(type, cycleArr, aStartDate, aEndDate);
				}
			}
		}
	}

	/**
	 * Сортирует массив циклического времени (перемещает строку ежемесячного или
	 * еженедельного назначения в конец)
	 *
	 * @param aCycleArr массив цикличского времени
	 */
	private void sortCycleArr(String[] aCycleArr) {
		String everyCycleTimeValue = null;
		for (int i = 0; i < aCycleArr.length; i++) {
			String value = aCycleArr[i];
			if (value.contains("DAY_OF")) {
				everyCycleTimeValue = value;
				int j = i;
				if ((j + 1) < aCycleArr.length) {
					aCycleArr[i] = aCycleArr[j + 1];
				}
			}
		}
		if (null != everyCycleTimeValue) {
			aCycleArr[aCycleArr.length - 1] = everyCycleTimeValue;
		}

	}

	/**
	 * Конвертация типовой задачи в обыкновенную
	 *
	 * @param aEntity типовая задача
	 * @return обыкновенная задача
	 */
	private TaskModel convertDefaultTask(DefaultTaskModel aEntity) {
		TaskModel result = null;
		try {
			SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

			Calendar calendarTempStartDate = Calendar.getInstance();
			calendarTempStartDate.setTime(parser.parse(aEntity.getStartTime()));

			Calendar calendarTempEndDate = Calendar.getInstance();
			calendarTempEndDate.setTime(parser.parse(aEntity.getEndTime()));

			Calendar calendarDefaultStartDate = Calendar.getInstance();
			calendarDefaultStartDate.set(Calendar.HOUR_OF_DAY,
					calendarTempStartDate.get(Calendar.HOUR_OF_DAY));
			calendarDefaultStartDate.set(Calendar.MINUTE,
					calendarTempStartDate.get(Calendar.MINUTE));

			Calendar calendarDefaultEndDate = Calendar.getInstance();
			calendarDefaultEndDate.set(Calendar.HOUR_OF_DAY,
					calendarTempEndDate.get(Calendar.HOUR_OF_DAY));
			calendarDefaultEndDate.set(Calendar.MINUTE, calendarTempEndDate.get(Calendar.MINUTE));

			if (aEntity.getNextDay()) {
				calendarDefaultStartDate.add(Calendar.DAY_OF_MONTH, 1);
				calendarDefaultEndDate.add(Calendar.DAY_OF_MONTH, 1);
			}

			if (calendarDefaultEndDate.getTimeInMillis() < calendarDefaultStartDate
					.getTimeInMillis()) {
				calendarDefaultEndDate.add(Calendar.DAY_OF_MONTH, 1);
			}

			//	@formatter:off
			result = TaskModel.builder()
					.suid(aEntity.getSuid())
					.authorSuid(aEntity.getAuthorSuid())
					.name(Utils.getInstance().toBase64(aEntity.getName()))
					.note(Utils.getInstance().toBase64(aEntity.getNote()))
					.priority(TaskPriority.EVERYDAY)
					.status(TaskStatus.WORKING)
					.taskType(TaskType.DEFAULT)
					.startDate(new java.sql.Timestamp(calendarDefaultStartDate.getTime().getTime()).toLocalDateTime())
					.endDate(new java.sql.Timestamp(calendarDefaultEndDate.getTime().getTime()).toLocalDateTime())
					.executorSuid(aEntity.getSubordinationSUID())
					.cycleType(CycleTypeTask.INT_DAY)
					.cycleTime(CycleTypeTask.INT_DAY.name() + " 1 " + aEntity.getStartTime())
					.build();
			//	@formatter:on
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Отслеживает состояние типовых задач
	 */
	private void inspectDefaultTask()  {
		for (DefaultTaskModel defaultTask : taskService.getDefaultTaskAll()) {
			TaskModel oldEntity = taskService.get(defaultTask.getSuid());
			boolean needCreateTask = (null == oldEntity);
			if (needCreateTask) {
				TaskModel entity = convertDefaultTask(defaultTask);
				taskService.add(entity, false);
			}
		}
	}

	/**
	 * Метод проверяет время отведенное на исполнениние задачи
	 *
	 * @param task задача
	 * @return {@code true} - если если текущее время не вышло за пределы 75% от
	 *         отпущенного на задачу
	 */
	private boolean isTimeAfter(TaskModel task) {
		double timeLeftOver = 0;
		double currentTime = 0;
		Date date = new Date();
		try {
			long startDate = Optional.ofNullable(task.getStartDate())
					.orElse(LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			long endDate = Optional.ofNullable(task.getEndDate())
					.orElse(LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			timeLeftOver = ((endDate - startDate) / 1000) * 0.75;
			currentTime = (date.getTime() - startDate) / 1000;
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
		if (currentTime >= timeLeftOver) {
			return true;
		} else {
			return false;
		}
	}

}
