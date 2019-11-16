package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.RoleEntity;

import javax.persistence.EntityManager;

/**
 * Репозитрий пользовательских ролей
 */
@Repository
public class RoleRepository extends AbstractRepository<RoleEntity> {

	private EntityManager em;

	public RoleRepository(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

}
