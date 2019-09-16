package ru.devazz.repository;

import ru.devazz.entity.SubordinationElementEntity;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Репозиторий элементов дерева подчиненности
 */
public class SubordinationElementRepository extends AbstractRepository<SubordinationElementEntity> {

	@Override
	public Class<SubordinationElementEntity> getEntityClass() {
		return SubordinationElementEntity.class;
	}

	/**
	 * Ищет элемент подчиненности по идентификатору роли
	 *
	 * @param aRoleSuid роль пользователя
	 * @return возвращает сущность элемента подчиненности
	 */
	public List<SubordinationElementEntity> getSubElsByUserRole(Long aRoleSuid) {
		List<SubordinationElementEntity> result = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SubordinationElementEntity> q = cb
					.createQuery(SubordinationElementEntity.class);
			Root<SubordinationElementEntity> root = q.from(SubordinationElementEntity.class);
			ParameterExpression<Long> p = cb.parameter(Long.class);
			q.select(root).where(cb.equal(root.get("roleSuid"), p));
			TypedQuery<SubordinationElementEntity> query = em.createQuery(q);
			query.setParameter(p, Long.valueOf(aRoleSuid));
			result = query.getResultList();
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
		return result;
	}

}
