package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.TaskHistoryEntity;
import ru.devazz.entity.TaskHistoryEntity_;
import ru.devazz.utils.*;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

/**
 * Репизиторий исторических записей
 */
@Repository
public class TaskHistoryRepository extends AbstractRepository<TaskHistoryEntity> {

	@Override
	public Class<TaskHistoryEntity> getEntityClass() {
		return TaskHistoryEntity.class;
	}

	/**
	 * Возвращает все исторические записи по задаче с определенным типом
	 *
	 * @param aTaskSuid идентификатор задачи
	 * @param aType тип исторической записи
	 * @return список исторических записей
	 */
	public List<TaskHistoryEntity> getAllTaskHistoryEntriesByType(Long aTaskSuid,
			TaskHistoryType aType) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskHistoryEntity> cq = builder.createQuery(TaskHistoryEntity.class);
		Root<TaskHistoryEntity> personRoot = cq.from(TaskHistoryEntity.class);
		cq.select(personRoot);
		cq.where(builder.and(builder.equal(personRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid),
				builder.equal(personRoot.get(TaskHistoryEntity_.historyType), aType)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Возвращает список исторических записей по идентификатору задачи
	 *
	 * @param aTaskSuid идентификатор задачи
	 * @return список исторических записей
	 */
	public List<TaskHistoryEntity> getTaskHistory(Long aTaskSuid, Filter aFilter) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskHistoryEntity> cq = builder.createQuery(TaskHistoryEntity.class);
		Root<TaskHistoryEntity> personRoot = cq.from(TaskHistoryEntity.class);
		cq.select(personRoot);
		cq.where(getHistoryExpression(aFilter, aTaskSuid, personRoot));
		List<TaskHistoryEntity> result = em.createQuery(cq).getResultList();
		sortListHistoryEntries(aFilter, result);
		return result;
	}

	/**
	 * Возвращает страницу исторических записей по идентификатору задачи
	 *
	 * @param aTaskSuid идентификатор задачи
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return список исторических записей
	 */
	public List<TaskHistoryEntity> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
			int aOffset) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskHistoryEntity> cq = builder.createQuery(TaskHistoryEntity.class);
		Root<TaskHistoryEntity> personRoot = cq.from(TaskHistoryEntity.class);
		cq.select(personRoot);
		cq.where(builder.equal(personRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid));
		return em.createQuery(cq).setFirstResult(aOffset).setMaxResults(aLimit).getResultList();
	}

	/**
	 * Возвращает количество записей в таблице исторических записей
	 *
	 * @param aTaskSuid идентификатор задачи
	 * @param aFilter Фильтр
	 * @param aCountEntriesOnPage количество записей на странице
	 * @return колчиество страниц
	 */
	public int getCountPages(Long aTaskSuid, Integer aCountEntriesOnPage, Filter aFilter) {
		int result = 0;
		em.clear();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = builder.createQuery(Long.class);
		Root<TaskHistoryEntity> personRoot = cq.from(TaskHistoryEntity.class);
		cq.select(builder.count(personRoot));
		cq.where(getHistoryExpression(aFilter, aTaskSuid, personRoot));
		int countEntries = Integer
				.parseInt(Long.toString(em.createQuery(cq).getSingleResult()));
		result = (int) Math.ceil((double) countEntries / aCountEntriesOnPage);
		if (0 == result) {
			result = 1;
		}
		return result;
	}

	/**
	 * Возвращает массив предикатов, сформированных по фильтру даты
	 *
	 * @param aFilter фильтр
	 * @param aTaskSuid идентификатор задачи
	 * @return массив предикатов
	 */
	private Expression<Boolean> getHistoryExpression(Filter aFilter, Long aTaskSuid,
			Root<TaskHistoryEntity> aRoot) {
		Expression<Boolean> result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		List<Predicate> listPredicateDate = new ArrayList<>();
		List<Predicate> listPredicateType = new ArrayList<>();
		for (Entry<FilterType, List<String>> entry : aFilter.getFilterTypeMap().entrySet()) {
			switch (entry.getKey()) {
			case FILTER_BY_DATE:
				Date startDate = null;
				Date endDate = null;
				for (String filterKey : entry.getValue()) {
					TaskTimeInterval interval = TaskTimeInterval.getTimeIntervalBySuid(filterKey);
					if (null != interval) {
						switch (interval) {
						case ALL_TIME:
							break;
						case CUSTOM_TIME_INTERVAL:
							startDate = aFilter.getStartDate();
							endDate = aFilter.getEndDate();
							listPredicateDate.add(builder.and(
									builder.greaterThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), startDate),
									builder.lessThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), endDate)));
							break;
						case DAY:
							startDate = Utils.getInstance().getStartDateForFilterDate();
							endDate = Utils.getInstance().getEndDateForFilterDate();
							listPredicateDate.add(builder.and(
									builder.greaterThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), startDate),
									builder.lessThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), endDate)));
							break;
						case MONTH:
							startDate = Utils.getInstance().getStartDateForFilterMonth();
							endDate = Utils.getInstance().getEndDateForFilterMonth();
							listPredicateDate.add(builder.and(
									builder.greaterThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), startDate),
									builder.lessThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), endDate)));
							break;
						case WEEK:
							startDate = Utils.getInstance().getStartDateForFilterWeek();
							endDate = Utils.getInstance().getEndDateForFilterWeek();
							listPredicateDate.add(builder.and(
									builder.greaterThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), startDate),
									builder.lessThanOrEqualTo(
											aRoot.<Date>get(TaskHistoryEntity_.date), endDate)));
							break;
						}
					}
				}
				break;
			case FILTER_BY_HISTORY_TYPE:
				for (String filterKey : entry.getValue()) {
					TaskHistoryType type = TaskHistoryType.getTaskHistoryTypeBySuid(filterKey);
					listPredicateType
							.add(builder.equal(aRoot.get(TaskHistoryEntity_.historyType), type));

				}
				break;
			default:
				break;
			}
		}
		Predicate[] predicatsDate = new Predicate[listPredicateDate.size()];
		Predicate[] predicatsType = new Predicate[listPredicateType.size()];
		for (int i = 0; i < listPredicateDate.size(); i++) {
			predicatsDate[i] = listPredicateDate.get(i);
		}
		for (int i = 0; i < listPredicateType.size(); i++) {
			predicatsType[i] = listPredicateType.get(i);
		}

		if (listPredicateDate.isEmpty() && listPredicateType.isEmpty()) {
			result = builder.equal(aRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid);
		} else if (!listPredicateDate.isEmpty() && listPredicateType.isEmpty()) {
			result = builder.and(builder.equal(aRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid),
					builder.or(predicatsDate));
		} else if (!listPredicateType.isEmpty() && listPredicateDate.isEmpty()) {
			result = builder.and(builder.equal(aRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid),
					builder.or(predicatsType));
		} else if (!(listPredicateDate.isEmpty() || listPredicateType.isEmpty())) {
			result = builder.and(builder.equal(aRoot.get(TaskHistoryEntity_.taskSuid), aTaskSuid),
					builder.and(builder.or(predicatsDate), builder.or(predicatsType)));
		}

		return result;
	}

	/**
	 * Возвращает страницу исторических записей по идентификатору задачи с учетом
	 * фильтра
	 *
	 * @param aTaskSuid идентификатор задачи
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @param aFilter фильтр
	 * @return список исторических записей
	 */
	public List<TaskHistoryEntity> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
			int aOffset, Filter aFilter) {
		List<TaskHistoryEntity> result = new ArrayList<>();
		List<TaskHistoryEntity> list = null;
		em.clear();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TaskHistoryEntity> cq = builder.createQuery(TaskHistoryEntity.class);
		Root<TaskHistoryEntity> personRoot = cq.from(TaskHistoryEntity.class);
		cq.select(personRoot);
		cq.where(getHistoryExpression(aFilter, aTaskSuid, personRoot));
		list = em.createQuery(cq).getResultList();
		if (aFilter.getContainsSort()) {
			sortListHistoryEntries(aFilter, list);
		}
		int k = 0;
		for (int i = 0; i <= list.size(); i++) {
			if (i == aOffset) {
				for (int j = aOffset; j < list.size(); j++) {
					if (k < aLimit) {
						result.add(list.get(j));
						k++;
					}
				}
			}

		}
		return result;
	}

	/**
	 * Сортировка списка исторических записей
	 *
	 * @param aFilter фильтр
	 * @param list список
	 */
	private void sortListHistoryEntries(Filter aFilter, List<TaskHistoryEntity> list) {
		if (null != aFilter.getSortType()) {
			switch (aFilter.getSortType()) {
			case SORT_BY_DATE_FIRST_NEW:
				list.sort((o1, o2) -> {
					int sort = 0;
					Date o1Date = o1.getDate();
					Date o2Date = o2.getDate();
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
				list.sort((o1, o2) -> {
					int sort = 0;
					Date o1Date = o1.getDate();
					Date o2Date = o2.getDate();
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
			default:
				break;
			}
		}
	}

	/**
	 * Возвращает номер страницы исторической записи
	 *
	 * @param aEntry историческая запись
	 * @param aCountEntriesOnPage количество записей на странице
	 * @return номер страницы исторической записи
	 */
	public Integer getNumberPageByTask(TaskHistoryEntity aEntry, Long aTaskSuid,
			Integer aCountEntriesOnPage, Filter aFilter) {
		return calcTaskPageNumber(getTaskHistory(aTaskSuid, aFilter), aEntry.getSuid(),
				aCountEntriesOnPage);
	}

	/**
	 * Рассчитывает номер страницы по идентификатору исторической записи
	 *
	 * @param aCountTaskOnPage количество задач на странице
	 * @param aTaskHistoryEntrySuid идентификатор исторической записи
	 * @return номер страницы
	 */
	private Integer calcTaskPageNumber(List<TaskHistoryEntity> aList, Long aTaskHistoryEntrySuid,
			Integer aCountTaskOnPage) {
		int result = 0;
		if (null != aList) {
			int k = 0;
			int countPages = 0;
			for (TaskHistoryEntity task : aList) {
				if (k == aCountTaskOnPage) {
					k = 0;
					countPages++;
				}
				k++;
				if (task.getSuid().equals(aTaskHistoryEntrySuid)) {
					result = countPages;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Удаляет историю по идентификатору задачи
	 *
	 * @param aTaskSuid иденификатор задачи
	 */
	public void deleteHistory(Long aTaskSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<TaskHistoryEntity> query =
				builder.createCriteriaDelete(TaskHistoryEntity.class);
		Root<TaskHistoryEntity> root = query.from(TaskHistoryEntity.class);
		query.where(builder.equal(root.get(TaskHistoryEntity_.taskSuid), aTaskSuid));
		em.createQuery(query).executeUpdate();
	}

}
