package ru.devazz.repository;

import ru.devazz.entity.EventEntity;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Репохиторий событий
 */
public class EventRepository extends AbstractRepository<EventEntity> {

	/**
	 * Удаляет события по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 */
	public void deleteEventsByTasksSuid(Long aSuid) {
		List<EventEntity> list = getEventsByTaskSuid(aSuid);
		for (EventEntity entity : list) {
			delete(entity.getIdEvents());
		}
	}

	/**
	 * Возвращает список событий по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return список событий
	 */
	private List<EventEntity> getEventsByTaskSuid(Long aSuid) {
		em.clear();
		String nameQuery = getEntityClass().getSimpleName() + ".getEventsByTaskSuid";
		TypedQuery<EventEntity> namedQuery = em.createNamedQuery(nameQuery, getEntityClass()).setParameter("taskSuid",
				aSuid);
		return namedQuery.getResultList();

	}

	@Override
	public Class<EventEntity> getEntityClass() {
		return EventEntity.class;
	}

}
