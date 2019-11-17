package ru.devazz.entity;

import lombok.Data;
import org.hibernate.annotations.Type;
import ru.devazz.server.api.model.IEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность Пользователь
 */
@Data
@Entity
@Table(name = "user", schema = "tasksdb")
public class UserEntity implements Serializable, IEntity {

	/** Идентификатор пользователя */
	@Id
	@Column(name = "iduser")
	private Long iduser;

	/** Логин */
	@Column(name = "login", nullable = false, unique = true, columnDefinition = "varchar")
	private String login;

	/** Пароль */
	@Column(name = "password", nullable = false, columnDefinition = "varchar")
	private String password;

	/** Идентификатор роли */
	@Column(name = "idrole")
	private Long idrole;

	/** Идентификатор элемнета подчиненности */
	@Column(name = "sub_element_suid")
	private Long positionSuid;

	/** ФИО пользователя */
	@Column(name = "name", nullable = false, columnDefinition = "varchar")
	private String name;

	/** Должность пользователя */
	@Column(name = "position", nullable = false, columnDefinition = "varchar")
	private String position;

	/** Прикрепляемое изображение */
	@Column(name = "image", length = 100000000)
	@Lob
	private byte[] image;

	/** Флаг того, что пользователь находится в сети */
	@Column(name = "online", nullable = false, columnDefinition = "INT", length = 1)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean online;

	/**
	 * Конструктор
	 */
	public UserEntity() {
		super();
	}

	public UserEntity(Long iduser, Long idrole, Long positionSuid, String name,
			String position, boolean online) {
		super();
		this.iduser = iduser;
		this.idrole = idrole;
		this.positionSuid = positionSuid;
		this.name = name;
		this.position = position;
		this.online = online;
	}

	public UserEntity(Long iduser, String login, String password, Long idrole, Long positionSuid,
			String name, String position, Boolean online) {
		super();
		this.iduser = iduser;
		this.login = login;
		this.password = password;
		this.idrole = idrole;
		this.positionSuid = positionSuid;
		this.name = name;
		this.position = position;
		this.online = online;
	}

	public Long getSuid() {
		return getIduser();
	}
}
