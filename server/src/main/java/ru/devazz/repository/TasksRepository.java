package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.TaskEntity;
import ru.devazz.entity.TaskEntity_;
import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.enums.*;
import ru.devazz.utils.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

/**
 * Репозиторий задач
 */
@Repository
@Transactional
public class TasksRepository extends AbstractRepository<TaskEntity> {

	private EntityManager em;

	public TasksRepository(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public void delete(Long aSuid) {
		TaskEntity entity = get(aSuid);
		if (null != entity) {
			super.delete(aSuid);
		}
	}

	/**
	 * Возвращает список задач по идентификатору должности автора задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getAllTasksByAuthor(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.equal(personRoot.get(TaskEntity_.authorSuid), aPositionSuid));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает список задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getAllTasksByExecutor(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.equal(personRoot.get(TaskEntity_.executorSuid), aPositionSuid));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает список задач по идентификатору должности автора задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByAuthor(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskEntity_.taskType), TaskType.USUAL),
				builder.equal(personRoot.get(TaskEntity_.authorSuid), aPositionSuid)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает список задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByExecutor(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskEntity_.taskType), TaskType.USUAL),
				builder.equal(personRoot.get(TaskEntity_.executorSuid), aPositionSuid)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает страницу задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница задач
	 */
	public List<TaskEntity> getInTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> query = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> root = query.from(TaskEntity.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(TaskEntity_.executorSuid), aPositionSuid),
								builder.equal(root.get(TaskEntity_.taskType), TaskType.USUAL)));
		return em.createQuery(query).setFirstResult(aOffset).setMaxResults(aLimit).getResultList();
	}

	/**
	 * Возвращает страницу задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница задач
	 */
	public List<TaskEntity> getOutTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> query = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> root = query.from(TaskEntity.class);
		query.select(root);
		query.where(builder.and(builder.equal(root.get(TaskEntity_.authorSuid), aPositionSuid),
								builder.equal(root.get(TaskEntity_.taskType), TaskType.USUAL)));
		return em.createQuery(query).setFirstResult(aOffset).setMaxResults(aLimit).getResultList();
	}

	/**
	 * Возвращает типовые задачи по исполнителю
	 *
	 * @param aPositionSuid идентификатор исполнителя
	 * @return
	 */
	public List<TaskEntity> getDefaultTask(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
				builder.equal(personRoot.get(TaskEntity_.executorSuid), aPositionSuid)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает список задач по типу и идентификатору должности
	 *
	 * @param aType тип задачи
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByType(Long aPositionSuid, TaskType aType) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
							 builder.or(builder.equal(personRoot.get(TaskEntity_.authorSuid),
													  aPositionSuid)),
							 builder.equal(personRoot.get(TaskEntity_.executorSuid),
										   aPositionSuid)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Сортировка списка задач
	 *
	 * @param aFilter фильтр
	 * @param list список
	 */
	private void sortListTasks(Filter aFilter, List<TaskEntity> list) {
		if (null != aFilter.getSortType()) {
			switch (aFilter.getSortType()) {
			case SORT_BY_DATE_FIRST_NEW:
				Collections.sort(list, (o1, o2) -> {
					int sort = 0;
					Date o1Date = o1.getStartDate();
					Date o2Date = o2.getStartDate();
					if (o1Date.getTime() == o2Date.getTime()) {
						sort = 0;
					} else if (o1Date.getTime() < o2Date.getTime()) {
						sort = 1;
					} else {
						sort = -1;
					}
					return sort;
				});
				break;
			case SORT_BY_DATE_FIRST_OLD:
				Collections.sort(list, (o1, o2) -> {
					int sort = 0;
					Date o1Date = o1.getStartDate();
					Date o2Date = o2.getStartDate();
					if (o1Date.getTime() == o2Date.getTime()) {
						sort = 0;
					} else if (o1Date.getTime() > o2Date.getTime()) {
						sort = 1;
					} else {
						sort = -1;
					}
					return sort;
				});
				break;
			case SORT_BY_PRIORITY_ASCENDING:
				Collections.sort(list, (o1, o2) -> {
					return o2.getPriority().compareTo(o1.getPriority());
				});
				break;
			case SORT_BY_PRIORITY_DESCENDING:
				Collections.sort(list, (o1, o2) -> {
					return o1.getPriority().compareTo(o2.getPriority());
				});
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Возвращает условие для поиска задач по представлению
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @param aViewType тип представления
	 * @param aRoot корневой элемент
	 * @return условие поиска
	 */
	private Predicate getTasksExressions(Long aPositionSuid, TasksViewType aViewType,
			Root<TaskEntity> aRoot, Filter aFilter) {
		Predicate result = null;
		Predicate filterPredicate = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		List<Predicate> listPredicateDate = new ArrayList<>();
		List<Predicate> listPredicatePriority = new ArrayList<>();
		List<Predicate> listPredicateStatus = new ArrayList<>();
		for (Entry<FilterType, List<String>> entry : aFilter.getFilterTypeMap().entrySet()) {
			switch (entry.getKey()) {
			case FILTER_BY_DATE:
				Date startDate = null;
				Date endDate = null;
				for (String filterKey : entry.getValue()) {
					TaskTimeInterval interval = TaskTimeInterval.getTimeIntervalBySuid(filterKey);
					if (null != interval) {
						switch (interval) {
						case CUSTOM_TIME_INTERVAL:
							startDate = aFilter.getStartDate();
							endDate = aFilter.getEndDate();
							break;
						case DAY:
							startDate = Utils.getInstance().getStartDateForFilterDate();
							endDate = Utils.getInstance().getEndDateForFilterDate();
							break;
						case MONTH:
							startDate = Utils.getInstance().getStartDateForFilterMonth();
							endDate = Utils.getInstance().getEndDateForFilterMonth();
							break;
						case WEEK:
							startDate = Utils.getInstance().getStartDateForFilterWeek();
							endDate = Utils.getInstance().getEndDateForFilterWeek();
							break;
						case PARTICULAR_TIME_INTERVAL:
							startDate = aFilter.getStartDate();
							listPredicateDate.add(builder
									.greaterThan(aRoot.<Date>get(TaskEntity_.endDate), startDate));
							break;
						default:
							break;
						}
						if (!(TaskTimeInterval.ALL_TIME.equals(interval)
								|| TaskTimeInterval.PARTICULAR_TIME_INTERVAL.equals(interval))) {
							listPredicateDate.add(builder.and(
									builder.greaterThanOrEqualTo(
											aRoot.<Date>get(TaskEntity_.startDate), startDate),
									builder.lessThanOrEqualTo(
											aRoot.<Date>get(TaskEntity_.startDate), endDate)));
						}
					}
				}
				break;
			case FILTER_BY_PRIORITY:
				for (String filterKey : entry.getValue()) {
					TaskPriority priority = TaskPriority.getPriorityBySuid(filterKey);
					if (null != priority) {
						listPredicatePriority
								.add(builder.equal(aRoot.get(TaskEntity_.priority), priority));
					}
				}
				break;

			case FILTER_BY_STATUS:
				for (String filterKey : entry.getValue()) {
					TaskStatus status = TaskStatus.getStatusBySuid(filterKey);
					if (null != status) {
						listPredicateStatus
								.add(builder.equal(aRoot.get(TaskEntity_.status), status));
					}
				}
				break;
			default:
				break;
			}
		}

		Predicate[] predicatsDate = new Predicate[listPredicateDate.size()];
		Predicate[] predicatsPriority = new Predicate[listPredicatePriority.size()];
		Predicate[] predicatsStatus = new Predicate[listPredicateStatus.size()];
		for (int i = 0; i < listPredicateDate.size(); i++) {
			predicatsDate[i] = listPredicateDate.get(i);
		}
		for (int i = 0; i < listPredicatePriority.size(); i++) {
			predicatsPriority[i] = listPredicatePriority.get(i);
		}
		for (int i = 0; i < listPredicateStatus.size(); i++) {
			predicatsStatus[i] = listPredicateStatus.get(i);
		}

		if (listPredicateDate.isEmpty() && listPredicatePriority.isEmpty()
				&& !listPredicateStatus.isEmpty()) {
			filterPredicate = builder.or(predicatsStatus);
		} else if (listPredicateDate.isEmpty() && !listPredicatePriority.isEmpty()
				&& listPredicateStatus.isEmpty()) {
			filterPredicate = builder.or(predicatsPriority);
		} else if (listPredicateDate.isEmpty() && !listPredicatePriority.isEmpty()
				&& !listPredicateStatus.isEmpty()) {
			filterPredicate = builder.and(builder.or(predicatsPriority),
					builder.or(predicatsStatus));
		} else if (!listPredicateDate.isEmpty() && listPredicatePriority.isEmpty()
				&& listPredicateStatus.isEmpty()) {
			filterPredicate = builder.and(predicatsDate);
		} else if (!listPredicateDate.isEmpty() && listPredicatePriority.isEmpty()
				&& !listPredicateStatus.isEmpty()) {
			filterPredicate = builder.and(builder.or(predicatsDate), builder.or(predicatsStatus));
		} else if (!listPredicateDate.isEmpty() && !listPredicatePriority.isEmpty()
				&& listPredicateStatus.isEmpty()) {
			filterPredicate = builder.and(builder.or(predicatsDate), builder.or(predicatsPriority));
		} else if (!listPredicateDate.isEmpty() && !listPredicatePriority.isEmpty()
				&& !listPredicateStatus.isEmpty()) {
			filterPredicate = builder.and(builder.or(predicatsDate), builder.or(predicatsPriority),
					builder.or(predicatsStatus));
		}

		if (null != aViewType) {
			switch (aViewType) {
			case ARCHIVE:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.ARCHIVAL),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case CURRENT_IN:
				result = builder.and(builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.USUAL),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case CURRENT_OUT:
				result = builder.and(builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.USUAL),
						builder.equal(aRoot.get(TaskEntity_.authorSuid), aPositionSuid));
				break;
			case CURRENT_EVERYDAY_IN:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case CURRENT_EVERYDAY_OUT:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
						builder.equal(aRoot.get(TaskEntity_.authorSuid), aPositionSuid));
				break;
			case CURRENT_ARCHIVE:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.ARCHIVAL),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case EVERYDAY_IN:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case EVERYDAY_OUT:
				result = builder.and(
						builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
						builder.equal(aRoot.get(TaskEntity_.authorSuid), aPositionSuid));
				break;
			case IN:
				result = builder.and(builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.USUAL),
						builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid));
				break;
			case OUT:
				result = builder.and(builder.equal(aRoot.get(TaskEntity_.taskType), TaskType.USUAL),
						builder.equal(aRoot.get(TaskEntity_.authorSuid), aPositionSuid));
				break;
			}
		} else {
			result = builder.or(builder.equal(aRoot.get(TaskEntity_.executorSuid), aPositionSuid),
					builder.equal(aRoot.get(TaskEntity_.authorSuid), aPositionSuid));
		}
		if (null != filterPredicate) {
			result = builder.and(result, filterPredicate);
		}

		return result;
	}

	/**
	 * Возвращает список задач по типу представления
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @param aViewType тип представления
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByViewTypeWithPagination(Long aPositionSuid,
			TasksViewType aViewType, int aLimit, int aOffset, Filter aFilter) {
		List<TaskEntity> result = new ArrayList<>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		Expression<Boolean> expression = getTasksExressions(aPositionSuid, aViewType, personRoot,
				aFilter);
		if (null != expression) {
			cq.where(expression);
			result.addAll(em.createQuery(cq).setFirstResult(aOffset).setMaxResults(aLimit)
					.getResultList());
		}
		if (aFilter.getContainsSort()) {
			sortListTasks(aFilter, result);
		}
		return result;
	}

	/**
	 * Возвращает список задач по типу представления
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @param aViewType тип представления
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByViewType(Long aPositionSuid, TasksViewType aViewType,
			Filter aFilter) {
		List<TaskEntity> result = new ArrayList<>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		Expression<Boolean> expression = getTasksExressions(aPositionSuid, aViewType, personRoot,
				aFilter);
		if (null != expression) {
			cq.where(expression);
			result.addAll(em.createQuery(cq).getResultList());
		}
		return result;
	}

	/**
	 * Возвращает список циклических просроченных задач
	 *
	 * @return список циклических просроченных задач
	 */
	public List<TaskEntity> getCycleTasks() {
		List<TaskEntity> result = new ArrayList<>();
		em.clear();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.or(
				builder.equal(personRoot.get(TaskEntity_.status), TaskStatus.OVERDUE.getMenuSuid()),
				builder.equal(personRoot.get(TaskEntity_.status),
						TaskStatus.WORKING.getMenuSuid())),
				builder.not(builder.equal(personRoot.get(TaskEntity_.cycleType), null))));
		return result;
	}

	/**
	 * Возвращает список задач по типу и идентификатору должности
	 *
	 * @param aType тип задачи
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	public List<TaskEntity> getTasksByTypeWithPagination(Long aPositionSuid, TaskType aType,
			int aLimit, int aOffset) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskEntity_.taskType), TaskType.DEFAULT),
							 builder.or(builder.equal(personRoot.get(TaskEntity_.authorSuid),
													  aPositionSuid)),
							 builder.equal(personRoot.get(TaskEntity_.executorSuid),
										   aPositionSuid)));
		return em.createQuery(cq).setFirstResult(aOffset)
				.setMaxResults(aLimit).getResultList();
	}

	/**
	 * Возвращает страницу записей
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	public List<TaskEntity> getSubElPageTasks(Long aPositionSuid, int aLimit, int aOffset) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		cq.where(builder.or(builder.equal(personRoot.get(TaskEntity_.authorSuid),
													  aPositionSuid),
							 builder.equal(personRoot.get(TaskEntity_.executorSuid),
										   aPositionSuid)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает количество страниц задач
	 *
	 * @param aType тип панели
	 * @param aPositionSuid идентификатор должности
	 * @return количество страниц задач
	 */
	public Long getCountTasks(TasksViewType aType, Long aPositionSuid, Filter aFilter) {
		em.clear();
		Long result = 0L;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(builder.count(personRoot));
		Expression<Boolean> expression = getTasksExressions(aPositionSuid, aType, personRoot,
				aFilter);
		if (null != expression) {
			cq.where(expression);
			result = em.createQuery(cq).getSingleResult();
		}
		return result;
	}

	/**
	 * Возвращает список всех задач боевого поста по фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	public List<TaskEntity> getAllUserTasksWithFilter(Long aPositionSuid, Filter aFilter) {
		List<TaskEntity> result = new ArrayList<>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		Predicate expression = getTasksExressions(aPositionSuid, null, personRoot, aFilter);
		if (null != expression) {
			cq.where(expression);
			result.addAll(em.createQuery(cq).getResultList());
		}
		return result;
	}

	/**
	 * Возвращает список задач в которых боевой пост числится автором по фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	public List<TaskEntity> getAllUserTasksAuthorWithFilter(Long aPositionSuid, Filter aFilter) {
		List<TaskEntity> result = new ArrayList<>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		Predicate expression = getTasksExressions(aPositionSuid, null, personRoot, aFilter);
		if (null != expression) {
			cq.where(builder.and(expression,
					builder.equal(personRoot.get(TaskEntity_.authorSuid), aPositionSuid)));
			result.addAll(em.createQuery(cq).getResultList());
		}
		return result;
	}

	/**
	 * Возвращает список задач в которых боевой пост числится исполонителем по
	 * фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	public List<TaskEntity> getAllUserTasksExecutorWithFilter(Long aPositionSuid, Filter aFilter) {
		List<TaskEntity> result = new ArrayList<>();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskEntity> cq = builder.createQuery(TaskEntity.class);
		Root<TaskEntity> personRoot = cq.from(TaskEntity.class);
		cq.select(personRoot);
		Predicate expression = getTasksExressions(aPositionSuid, null, personRoot, aFilter);
		if (null != expression) {
			cq.where(builder.and(expression,
					builder.equal(personRoot.get(TaskEntity_.executorSuid), aPositionSuid)));
			result.addAll(em.createQuery(cq).getResultList());
		}
		return result;
	}
	@Override
	public Class<TaskEntity> getEntityClass() {
		return TaskEntity.class;
	}

}
