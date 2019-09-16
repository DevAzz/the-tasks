package ru.devazz.repository;

import ru.devazz.entity.HelpEntity;

public class HelpRepository extends AbstractRepository<HelpEntity> {

	@Override
	public Class<HelpEntity> getEntityClass() {
		return HelpEntity.class;
	}

}
