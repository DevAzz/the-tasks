package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.HelpEntity;

@Repository
public class HelpRepository extends AbstractRepository<HelpEntity> {

	@Override
	public Class<HelpEntity> getEntityClass() {
		return HelpEntity.class;
	}

}
