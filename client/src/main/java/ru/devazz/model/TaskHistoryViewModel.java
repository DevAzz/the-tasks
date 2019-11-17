package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import ru.devazz.entities.Task;
import ru.devazz.server.EJBProxyFactory;
import ru.devazz.server.api.ITaskHistoryService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.TaskHistoryModel;
import ru.devazz.server.api.model.enums.FilterType;
import ru.devazz.server.api.model.enums.SortType;
import ru.devazz.server.api.model.enums.TaskHistoryType;
import ru.devazz.server.api.model.enums.TaskTimeInterval;

import javax.jms.JMSException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Модель представления формы истории задачи
 */
public class TaskHistoryViewModel
		extends PresentationModel<ITaskHistoryService, TaskHistoryModel> {

	/** Свойство количества страниц */
	private IntegerProperty pageCountProperty;

	/** Количество записей на странице */
	private Integer countPageEntries = 5;

	/** Видимые исторические записи */
	private ObservableList<TaskHistoryModel> visibleEntries;

	/** Модель виджета выбора временного промежутка */
	private CustomTimeIntervalModel customTimeIntervalModel;

	/** Свойство выбора пункта меню "Фильтр по дате" -> "За день" */
	private BooleanProperty dayFilterSelectedProperty;

	/** Свойство выбора пункта меню "Фильтр по дате" -> "За неделю" */
	private BooleanProperty weekFilterSelectedProperty;

	/** Свойство выбора пункта меню "Фильтр по дате" -> "За месяц" */
	private BooleanProperty monthFilterSelectedProperty;

	/** Свойство выбора пункта меню "Фильтр по дате" -> "За все время" */
	private BooleanProperty allTimeFilterSelectedProperty;

	/** Свойство выбора пункта меню "Сортировка по дате" -> "Сначала старые" */
	private BooleanProperty sortByDateFirstOld;

	/** Свойство выбора пункта меню "Сортировка по дате" -> "Сначала новые" */
	private BooleanProperty sortByDateFirstNew;

	/** Задача */
	private Task task = null;

	/** Фильтр исторических записей */
	private Filter filter = new Filter();

	/** Свойство номера текущей страницы */
	private IntegerProperty currentPageProperty;

	/** Список наименований активных фильтров */
	private ObservableList<String> nameActiveFiltersList = FXCollections
			.synchronizedObservableList(FXCollections.observableArrayList());

	@Override
	protected void initModel() {
		if (null == visibleEntries) {
			visibleEntries = FXCollections
					.synchronizedObservableList(FXCollections.observableArrayList());
		}

		dayFilterSelectedProperty = new SimpleBooleanProperty(this, "dayFilterSelectedProperty",
				false);
		weekFilterSelectedProperty = new SimpleBooleanProperty(this, "weekFilterSelectedProperty",
				false);
		monthFilterSelectedProperty = new SimpleBooleanProperty(this, "monthFilterSelectedProperty",
				false);
		allTimeFilterSelectedProperty = new SimpleBooleanProperty(this,
				"allTimeFilterSelectedProperty", false);
		sortByDateFirstNew = new SimpleBooleanProperty(this, "sortByDateFirstNew", false);
		sortByDateFirstOld = new SimpleBooleanProperty(this, "sortByDateFirstOld", false);
		currentPageProperty = new SimpleIntegerProperty(this, "currentPageProperty", 0);
		pageCountProperty = new SimpleIntegerProperty(this, "pageCountProperty", 1);

		connectToJMSService();
	}

	/**
	 * Установка начального значения фильтрации
	 *
	 * @param aSortType тип сортировки
	 */
	public void setInitialSortType(SortType aSortType) {
		if (null != aSortType) {
			filter.setContainsSort(true);
			filter.setSortType(aSortType);
			switch (aSortType) {
			case SORT_BY_DATE_FIRST_NEW:
				sortByDateFirstNew.set(true);
				sortByDateFirstOld.set(false);
				break;
			case SORT_BY_DATE_FIRST_OLD:
				sortByDateFirstOld.set(true);
				sortByDateFirstNew.set(false);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Сортировка записей
	 *
	 * @param aItemId идентификатор элемента меню
	 */
	public void sort(String aItemId) {
		if (null != aItemId) {
			switch (aItemId) {
			case "sortByDateFirstNewItem":
				if (sortByDateFirstNew.get()) {
					sortByDateFirstOld.set(false);
					filter.setContainsSort(true);
					filter.setSortType(SortType.SORT_BY_DATE_FIRST_NEW);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.setContainsSort(sortByDateFirstOld.get());
				}
				break;
			case "sortByDateFirstOldItem":
				if (sortByDateFirstOld.get()) {
					sortByDateFirstNew.set(false);
					filter.setContainsSort(true);
					filter.setSortType(SortType.SORT_BY_DATE_FIRST_OLD);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.setContainsSort(sortByDateFirstNew.get());
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Фильтрация по дате
	 *
	 * @param aItemId идентификатор пункта меню
	 */
	public void dateFilter(String aItemId) {
		if (null != aItemId) {
			switch (aItemId) {
			case "dayFilterItem":
				if (dayFilterSelectedProperty.get()) {
					weekFilterSelectedProperty.set(false);
					monthFilterSelectedProperty.set(false);
					allTimeFilterSelectedProperty.set(false);
					List<String> list = new ArrayList<>();
					list.add(TaskTimeInterval.DAY.getMenuSuid());
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, new ArrayList<>());
					loadPageEntries(currentPageProperty.get());
				}
				break;
			case "weekFilterItem":
				if (weekFilterSelectedProperty.get()) {
					dayFilterSelectedProperty.set(false);
					monthFilterSelectedProperty.set(false);
					allTimeFilterSelectedProperty.set(false);
					List<String> list = new ArrayList<>();
					list.add(TaskTimeInterval.WEEK.getMenuSuid());
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, new ArrayList<>());
					loadPageEntries(currentPageProperty.get());
				}
				break;
			case "monthFilterItem":
				if (monthFilterSelectedProperty.get()) {
					dayFilterSelectedProperty.set(false);
					weekFilterSelectedProperty.set(false);
					allTimeFilterSelectedProperty.set(false);
					List<String> list = new ArrayList<>();
					list.add(TaskTimeInterval.MONTH.getMenuSuid());
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, new ArrayList<>());
					loadPageEntries(currentPageProperty.get());
				}
				break;
			case "allTimeFilterItem":
				if (allTimeFilterSelectedProperty.get()) {
					dayFilterSelectedProperty.set(false);
					weekFilterSelectedProperty.set(false);
					monthFilterSelectedProperty.set(false);
					List<String> list = new ArrayList<>();
					list.add(TaskTimeInterval.ALL_TIME.getMenuSuid());
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, list);
					loadPageEntries(currentPageProperty.get());
				} else {
					filter.getFilterTypeMap().put(FilterType.FILTER_BY_DATE, new ArrayList<>());
					loadPageEntries(currentPageProperty.get());
				}
				break;
			default:
				break;
			}
			updateNameFilterLabel();
		}
	}

	/**
	 * Обновление наименования активного фильтра
	 */
	public void updateNameFilterLabel() {
		nameActiveFiltersList.clear();
		List<String> listTypeFilter = filter.getFilterTypeMap()
				.get(FilterType.FILTER_BY_HISTORY_TYPE);
		List<String> listTypeDate = filter.getFilterTypeMap().get(FilterType.FILTER_BY_DATE);
		if ((null != listTypeFilter) && !listTypeFilter.isEmpty()) {
			nameActiveFiltersList.add("Фильтры по типу:");
			for (String value : listTypeFilter) {
				TaskHistoryType type = TaskHistoryType.getTaskHistoryTypeBySuid(value);
				switch (type) {
				case TASK_CLOSED:
					nameActiveFiltersList.add("Закрытие задачи");
					break;
				case TASK_DONE:
					nameActiveFiltersList.add("Завершение задачи");
					break;
				case TASK_OVERDUE_DONE:
					nameActiveFiltersList.add("Завершение задачи c опозданием");
					break;
				case TASK_FAILED:
					nameActiveFiltersList.add("Провал задачи");
					break;
				case TASK_OVERDUE:
					nameActiveFiltersList.add("Просрочка задачи");
					break;
				case TASK_REMAPPING:
					nameActiveFiltersList.add("Переназначение задачи");
					break;
				case TASK_REWORK:
					nameActiveFiltersList.add("Доработка задачи");
					break;
				case TASK_UPDATED:
					nameActiveFiltersList.add("Обновление задачи");
					break;
				default:
					break;
				}
			}
		}
		if ((null != listTypeDate) && !listTypeDate.isEmpty()) {
			nameActiveFiltersList.add("Фильтр по дате:");
			for (String value : listTypeDate) {
				TaskTimeInterval interval = TaskTimeInterval.getTimeIntervalBySuid(value);
				if (null != interval) {
					String name = "";
					if (TaskTimeInterval.CUSTOM_TIME_INTERVAL.equals(interval)) {
						SimpleDateFormat parser = new SimpleDateFormat("HH:mm dd.MM.yyyy");
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
	 * Фильтрация по типу исторических записей
	 *
	 * @param aSelectedItemMap карта выделения элементов меню фильтрации по типу
	 *            исторических записей
	 */
	public void typeFilter(Map<String, Boolean> aSelectedItemMap) {
		List<String> list = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : aSelectedItemMap.entrySet()) {
			switch (entry.getKey()) {
			case "doneTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_DONE.getTypeSuid());
				}
				break;
			case "reworkTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_REWORK.getTypeSuid());
				}
				break;
			case "failedTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_FAILED.getTypeSuid());
				}
				break;
			case "updateTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_UPDATED.getTypeSuid());
				}
				break;
			case "overdueTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_OVERDUE.getTypeSuid());
				}
				break;
			case "remappingTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_REMAPPING.getTypeSuid());
				}
				break;
			case "closedTypeItem":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_CLOSED.getTypeSuid());
				}
				break;
			case "taskOverdueDone":
				if (entry.getValue()) {
					list.add(TaskHistoryType.TASK_OVERDUE_DONE.getTypeSuid());
				}
				break;
			default:
				break;
			}
		}
		filter.getFilterTypeMap().put(FilterType.FILTER_BY_HISTORY_TYPE, list);
		updateNameFilterLabel();
		loadPageEntries(0);
	}

	@Override
	public Class<ITaskHistoryService> getTypeService() {
		return ITaskHistoryService.class;
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
	 * Возвращает {@link#sortByDateFirstOld}
	 *
	 * @return the {@link#sortByDateFirstOld}
	 */
	public BooleanProperty getSortByDateFirstOld() {
		return sortByDateFirstOld;
	}

	/**
	 * Возвращает {@link#sortByDateFirstNew}
	 *
	 * @return the {@link#sortByDateFirstNew}
	 */
	public BooleanProperty getSortByDateFirstNew() {
		return sortByDateFirstNew;
	}

	/**
	 * Возвращает {@link#dayFilterSelectedProperty}
	 *
	 * @return the {@link#dayFilterSelectedProperty}
	 */
	public BooleanProperty getDayFilterSelectedProperty() {
		return dayFilterSelectedProperty;
	}

	/**
	 * Возвращает {@link#weekFilterSelectedProperty}
	 *
	 * @return the {@link#weekFilterSelectedProperty}
	 */
	public BooleanProperty getWeekFilterSelectedProperty() {
		return weekFilterSelectedProperty;
	}

	/**
	 * Возвращает {@link#monthFilterSelectedProperty}
	 *
	 * @return the {@link#monthFilterSelectedProperty}
	 */
	public BooleanProperty getMonthFilterSelectedProperty() {
		return monthFilterSelectedProperty;
	}

	/**
	 * Возвращает {@link#allTimeFilterSelectedProperty}
	 *
	 * @return the {@link#allTimeFilterSelectedProperty}
	 */
	public BooleanProperty getAllTimeFilterSelectedProperty() {
		return allTimeFilterSelectedProperty;
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
	 * Возвращает {@link#visibleEntries}
	 *
	 * @return the {@link#visibleEntries}
	 */
	public ObservableList<TaskHistoryModel> getVisibleEntries() {
		return visibleEntries;
	}

	/**
	 * Загрузка страницы записей
	 *
	 * @param aPageNumber номер страницы записей
	 */
	public void loadPageEntries(Integer aPageNumber) {
		Thread thread = new Thread(() -> {
			try {
				listDataModelEntities.clear();
				visibleEntries.clear();
				listDataModelEntities.addAll(service.getTaskHistoryWithPagination(task.getSuid(),
						countPageEntries, countPageEntries * aPageNumber, filter));
				for (TaskHistoryModel entity : new ArrayList<>(listDataModelEntities)) {
					Thread.sleep(70L);
					visibleEntries.add(entity);
				}
			} catch (InterruptedException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Возвращает {@link#task}
	 *
	 * @return the {@link#task}
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * Устанавливает значение полю {@link#task}
	 *
	 * @param task значение поля
	 */
	public void setTask(Task task) {
		this.task = task;
		setPageCountPropertValue(getCountPages());
	}

	/**
	 * Возвращает номер страницы исторической записи
	 *
	 * @param aEntry историческая запись
	 * @return номер страницы исторической записи
	 */
	public Integer getNumberPageByTask(TaskHistoryModel aEntry) {
		return service.getNumberPageByTask(aEntry, task.getSuid(), countPageEntries, filter);
	}

	/**
	 * Возвращает количество записей в таблице задач
	 *
	 * @return колчиество страниц
	 */
	public int getCountPages() {
		return service.getCountPages(task.getSuid(), countPageEntries, filter);
	}

	/**
	 * Возвращает историческую запись по индентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return историческая запись
	 */
	public TaskHistoryModel getHistoryEntryBySuid(Long aSuid) {
		TaskHistoryModel result = null;
		for (TaskHistoryModel entity : listDataModelEntities) {
			if (entity.getSuid().equals(aSuid)) {
				result = entity;
			}
		}
		return result;
	}

	/**
	 * Подключение к службе рассылки JMS сообщений
	 */
	private void connectToJMSService() {
		EJBProxyFactory.getInstance().addMessageListener(message -> {
			try {
				if (message instanceof ActiveMQMessage) {
					ActiveMQMessage objectMessage = (ActiveMQMessage) message;
					if (objectMessage instanceof ActiveMQObjectMessage) {
						ObjectEvent event =
								(ObjectEvent) ((ActiveMQObjectMessage) objectMessage).getObject();
						IEntity entity = event.getEntity();
						if (entity instanceof TaskHistoryModel) {
							TaskHistoryModel historyEntity = (TaskHistoryModel) entity;
							if (historyEntity.getTaskSuid().equals(task.getSuid())) {
								visibleEntries.add(historyEntity);
							}
						}
					}
				}
			} catch (JMSException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
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
	 * Возвращает {@link#nameActiveFiltersList}
	 *
	 * @return the {@link#nameActiveFiltersList}
	 */
	public ObservableList<String> getNameActiveFiltersList() {
		return nameActiveFiltersList;
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
					loadPageEntries(currentPageProperty.get());
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
	 * Выполняет загрузку всех записей
	 */
	public void showAllEntries() {
		nameActiveFiltersList.clear();
		dayFilterSelectedProperty.set(false);
		weekFilterSelectedProperty.set(false);
		monthFilterSelectedProperty.set(false);
		allTimeFilterSelectedProperty.set(false);
		customTimeIntervalModel.clearDateFields();

		filter.setContainsSort(false);
		filter.setSortType(null);
		filter.getFilterTypeMap().clear();
		loadPageEntries(currentPageProperty.get());
	}

}
