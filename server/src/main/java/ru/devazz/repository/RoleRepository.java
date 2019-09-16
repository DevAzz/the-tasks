package ru.devazz.repository;

import ru.devazz.entity.RoleEntity;

/**
 * Репозитрий пользовательских ролей
 */
public class RoleRepository extends AbstractRepository<RoleEntity> {

	@Override
	public Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

}
