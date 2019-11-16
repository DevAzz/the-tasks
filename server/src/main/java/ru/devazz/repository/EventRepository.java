package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.EventEntity;
import ru.devazz.entity.EventEntity_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Репохиторий событий
 */
@Repository
public class EventRepository extends AbstractRepository<EventEntity> {

	private EntityManager em;

	public EventRepository(EntityManager em) {
		super(em);
		this.em = em;
	}

	/**
	 * Удаляет события по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 */
	public void deleteEventsByTasksSuid(Long aSuid) {
		List<EventEntity> list = getEventsByTaskSuid(aSuid);
		for (EventEntity entity : list) {
			delete(entity.getSuid());
		}
	}

	/**
	 * Возвращает список событий по идентификатору задачи
	 *
	 * @param aSuid идентификатор задачи
	 * @return список событий
	 */
	public List<EventEntity> getEventsByTaskSuid(Long aSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);
		Root<EventEntity> root = query.from(EventEntity.class);
		query.select(root).where(builder.equal(root.get(EventEntity_.taskSuid), aSuid));
		return em.createQuery(query).getResultList();

	}

	@Override
	public Class<EventEntity> getEntityClass() {
		return EventEntity.class;
	}

}
