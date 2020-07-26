package ru.devazz.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.devazz.entity.DefaultTaskEntity;
import ru.devazz.entity.DefaultTaskEntity_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Репозиторий типовых задач
 *
 */
@Repository
@Transactional
public class DefaultTaskRepository extends AbstractRepository<DefaultTaskEntity> {

	public DefaultTaskRepository(EntityManager em) {
		super(em);
	}

	/**
	 * Получение типовых задач по SUID должности
	 *
	 * @param subordinationSUID идентификатор должности
	 * @return список типовых задач
	 */
	public List<DefaultTaskEntity> getDefaultTaskByExecuter(Long subordinationSUID) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DefaultTaskEntity> query = builder.createQuery(DefaultTaskEntity.class);
		Root<DefaultTaskEntity> root = query.from(DefaultTaskEntity.class);
		query.select(root).where(builder.equal(root.get(DefaultTaskEntity_.subordinationSUID),
											   subordinationSUID));
		return em.createQuery(query).getResultList();
	}

	/**
	 * Возвращает типовые задачи по идентификатору автора
	 *
	 * @param aPositionSuid идентификаторт автора
	 * @return список типовых задач
	 */
	public List<DefaultTaskEntity> getDefaultTasksByAuthor(Long aPositionSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DefaultTaskEntity> criteriaQuery = builder
				.createQuery(DefaultTaskEntity.class);
		Root<DefaultTaskEntity> root = criteriaQuery.from(DefaultTaskEntity.class);
		criteriaQuery.select(root)
				.where(builder.equal(root.get(DefaultTaskEntity_.authorSuid), aPositionSuid));
		return em.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * Возвращает список идентификатор авторов типовых задач без повторений
	 * 
	 * @return список идентификаров
	 */
	public List<Long> getDefaultTasksAuthorsSuids() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<DefaultTaskEntity> root = criteriaQuery.from(DefaultTaskEntity.class);
		criteriaQuery.select(root.get(DefaultTaskEntity_.authorSuid)).distinct(true);
		return em.createQuery(criteriaQuery).getResultList();

	}

	@Override
	public Class<DefaultTaskEntity> getEntityClass() {
		return DefaultTaskEntity.class;
	}

}
