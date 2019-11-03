package ru.devazz.repository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.devazz.entity.IEntity;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Абстрактный репозиторий
 */
@Repository
public abstract class AbstractRepository<T extends IEntity> {

	/** Менеджер сущностей */
	@Autowired
	EntityManager em;

	/**
	 * Удаляет сущность
	 *
	 * @param aSuid идентификатор сущности
	 */
	public void delete(Long aSuid) {
		try {
			if (!em.getTransaction().isActive()) {
				em.getTransaction().begin();
				em.remove(get(aSuid));
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}

	}

	/**
	 * Поиск сущности по ее идентификатору
	 *
	 * @param aSuid идентификатор сущности
	 * @return возвращает сущность
	 */
	public T get(Long aSuid) {
		return em.find(getEntityClass(), aSuid);
	}

	/**
	 * Редактирует сущность
	 *
	 * @param aEntity сущность
	 */
	public void update(T aEntity) {
		try {
			if (!em.getTransaction().isActive()) {
				em.getTransaction().begin();
				em.merge(aEntity);
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
	}

	/**
	 * Добавляет сущность в базу данных
	 *
	 * @param aEntity сущность на основе которого создается запись в таблице
	 * @return добавленная сущность в случае успеха или {@code null} в случае ошибки
	 */
	public T add(T aEntity) {
		T entity = null;
		try {
			if (!em.getTransaction().isActive()) {
				em.clear();
				em.getTransaction().begin();
				entity = em.merge(aEntity);
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		}
		return entity;
	}

	/**
	 * Возвращает список сущностей
	 *
	 * @return список сущностей
	 */
	public List<T> getAll() {
		return em.createQuery("SELECT entity FROM " + getEntityClass().getSimpleName() + " entity",
							  getEntityClass())
				.getResultList();
	}

	/**
	 * Возвращает класс сущности
	 *
	 * @return класс
	 */
	public abstract Class<T> getEntityClass();

}
