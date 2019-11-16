package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.HelpEntity;

import javax.persistence.EntityManager;

@Repository
public class HelpRepository extends AbstractRepository<HelpEntity> {

	private EntityManager em;

	public HelpRepository(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public Class<HelpEntity> getEntityClass() {
		return HelpEntity.class;
	}

}
