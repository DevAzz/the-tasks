package ru.devazz.repository;

import lombok.AllArgsConstructor;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Абстрактный репозиторий
 */
@AllArgsConstructor
@Transactional
public abstract class AbstractRepository<T extends IEntity> {

    /**
     * Менеджер сущностей
     */
    private EntityManager em;

    /**
     * Удаляет сущность
     *
     * @param aSuid идентификатор сущности
     */
    @Transactional
    public void delete(Long aSuid) {
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                em.remove(get(aSuid));
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
    @Transactional
    public void update(T aEntity) {
        try {
            em.merge(aEntity);
        } catch (Exception e) {
            // TODO Логирование
            e.printStackTrace();
        }
    }

    /**
     * Добавляет сущность в базу данных
     *
     * @param aEntity сущность на основе которого создается запись в таблице
     * @return добавленная сущность в случае успеха или {@code null} в случае ошибки
     */
    @Transactional
    public T add(T aEntity) {
        T entity = null;
        try {
            em.clear();
            entity = em.merge(aEntity);
        } catch (Exception e) {
            e.printStackTrace();

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
