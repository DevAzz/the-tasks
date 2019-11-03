package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.RoleEntity;

/**
 * Репозитрий пользовательских ролей
 */
@Repository
public class RoleRepository extends AbstractRepository<RoleEntity> {

	@Override
	public Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

}
