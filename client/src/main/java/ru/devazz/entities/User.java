package ru.devazz.entities;

import java.io.File;

/**
 * Клиенсткая обертка над сущностью пользователя
 */
public class User {

	/** ФИО пользователя */
	private String name;

	/** Должность пользователя */
	private String position;

	/** Идентификатор пользователя */
	private Long suid;

	/** Идентификатор роли */
	private Long roleSuid;

	/** Идентификатор боевого поста */
	private Long subElementSuid;

	/** Портрет */
	private File image;

	/** Статус пользователя в системе */
	private Boolean online;

	/**
	 * Конструктор
	 */
	public User() {
		this.name = "";
	}

	/**
	 * Возвращает {@link#image}
	 *
	 * @return the {@link#image}
	 */
	public File getImage() {
		return image;
	}

	/**
	 * Устанавливает значение полю {@link#image}
	 *
	 * @param image значение поля
	 */
	public void setImage(File image) {
		this.image = image;
	}

	/**
	 * Возвращает {@link#position}
	 *
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * Устанавливает значение полю position
	 *
	 * @param position значение поле
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает значение полю name
	 *
	 * @param name значение поле
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#suid}
	 *
	 * @return the suid
	 */
	public Long getSuid() {
		return suid;
	}

	/**
	 * Устанавливает значение полю suid
	 *
	 * @param suid значение поле
	 */
	public void setSuid(Long suid) {
		this.suid = suid;
	}

	/**
	 * Возвращает {@link#roleSuid}
	 *
	 * @return the roleSuid
	 */
	public Long getRoleSuid() {
		return roleSuid;
	}

	/**
	 * Устанавливает значение полю roleSuid
	 *
	 * @param roleSuid значение поле
	 */
	public void setRoleSuid(Long roleSuid) {
		this.roleSuid = roleSuid;
	}

	/**
	 * Возвращает {@link#subElementSuid}
	 *
	 * @return the subElementSuid
	 */
	public Long getSubElementSuid() {
		return subElementSuid;
	}

	/**
	 * Устанавливает значение полю subElementSuid
	 *
	 * @param subElementSuid значение поле
	 */
	public void setSubElementSuid(Long subElementSuid) {
		this.subElementSuid = subElementSuid;
	}

	/**
	 * Билдер пользователя
	 */
	public class UserBuilder {

		/** Пользователь */
		private User user;

		/**
		 * Конструктор
		 */
		public UserBuilder(User aUser) {
			user = aUser;
		}

		/**
		 * Возвращает {@link#task}
		 *
		 * @return the {@link#task}
		 */
		public User toUser() {
			return user;
		}

		/**
		 * Добавляет идентификатор события
		 *
		 * @param aSuid идентификатор задачи
		 * @return билдер
		 */
		public UserBuilder addSuid(Long aSuid) {
			user.setSuid(aSuid);
			return this;
		}

		public UserBuilder addRoleSuid(Long aSuid) {
			user.setRoleSuid(aSuid);
			return this;
		}

		public UserBuilder addSubElementSuid(Long aSuid) {
			user.setSubElementSuid(aSuid);
			return this;
		}

		public UserBuilder addName(String aName) {
			user.setName(aName);
			return this;
		}

		public UserBuilder addPosition(String aPos) {
			user.setPosition(aPos);
			return this;
		}

		public UserBuilder addImage(File image) {
			user.setImage(image);
			return this;
		}

		public UserBuilder addOnline(Boolean aOnline) {
			user.setOnline(aOnline);
			return this;
		}

	}

	/**
	 * Возвращает построитель обертки над сущностью пользователя
	 *
	 * @return построитель
	 */
	public UserBuilder getBuilder() {
		return new UserBuilder(this);
	}

	/**
	 * Возвращает {@link#online}
	 *
	 * @return the {@link#online}
	 */
	public Boolean getOnline() {
		return online;
	}

	/**
	 * Устанавливает значение полю {@link#online}
	 *
	 * @param online значение поля
	 */
	public void setOnline(Boolean online) {
		this.online = online;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((image == null) ? 0 : image.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + ((position == null) ? 0 : position.hashCode());
		result = (prime * result) + ((roleSuid == null) ? 0 : roleSuid.hashCode());
		result = (prime * result) + ((subElementSuid == null) ? 0 : subElementSuid.hashCode());
		result = (prime * result) + ((suid == null) ? 0 : suid.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (image == null) {
			if (other.image != null) {
				return false;
			}
		} else if (!image.equals(other.image)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (roleSuid == null) {
			if (other.roleSuid != null) {
				return false;
			}
		} else if (!roleSuid.equals(other.roleSuid)) {
			return false;
		}
		if (subElementSuid == null) {
			if (other.subElementSuid != null) {
				return false;
			}
		} else if (!subElementSuid.equals(other.subElementSuid)) {
			return false;
		}
		if (suid == null) {
			if (other.suid != null) {
				return false;
			}
		} else if (!suid.equals(other.suid)) {
			return false;
		}
		return true;
	}

}
