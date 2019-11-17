package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ITaskService;
import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.TaskModel;
import ru.devazz.server.api.model.enums.*;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Модель представления задач
 */
public class TasksViewModel extends PresentationModel<ITaskService, TaskModel> {

	/** Видимые задачи */
	private ObservableList<Task> visibleTasks;

	/** Свойство количества страниц */
	private IntegerProperty pageCountProperty;

	/** Свойство видимости компонента паджинации */
	private BooleanProperty paginationVisibleProperty;

	/** Модель виджета выбора временного промежутка */
	private CustomTimeIntervalModel customTimeIntervalModel;

	/** Идентификатор должности, выбранной в дереве подчиненности */
	private Long positionSuid;

	/** Идентификатор должности текущего пользователя */
	private Long currentUserPositionSuid;

	/** Количество записей на странице */
	private Integer countPageEntries = 5;

	/** Свойство номера текущей страницы */
	private IntegerProperty currentPageProperty;

	/** Свойство активности пункта меню сортировки по приоритету "По убыванию" */
	private BooleanProperty sortByPriorityDescendingProperty;

	/** Свойство активности пункта меню сортировки по приоритету "По возрастанию" */
	private BooleanProperty sortByPriorityAscendingProperty;

	/** Свойство активности пункта меню сортировки по дате "Сначала новые" */
	private BooleanProperty sortByDateFirstNewItemProperty;

	/** Свойство активности пункта меню сортировки по дате "Сначала старые" */
	private BooleanProperty sortByDateFirstOldItemProperty;

	/** Свойство активности пункта меню настройки записей "Подстветка записей" */
	private BooleanProperty illEnableCheckProperty;

	/** Текстовое свойство цвета прогресс бара */
	private Map<String, ObjectProperty<TaskBackGroundColor>> colorTaskPropertyMap;

	/** Тип представления */
	private TasksViewType typeView;

	/** Список наименований активных фильтров */
	private ObservableList<String> nameActiveFiltersList = FXCollections
			.synchronizedObservableList(FXCollections.observableArrayList());

	/** Фильтр исторических записей */
	private Filter filter = new Filter();

	/** Класс, выполняющий вычисление и обновление прогресса по каждой задаче */
	private ProgressUpdater progressUpdater;

	/** Признак активности представления отображения задач */
	private BooleanProperty isActiveProperty;

	@Override
	protected void initModel() {
		if (null == visibleTasks) {
			visibleTasks = FXCollections
					.synchronizedObservableList(FXCollections.observableArrayList());
		}
		colorTaskPropertyMap = new HashMap<>();
		paginationVisibleProperty = new SimpleBooleanProperty(this, "paginationVisibleProperty",
				true);
		pageCountProperty = new SimpleIntegerProperty(this, "pageCountProperty", 1);
		currentPageProperty = new SimpleIntegerProperty(this, "currentPageProperty", 0);
		currentUserPositionSuid = Utils.getInstance().getCurrentUser().getPositionSuid();

		// Свойства сортировки
		sortByPriorityDescendingProperty = new SimpleBooleanProperty(this,
				"sortByPriorityDescendingProperty", false);
		sortByPriorityAscendingProperty = new SimpleBooleanProperty(this,
				"sortByPriorityAscendingProperty", false);
		sortByDateFirstNewItemProperty = new SimpleBooleanProperty(this,
				"sortByDateFirstNewItemProperty", false);
		sortByDateFirstOldItemProperty = new SimpleBooleanProperty(this,
				"sortByDateFirstOldItemProperty", false);
		isActiveProperty = new SimpleBooleanProperty(this, "isActiveProperty", false);
		illEnableCheckProperty = new SimpleBooleanProperty(this, "illumnationEnableCheckMenuItem",
				true);
		isActiveProperty.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				startComputeProgress();
			}

		});
		illEnableCheckProperty
				.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (newValue) {
						startComputeProgress();
					}

				});

		progressUpdater = new ProgressUpdater();
	}

	/**
	 * Сортировка записей
	 *
	 * @param aItemId идентификатор элемента меню
	 */
	public void sort(String aItemId) {
		switch (aItemId) {
		case "sortByPriorityDescending":
			sortByPriorityDescending();
			break;
		case "sortByPriorityAscending":
			sortByPriorityAscending();
			break;
		case "sortByDateFirstNewItem":
			sortByDateFirstNew();
			break;
		case "sortByDateFirstOldItem":
			sortByDateFirstOld();
			break;
		default:
			break;
		}
	}

	/**
	 * Сортировка по дате "Сначала старые"
	 */
	private void sortByDateFirstOld() {
		if (sortByDateFirstOldItemProperty.get()) {
			sortByDateFirstNewItemProperty.set(false);
			sortByPriorityDescendingProperty.set(false);
			sortByPriorityAscendingProperty.set(false);
			filter.setContainsSort(true);
			filter.setSortType(SortType.SORT_BY_DATE_FIRST_OLD);
			paramLoad(typeView, currentPageProperty.get());
		} else {
			filter.setContainsSort(
					sortByDateFirstNewItemProperty.get() || sortByPriorityDescendingProperty.get()
							|| sortByPriorityAscendingProperty.get());
		}
	}

	/**
	 * Сортировка по дате "Сначала новые"
	 */
	private void sortByDateFirstNew() {
		if (sortByDateFirstNewItemProperty.get()) {
			sortByDateFirstOldItemProperty.set(false);
			sortByPriorityDescendingProperty.set(false);
			sortByPriorityAscendingProperty.set(false);
			filter.setContainsSort(true);
			filter.setSortType(SortType.SORT_BY_DATE_FIRST_NEW);
			paramLoad(typeView, currentPageProperty.get());
		} else {
			filter.setContainsSort(
					sortByDateFirstOldItemProperty.get() || sortByPriorityDescendingProperty.get()
							|| sortByPriorityAscendingProperty.get());
		}
	}

	/**
	 * Сортировка по приоритету "По возрастанию"
	 */
	private void sortByPriorityAscending() {
		if (sortByPriorityAscendingProperty.get()) {
			sortByPriorityDescendingProperty.set(false);
			sortByDateFirstNewItemProperty.set(false);
			sortByDateFirstOldItemProperty.set(false);
			filter.setContainsSort(true);
			filter.setSortType(SortType.SORT_BY_PRIORITY_ASCENDING);
			paramLoad(typeView, currentPageProperty.get());
		} else {
			filter.setContainsSort(
					sortByPriorityDescendingProperty.get() || sortByDateFirstNewItemProperty.get()
							|| sortByDateFirstOldItemProperty.get());
		}
	}

	/**
	 * Сортировка по приоритету "По убыванию"
	 */
	private void sortByPriorityDescending() {
		if (sortByPriorityDescendingProperty.get()) {
			sortByPriorityAscendingProperty.set(false);
			sortByDateFirstNewItemProperty.set(false);
			sortByDateFirstOldItemProperty.set(false);
			filter.setContainsSort(true);
			filter.setSortType(SortType.SORT_BY_PRIORITY_DESCENDING);
			paramLoad(typeView, currentPageProperty.get());
		} else {
			filter.setContainsSort(
					sortByPriorityAscendingProperty.get() || sortByDateFirstNewItemProperty.get()
							|| sortByDateFirstOldItemProperty.get());
		}
	}

	/**
	 * Возвращает {@link#nameActiveFiltersList}
	 *
	 * @return the {@link#nameActiveFiltersList}
	 */
	public ObservableList<String> getNameActiveFiltersList() {
		return nameActiveFiltersList;
	}

	/**
	 * Возвращает {@link#sortByPriorityDescendingProperty}
	 *
	 * @return the {@link#sortByPriorityDescendingProperty}
	 */
	public BooleanProperty getSortByPriorityDescendingProperty() {
		return sortByPriorityDescendingProperty;
	}

	/**
	 * Возвращает {@link#sortByPriorityAscendingProperty}
	 *
	 * @return the {@link#sortByPriorityAscendingProperty}
	 */
	public BooleanProperty getSortByPriorityAscendingProperty() {
		return sortByPriorityAscendingProperty;
	}

	/**
	 * Возвращает {@link#sortByDateFirstNewItemProperty}
	 *
	 * @return the {@link#sortByDateFirstNewItemProperty}
	 */
	public BooleanProperty getSortByDateFirstNewItemProperty() {
		return sortByDateFirstNewItemProperty;
	}

	/**
	 * Возвращает {@link#sortByDateFirstOldItem}
	 *
	 * @return the {@link#sortByDateFirstOldItem}
	 */
	public BooleanProperty getSortByDateFirstOldItemProperty() {
		return sortByDateFirstOldItemProperty;
	}

	/**
	 * Выполняет загрузку всех записей
	 */
	public void showAllEntries() {
		nameActiveFiltersList.clear();
		customTimeIntervalModel.clearDateFields();
		filter.getFilterTypeMap().clear();
		paramLoad(typeView, currentPageProperty.get());
	}

	/**
	 * Обновление наименования активного фильтра
	 */
	public void updateNameFilterLabel() {
		nameActiveFiltersList.clear();
		List<String> listPriorityFilter = filter.getFilterTypeMap()
				.get(FilterType.FILTER_BY_PRIORITY);
		List<String> listDateFilter = filter.getFilterTypeMap().get(FilterType.FILTER_BY_DATE);
		List<String> listStatusFilter = filter.getFilterTypeMap().get(FilterType.FILTER_BY_STATUS);

		if ((null != listPriorityFilter) && !listPriorityFilter.isEmpty()) {
			nameActiveFiltersList.add("Фильтры по приоритету:");
			for (String value : listPriorityFilter) {
				TaskPriority taskPriority = TaskPriority.getPriorityBySuid(value);
				if (null != taskPriority) {
					nameActiveFiltersList.add(taskPriority.getName());
				}
			}
		}
		if ((null != listStatusFilter) && !listStatusFilter.isEmpty()) {
			nameActiveFiltersList.add("Фильтры по статусу:");
			for (String value : listStatusFilter) {
				TaskStatus status = TaskStatus.getStatusBySuid(value);
				if (null != status) {
					nameActiveFiltersList.add(status.getName());
				}
			}
		}
		if ((null != listDateFilter) && !listDateFilter.isEmpty()) {
			nameActiveFiltersList.add("Фильтры по дате:");
			for (String value : listDateFilter) {
				TaskTimeInterval interval = TaskTimeInterval.getTimeIntervalBySuid(value);
				if (null != interval) {
					String name = "";
					if (TaskTimeInterval.CUSTOM_TIME_INTERVAL.equals(interval)) {
						SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
						name = "От " + parser.format(filter.getStartDate()) + " до "
								+ parser.format(filter.getEndDate());
					} else {
						name = interval.getName();
					}
					nameActiveFiltersList.add(name);
				}
			}
		}
	}

	/**
	 *
	 * Связываение фильтра
	 *
	 * @param aMenuId идентификатор меню
	 * @param aSelectedItemsMap карта выделенных пунктов меню
	 */
	public void bindFilter(String aMenuId, Map<String, Boolean> aSelectedItemsMap) {
		switch (aMenuId) {
		case "dateFilterMenu":
			dateFilter(aSelectedItemsMap);
			break;
		case "priorityFilterMenu":
			priorityFilter(aSelectedItemsMap);
			break;
		case "statusFilterMenu":
			statusFilter(aSelectedItemsMap);
			break;
		default:
			break;
		}
		updateNameFilterLabel();
	}

	/**
	 * Фильтрация по дате
	 *
	 * @param aSelectedItemsMap карта выделенных пунктов меню
	 */
	private void dateFilter(Map<String, Boolean> aSelectedItemsMap) {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : aSelectedItemsMap.entrySet()) {
			switch (entry.getKey()) {
			case "dayFilterItem":
				if (entry.getValue()) {
					list.add(TaskTimeInterval.DAY.getMenuSuid());
				}
				break;
			case "weekFilterItem":
				if (entry.getValue()) {
					list.add(TaskTimeInterval.WEEK.getMenuSuid());
				}
				break;
			case "monthFilterItem":
				if (entry.getValue()) {
					list.add(TaskTimeInterval.MONTH.getMenuSuid());
				}
				break;
			case "allTimeFilterItem":
				if (entry.getValue()) {
					list.add(TaskTimeInterval.ALL_TIME.getMenuSuid());
				}
				break;
			default:
				break;
			}
		}
		filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
		paramLoad(typeView, currentPageProperty.get());
	}

	/**
	 * Фильтрация по статусу
	 *
	 * @param aSelectedItemsMap карта выделенных пунктов меню
	 */
	private void statusFilter(Map<String, Boolean> aSelectedItemsMap) {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : aSelectedItemsMap.entrySet()) {
			switch (entry.getKey()) {
			case "statusWorking":
				if (entry.getValue()) {
					list.add(TaskStatus.WORKING.getMenuSuid());
				}
				break;
			case "statusDone":
				if (entry.getValue()) {
					list.add(TaskStatus.DONE.getMenuSuid());
				}
				break;
			case "statusClosed":
				if (entry.getValue()) {
					list.add(TaskStatus.CLOSED.getMenuSuid());
				}
				break;
			case "statusFaild":
				if (entry.getValue()) {
					list.add(TaskStatus.FAILD.getMenuSuid());
				}
				break;
			case "statusOverdue":
				if (entry.getValue()) {
					list.add(TaskStatus.OVERDUE.getMenuSuid());
				}
				break;
			case "statusRework":
				if (entry.getValue()) {
					list.add(TaskStatus.REWORK.getMenuSuid());
				}
				break;
			default:
				break;
			}
		}
		filter.getFilterTypeMap().put(FilterType.FILTER_BY_STATUS, list);
		paramLoad(typeView, currentPageProperty.get());
	}

	/**
	 * Фильтрация по приоритету
	 *
	 * @param aSelectedItemsMap карта выделенных пунктов меню
	 */
	private void priorityFilter(Map<String, Boolean> aSelectedItemsMap) {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : aSelectedItemsMap.entrySet()) {
			switch (entry.getKey()) {
			case "priorityCritical":
				if (entry.getValue()) {
					list.add(TaskPriority.CRITICAL.getMenuSuid());
				}
				break;
			case "priorityMajor":
				if (entry.getValue()) {
					list.add(TaskPriority.MAJOR.getMenuSuid());
				}
				break;
			case "priorityMinor":
				if (entry.getValue()) {
					list.add(TaskPriority.MINOR.getMenuSuid());
				}
				break;
			case "priorityTrivial":
				if (entry.getValue()) {
					list.add(TaskPriority.TRIVIAL.getMenuSuid());
				}
				break;
			default:
				break;
			}
		}
		filter.getFilterTypeMap().put(FilterType.FILTER_BY_PRIORITY, list);
		paramLoad(typeView, currentPageProperty.get());
	}

	/**
	 * Удаляет задачу
	 *
	 * @param aTask удаляемая задача
	 */
	public void deleteTask(Task aTask) {
		visibleTasks.remove(aTask);
		getService().delete(aTask.getSuid(), true);
	}

	/**
	 * Возвращает {@link#visibleTasks}
	 *
	 * @return the {@link#visibleTasks}
	 */
	public ObservableList<Task> getVisibleTasks() {
		return visibleTasks;
	}

	/**
	 *
	 * Возвращает задачу по ее иднетификатору
	 *
	 * @param aSuid идентификатор задачи
	 * @return задача
	 */
	public Task getTaskBySuid(Long aSuid) {
		return getTaskBySuid(String.valueOf(aSuid));
	}

	/**
	 *
	 * Возвращает задачу по ее иднетификатору
	 *
	 * @param aSuid идентификатор задачи
	 * @return задача
	 */
	public Task getTaskBySuid(String aSuid) {
		Task result = null;
		Long suid = Long.valueOf(aSuid);
		for (Task task : visibleTasks) {
			if ((null != task) && (task.getSuid().equals(suid))) {
				result = task;
				break;
			}
		}
		return result;
	}

	@Override
	public Class<ITaskService> getTypeService() {
		return ITaskService.class;
	}

	/**
	 * Рассчитывает смещение
	 *
	 * @param aCountTasks количество задач
	 * @param aPageNumber номер страницы
	 * @return смещение по задачам
	 */
	private int calcOffset(int aCountTasks, int aPageNumber) {
		int result = aCountTasks * aPageNumber;
		if (0 == aPageNumber) {
			result = 0;
		}
		return result;
	}

	/**
	 * Загрузка записей по типу представления
	 *
	 * @param aType тип представления
	 */
	public void paramLoad(TasksViewType aType, final int aPageNumber) {
		Thread thread = new Thread(() -> {
			try {
				listDataModelEntities.clear();
				visibleTasks.clear();
				switch (aType) {
				case ARCHIVE:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							currentUserPositionSuid, TasksViewType.ARCHIVE, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case CURRENT_IN:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							positionSuid, TasksViewType.CURRENT_IN, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case CURRENT_OUT:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							positionSuid, TasksViewType.CURRENT_OUT, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case CURRENT_ARCHIVE:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							positionSuid, TasksViewType.CURRENT_ARCHIVE, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case CURRENT_EVERYDAY_IN:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							positionSuid, TasksViewType.CURRENT_EVERYDAY_IN, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case CURRENT_EVERYDAY_OUT:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							positionSuid, TasksViewType.CURRENT_EVERYDAY_OUT, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case IN:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							currentUserPositionSuid, TasksViewType.IN, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case OUT:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							currentUserPositionSuid, TasksViewType.OUT, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case EVERYDAY_IN:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							currentUserPositionSuid, TasksViewType.EVERYDAY_IN, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				case EVERYDAY_OUT:
					listDataModelEntities.addAll(getService().getTasksByViewTypeWithPagination(
							currentUserPositionSuid, TasksViewType.EVERYDAY_OUT, countPageEntries,
							calcOffset(countPageEntries, aPageNumber), filter));
					break;
				}
				for (TaskModel entity : new ArrayList<>(listDataModelEntities)) {
					try {
						Thread.sleep(70L);
					} catch (InterruptedException e) {
						// Логирование
						e.printStackTrace();
					}
					Task task = EntityConverter.getInstatnce()
							.convertTaskModelToClientWrapTask(entity);
					visibleTasks.add(task);
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Возвращает {@link#countPageEntries}
	 *
	 * @return the {@link#countPageEntries}
	 */
	public Integer getCountPageEntries() {
		return countPageEntries;
	}

	/**
	 * Устанавливает значение полю {@link#countPageEntries}
	 *
	 * @param countPageEntries значение поля
	 */
	public void setCountPageEntries(Integer countPageEntries) {
		this.countPageEntries = countPageEntries;
	}

	/**
	 * Обновление списка задач
	 *
	 * @param aEventType тип события
	 * @param aTask обновляемая задача
	 */
	public void updateTaskList(String aEventType, TaskModel aTask) {
		if (!"time_left_over".equals(aEventType)) {
			for (TaskModel entity : new ArrayList<>(listDataModelEntities)) {
				if (entity.getSuid().equals(aTask.getSuid())) {
					listDataModelEntities.remove(entity);
					for (Task task : new ArrayList<>(visibleTasks)) {
						if (task.getSuid().equals(entity.getSuid())) {
							visibleTasks.remove(task);
						}
					}
				}
			}
		}

		Task task = EntityConverter.getInstatnce().convertTaskModelToClientWrapTask(aTask);
		boolean isUsual = TaskType.USUAL.equals(task.getType());
		switch (aEventType) {
		case "created":
		case "overdue":
		case "done":
		case "rework":
		case "updated":
			if (null != positionSuid) {
				switch (typeView) {
				case CURRENT_IN:
					if (positionSuid.equals(task.getExecutor().getSuid()) && isUsual) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case CURRENT_OUT:
					if (positionSuid.equals(task.getAuthor().getSuid()) && isUsual) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case CURRENT_EVERYDAY_IN:
					if (positionSuid.equals(task.getExecutor().getSuid())
							&& TaskPriority.EVERYDAY.equals(task.getPriority())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case CURRENT_EVERYDAY_OUT:
					if (positionSuid.equals(task.getAuthor().getSuid())
							&& TaskPriority.EVERYDAY.equals(task.getPriority())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case IN:
					if (currentUserPositionSuid.equals(task.getExecutor().getSuid()) && isUsual) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case OUT:
					if (currentUserPositionSuid.equals(task.getAuthor().getSuid()) && isUsual) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case EVERYDAY_IN:
					if (currentUserPositionSuid.equals(task.getExecutor().getSuid())
							&& TaskPriority.EVERYDAY.equals(task.getPriority())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case EVERYDAY_OUT:
					if (currentUserPositionSuid.equals(task.getAuthor().getSuid())
							&& TaskPriority.EVERYDAY.equals(task.getPriority())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				default:
					break;
				}
			}

			break;
		case "deleted":
			break;
		case "closed":
		case "failed":
			if (null != positionSuid) {
				switch (typeView) {
				case ARCHIVE:
					if ((TaskStatus.CLOSED.equals(task.getStatus())
							|| TaskStatus.FAILD.equals(task.getStatus()))
							&& currentUserPositionSuid.equals(task.getExecutor().getSuid())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				case CURRENT_ARCHIVE:
					if ((TaskStatus.CLOSED.equals(task.getStatus())
							|| TaskStatus.FAILD.equals(task.getStatus()))
							&& positionSuid.equals(task.getExecutor().getSuid())) {
						listDataModelEntities.add(aTask);
						visibleTasks.add(task);
					}
					break;
				default:
					break;
				}
			}
			break;
		}
	}

	/**
	 * Возвращает {@link#positionSuid}
	 *
	 * @return the {@link#positionSuid}
	 */
	public Long getPositionSuid() {
		return positionSuid;
	}

	/**
	 * Устанавливает значение полю {@link#positionSuid}
	 *
	 * @param positionSuid значение поля
	 */
	public void setPositionSuid(Long positionSuid) {
		this.positionSuid = positionSuid;
		setPageCountPropertValue(getCountPages());
	}

	/**
	 * Возвращает {@link#currentUserPositionSuid}
	 *
	 * @return the {@link#currentUserPositionSuid}
	 */
	public Long getCurrentUserPositionSuid() {
		return currentUserPositionSuid;
	}

	/**
	 * Устанавливает значение полю {@link#currentUserPositionSuid}
	 *
	 * @param currentUserPositionSuid значение поля
	 */
	public void setCurrentUserPositionSuid(Long currentUserPositionSuid) {
		this.currentUserPositionSuid = currentUserPositionSuid;
	}

	/**
	 * Возвращает {@link#typeView}
	 *
	 * @return the {@link#typeView}
	 */
	public TasksViewType getTypeView() {
		return typeView;
	}

	/**
	 * Устанавливает значение полю {@link#typeView}
	 *
	 * @param typeView значение поля
	 */
	public void setTypeView(TasksViewType typeView) {
		this.typeView = typeView;
		setPageCountPropertValue(getCountPages());
	}

	/**
	 * Возвращает {@link#paginationVisibleProperty}
	 *
	 * @return the {@link#paginationVisibleProperty}
	 */
	public BooleanProperty getPaginationVisibleProperty() {
		return paginationVisibleProperty;
	}

	/**
	 * Устанавливает значение полю {@link#paginationVisibleProperty}
	 *
	 * @param paginationVisibleValue значение поля
	 */
	public void setPaginationVisiblePropertyValue(Boolean paginationVisibleValue) {
		this.paginationVisibleProperty.set(paginationVisibleValue);
	}

	/**
	 * Возвращает {@link#pageCountProperty}
	 *
	 * @return the {@link#pageCountProperty}
	 */
	public IntegerProperty getPageCountProperty() {
		return pageCountProperty;
	}

	/**
	 * Устанавливает значение полю {@link#pageCountProperty}
	 *
	 * @param pageCountValue значение поля
	 */
	public void setPageCountPropertValue(Integer pageCountValue) {
		this.pageCountProperty.set(pageCountValue);
	}

	/**
	 * Устанавливает значение полю {@link#customTimeIntervalModel}
	 *
	 * @param customTimeIntervalModel значение поля
	 */
	public void setCustomTimeIntervalModel(CustomTimeIntervalModel customTimeIntervalModel) {
		this.customTimeIntervalModel = customTimeIntervalModel;
	}

	/**
	 * Возвращает {@link#currentPageProperty}
	 *
	 * @return the {@link#currentPageProperty}
	 */
	public IntegerProperty getCurrentPageProperty() {
		return currentPageProperty;
	}

	/**
	 * Создает поток поиска записей по заданному промежутку времени
	 *
	 * @return поток поиска записей по заданному промежутку времени
	 */
	public Runnable createSearchRunnable() {
		Runnable runnable = () -> {
			try {
				Date startDate = customTimeIntervalModel.getStartDate();
				Date endDate = customTimeIntervalModel.getEndDate();
				if ((null != startDate) && (null != endDate)) {
					List<String> list = new ArrayList<>();
					list.add(TaskTimeInterval.CUSTOM_TIME_INTERVAL.getMenuSuid());
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
					filter.setStartDate(startDate);
					filter.setEndDate(endDate);
					paramLoad(typeView, currentPageProperty.get());
					updateNameFilterLabel();
				}
			} catch (ParseException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		};
		return runnable;

	}

	/**
	 * Возвращает количество записей в таблице задач
	 *
	 * @return колчиество страниц
	 */
	public int getCountPages() {
		int result = 0;
		Long countTasks = 0L;
		if (null != typeView) {
			switch (typeView) {
			case ARCHIVE:
			case IN:
			case OUT:
			case EVERYDAY_IN:
			case EVERYDAY_OUT:
				countTasks = service.getCountTasks(typeView, currentUserPositionSuid, filter);
				break;
			case CURRENT_IN:
			case CURRENT_OUT:
			case CURRENT_EVERYDAY_IN:
			case CURRENT_EVERYDAY_OUT:
			case CURRENT_ARCHIVE:
				countTasks = service.getCountTasks(typeView, positionSuid, filter);
				break;
			}

			result = (int) Math.ceil(countTasks.doubleValue() / countPageEntries);
			if (0 == result) {
				result = 1;
			}
		}
		return result;
	}

	/**
	 * Возвращает номер страницы задачи
	 *
	 * @param aTask задача
	 * @return номер страницы задач
	 */
	public Integer getNumberPageByTask(Task aTask) {
		Integer result = 0;
		if (null != typeView) {
			switch (typeView) {
			case ARCHIVE:
			case IN:
			case OUT:
			case EVERYDAY_IN:
			case EVERYDAY_OUT:
				result = service.getPageNumberByTask(currentUserPositionSuid, aTask.getSuid(),
						countPageEntries, typeView, filter);
				break;
			case CURRENT_IN:
			case CURRENT_OUT:
			case CURRENT_EVERYDAY_IN:
			case CURRENT_EVERYDAY_OUT:
			case CURRENT_ARCHIVE:
				result = service.getPageNumberByTask(positionSuid, aTask.getSuid(),
						countPageEntries, typeView, filter);
				break;
			}
		}
		if (result < 0) {
			result = 0;
		}
		return result;
	}

	/**
	 * Возвращает признак того, что задача была удалена из базы данных
	 *
	 * @param aTask задача
	 * @return {@code true} - если задача была удалена
	 */
	public Boolean isTaskRemoved(Task aTask) {
		Boolean result = false;
		TaskModel entity = service.get(aTask.getSuid());
		if (null == entity) {
			result = true;
		}
		return result;
	}

	/**
	 * Перечисление цветов индикатора прогресса
	 */
	public enum TaskBackGroundColor {

		RED, GREEN, YELLOW, DOESNT_START, DEFAULT;

	}

	/**
	 * Класс, который обновляет прогресс бар в зависимости от процента оставшегося
	 * времени
	 */
	private class ProgressUpdater {

		/**
		 * Рассчитывает процент времени выполнения и обновляет прогресс бар
		 */
		private void startCopmuteProgress() {
			if (isActiveProperty.get()) {
				Thread thread = new Thread(() -> {
					boolean exit = false;
					while (!exit) {
						try {
							List<Task> list = new ArrayList<>(visibleTasks);
							if (!illEnableCheckProperty.get()) {
								for (Task task : list) {
									setColorTaskValue(TaskBackGroundColor.DEFAULT,
                                                      String.valueOf(task.getSuid()));
								}
								break;
							}
							if (!isActiveProperty.get()) {
								break;
							}
							for (Task task : list) {
								Date startDate = task.getStartedDate();
								Date endDate = task.getEndDate();

								if ((null != startDate) && (null != endDate)) {
									long longStartDate = startDate.getTime();
									long longEndDate = endDate.getTime();
									double diskret = ((longEndDate - longStartDate) / 10000);

									double diskretNumber = (new Date().getTime() - longStartDate)
											/ diskret;
									double result = 1 - (diskretNumber * 0.0001);

									String taskSuid = String.valueOf(task.getSuid());
									if (result > 1) {
										setColorTaskValue(TaskBackGroundColor.DOESNT_START,
                                                          taskSuid);
									} else if (result > 0.5) {
										setColorTaskValue(TaskBackGroundColor.GREEN, taskSuid);
									} else if ((result < 0.5) && (result > 0.25)) {
										setColorTaskValue(TaskBackGroundColor.YELLOW, taskSuid);
									} else if (result < 0.01) {
										setColorTaskValue(TaskBackGroundColor.RED, taskSuid);
									}
								}

							}
							Thread.sleep(100L);
						} catch (Exception e) {
							// TODO Логирование
							e.printStackTrace();
						}
						if (!getOpenViewFlag().get()) {
							exit = true;
						}
					}

				});
				thread.setName(typeView.name() + " " + "Progress Color Updater");
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	/**
	 * Возвращает {@link#colorTaskProperty}
	 *
	 * @return the {@link#colorTaskProperty}
	 */
	public ObjectProperty<TaskBackGroundColor> getColorTaskProperty(String aSuid) {
		return colorTaskPropertyMap.get(aSuid);
	}

	/**
	 * Устанавливает значение полю {@link#colorTaskProperty}
	 *
	 * @param colorTaskValue значение поля
	 */
	public void setColorTaskValue(TaskBackGroundColor colorTaskValue, String aSuid) {
		Platform.runLater(() -> {
			ObjectProperty<TaskBackGroundColor> property = colorTaskPropertyMap.get(aSuid);
			if (null != property) {
				property.set(colorTaskValue);
			}
		});

	}

	/**
	 * Создает свойство цвета вкладки задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return созданное свойство
	 */
	public ObjectProperty<TaskBackGroundColor> createColorProperty(String aSuid) {
		ObjectProperty<TaskBackGroundColor> property = new SimpleObjectProperty<>(this, aSuid,
				null);
		colorTaskPropertyMap.put(aSuid, property);
		return property;
	}

	/**
	 * Возвращает {@link#colorTaskPropertyMap}
	 *
	 * @return the {@link#colorTaskPropertyMap}
	 */
	public Map<String, ObjectProperty<TaskBackGroundColor>> getColorTaskPropertyMap() {
		return colorTaskPropertyMap;
	}

	/**
	 * Запускает обновление прогресса
	 */
	public void startComputeProgress() {
		progressUpdater.startCopmuteProgress();
	}

	/**
	 * Возвращает {@link#illumnationEnableCheckMenuItem}
	 *
	 * @return the {@link#illumnationEnableCheckMenuItem}
	 */
	public BooleanProperty getIllEnableCheckProperty() {
		return illEnableCheckProperty;
	}

	/**
	 * Устанавливает значение полю {@link#illumnationEnableCheckMenuItem}
	 *
	 * @param illumnationEnableCheckMenuItemValue значение поля
	 */
	public void setIllEnableCheckValue(Boolean illumnationEnableCheckMenuItemValue) {
		this.illEnableCheckProperty.set(illumnationEnableCheckMenuItemValue);
	}

	/**
	 * Возвращает {@link#isActive}
	 *
	 * @return the {@link#isActive}
	 */
	public Boolean getIsActive() {
		return isActiveProperty.get();
	}

	/**
	 * Устанавливает значение полю {@link#isActive}
	 *
	 * @param isActive значение поля
	 */
	public void setIsActive(Boolean isActive) {
		this.isActiveProperty.set(isActive);
		if (isActive) {
			paramLoad(typeView, currentPageProperty.get());
		}
	}

	/**
	 * Устанавливает значение полю {@link#openViewFlag}
	 *
	 * @param aFlag значение поля
	 */
	@Override
	public void setOpenFlagValue(Boolean aFlag) {
		super.setOpenFlagValue(aFlag);
	}

}
