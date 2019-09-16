package ru.devazz.repository;

import ru.devazz.entity.UserEntity;
import ru.devazz.entity.UserEntity_;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Репозиторий пользователей
 */
public class UserRepository extends AbstractRepository<UserEntity> {

	/**
	 * Ищет пользователя по логину и паролю
	 *
	 * @param aUsername логин
	 * @param aPassword пароль
	 * @return список пользователей или {@code null} в случае если пользователь не
	 *         был найден
	 */
	public UserEntity findByUserName(String aUsername, String aPassword) {
		em.clear();
		TypedQuery<UserEntity> namedQuery = em
				.createNamedQuery("UserEntity.checkUser", getEntityClass())
				.setParameter("login", aUsername).setParameter("password", aPassword);

		return namedQuery.getSingleResult();
	}

	/**
	 * Возвращает пользователя по идентификатору
	 *
	 * @param aSuid идентификатор ДЛ
	 * @return сущность пользователя
	 */
	public List<UserEntity> getUserWithoutImage(Long aSuid) {
		em.clear();
		TypedQuery<UserEntity> namedQuery = em
				.createNamedQuery("UserEntity.getUserWithoutImage", getEntityClass())
				.setParameter("iduser", aSuid);
		return namedQuery.getResultList();
	}

	/**
	 * Возвращает пользователя по идентификатору
	 *
	 * @param aSuid идентификатор боевого поста
	 * @return сущность польщователя
	 */
	public List<UserEntity> getUserBySubElSuid(Long aSuid) {
		em.clear();
		TypedQuery<UserEntity> namedQuery = em
				.createNamedQuery("UserEntity.getUserBySubElSuid", getEntityClass())
				.setParameter("subElSuid", aSuid);

		return namedQuery.getResultList();
	}

	public List<UserEntity> getServiceUserList() {
		em.clear();
		TypedQuery<UserEntity> namedQuery = em.createNamedQuery("UserEntity.getServiceUserList",
				getEntityClass());
		return namedQuery.getResultList();
	}

	/**
	 * Возвращает пользовательскую фотографию
	 *
	 * @param aUserSuid идентификатор пользователя
	 * @return пользовательская фотография
	 */
	public byte[] getUserImage(Long aUserSuid) {
		byte[] result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<byte[]> criteria = builder.createQuery(byte[].class);
		Root<UserEntity> personRoot = criteria.from(UserEntity.class);
		criteria.select(personRoot.get(UserEntity_.image));
		criteria.where(builder.equal(personRoot.get(UserEntity_.iduser), aUserSuid));
		result = em.createQuery(criteria).getSingleResult();
		return result;
	}

	/**
	 * Возвращает идентификатор пользователя в сети по идентификатору элемента
	 * подчиненности
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 * @return идентификатор пользователя
	 */
	public Long getUserSuidBySubElSuid(Long aSubElSuid) {
		Long result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<UserEntity> personRoot = criteria.from(UserEntity.class);
		try {
			criteria.select(personRoot.get(UserEntity_.iduser));
			criteria.where(
					builder.and(builder.equal(personRoot.get(UserEntity_.positionSuid), aSubElSuid),
							(builder.equal(personRoot.get(UserEntity_.online), true))));
			result = em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			criteria = builder.createQuery(Long.class);
			personRoot = criteria.from(UserEntity.class);
			criteria.select(personRoot.get(UserEntity_.iduser));
			criteria.where(builder.equal(personRoot.get(UserEntity_.positionSuid), aSubElSuid));
			List<Long> list = em.createQuery(criteria).getResultList();
			if ((null != list) && !list.isEmpty()) {
				result = list.get(0);
			}
		}
		return result;
	}

	/**
	 * Возвращает пользовательскую фотографию
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 * @return пользовательская фотография
	 */
	public byte[] getUserImageBySubElSuid(Long aSubElSuid) {
		byte[] result = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<byte[]> criteria = builder.createQuery(byte[].class);
		Root<UserEntity> personRoot = criteria.from(UserEntity.class);
		try {
			criteria.select(personRoot.get(UserEntity_.image));
			criteria.where(
					builder.and(builder.equal(personRoot.get(UserEntity_.positionSuid), aSubElSuid),
							(builder.equal(personRoot.get(UserEntity_.online), true))));
			result = em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			criteria = builder.createQuery(byte[].class);
			personRoot = criteria.from(UserEntity.class);
			criteria.select(personRoot.get(UserEntity_.image));
			criteria.where(builder.equal(personRoot.get(UserEntity_.positionSuid), aSubElSuid));
			List<byte[]> list = em.createQuery(criteria).getResultList();
			if ((null != list) && !list.isEmpty()) {
				result = list.get(0);
			}
		}
		return result;
	}

	@Override
	public Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}

}
