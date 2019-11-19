package ru.devazz.repository;

import org.springframework.stereotype.Repository;
import ru.devazz.entity.UserEntity;
import ru.devazz.entity.UserEntity_;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Репозиторий пользователей
 */
@Repository
@Transactional
public class UserRepository extends AbstractRepository<UserEntity> {

	private EntityManager em;

	public UserRepository(EntityManager em) {
		super(em);
		this.em = em;
	}

	/**
	 * Ищет пользователя по логину и паролю
	 *
	 * @param aUsername логин
	 * @param aPassword пароль
	 * @return список пользователей или {@code null} в случае если пользователь не
	 *         был найден
	 */
	public UserEntity findByUserName(String aUsername, String aPassword) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.select(root).where(builder.and(builder.equal(root.get(UserEntity_.login),
														   aUsername),
											 builder.equal(root.get(UserEntity_.password),
														   aPassword)));
		return em.createQuery(query).getSingleResult();
	}

	/**
	 * Возвращает пользователя по идентификатору
	 *
	 * @param aSuid идентификатор ДЛ
	 * @return сущность пользователя
	 */
	public List<UserEntity> getUserWithoutImage(Long aSuid) {
		em.clear();
		Query namedQuery = em.createQuery("SELECT NEW ru.devazz.entity.UserEntity(c.iduser,c" +
										  ".login, c.password, c.idrole, c.positionSuid, c.name, c.position, c.online) FROM UserEntity c WHERE c.iduser = :iduser")
				.setParameter("iduser", aSuid);
		return namedQuery.getResultList();
	}

	/**
	 * Возвращает пользователя по идентификатору
	 *
	 * @param aSuid идентификатор элемента подчиненности
	 * @return сущность польщователя
	 */
	public List<UserEntity> getUserBySubElSuid(Long aSuid) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> query = builder.createQuery(UserEntity.class);
		Root<UserEntity> root = query.from(UserEntity.class);
		query.select(root).where(builder.equal(root.get(UserEntity_.positionSuid), aSuid));
		return em.createQuery(query).getResultList();
	}

	public List<UserEntity> getServiceUserList() {
		TypedQuery<UserEntity> namedQuery = em.createQuery("SELECT NEW ru.devazz.entity.UserEntity(c" +
														   ".iduser,c.login, c.password, c" +
														   ".idrole, c.positionSuid, c.name, c.position, c.online) " +
														   "FROM UserEntity c", UserEntity.class);
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
