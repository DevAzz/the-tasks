package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import ru.devazz.utils.SubElType;

/**
 * Модель регистрации пользователей
 */
public class RegistryModel extends PresentationModel<UserServiceRemote, UserEntity> {
	/** Список пользователей */
	private ObservableList<UserEntity> data;

	/** Список измененных пользователей */
	private ObservableList<UserEntity> changedUsers;

	/** Логин */
	private StringProperty login;

	/** Пароль */
	private StringProperty password;

	/** Имя */
	private StringProperty name;

	/** Фамилия */
	private StringProperty surname;

	/** Отчество */
	private StringProperty patronymic;

	/** Должность */
	private StringProperty position;

	/** Воинское звание */
	private StringProperty militaryRank;

	/** Свойство текста подразделения */
	private StringProperty subElsProperty;

	/** Выбранный элемент подчиненности */
	private SubElType selectedSubElType;

	/** Флаг наличия изменений */
	private BooleanProperty changeExistProperty;

	@Override
	protected void initModel() {
		data = FXCollections.observableArrayList();
		changedUsers = FXCollections.observableArrayList();

		login = new SimpleStringProperty(this, "login", "");
		password = new SimpleStringProperty(this, "password", "");
		name = new SimpleStringProperty(this, "name", "");
		surname = new SimpleStringProperty(this, "surname", "");
		patronymic = new SimpleStringProperty(this, "patronymic", "");
		position = new SimpleStringProperty(this, "position", "");
		militaryRank = new SimpleStringProperty(this, "militaryRank", "");
		subElsProperty = new SimpleStringProperty(this, "subElsProperty", "");
		changeExistProperty = new SimpleBooleanProperty(this, "changeExistProperty", false);
		loadUsers();
	}

	public void loadUsers() {
		changedUsers.clear();
		data.clear();
		data.addAll(service.getServiceUserList());
		setChangeExistProperty(false);

	}

	/**
	 * Возвращает {@link#login}
	 *
	 * @return the login
	 */
	public StringProperty getLogin() {
		return login;
	}

	/**
	 * Устанавливает значение полю login
	 *
	 * @param login значение поле
	 */
	public void setLogin(StringProperty login) {
		this.login = login;
	}

	/**
	 * Возвращает {@link#password}
	 *
	 * @return the password
	 */
	public StringProperty getPassword() {
		return password;
	}

	/**
	 * Устанавливает значение полю password
	 *
	 * @param password значение поле
	 */
	public void setPassword(StringProperty password) {
		this.password = password;
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the name
	 */
	public StringProperty getName() {
		return name;
	}

	/**
	 * Устанавливает значение полю name
	 *
	 * @param name значение поле
	 */
	public void setName(StringProperty name) {
		this.name = name;
	}

	/**
	 * Возвращает {@link#surname}
	 *
	 * @return the surname
	 */
	public StringProperty getSurname() {
		return surname;
	}

	/**
	 * Устанавливает значение полю surname
	 *
	 * @param surname значение поле
	 */
	public void setSurname(StringProperty surname) {
		this.surname = surname;
	}

	/**
	 * Возвращает {@link#patronymic}
	 *
	 * @return the patronymic
	 */
	public StringProperty getPatronymic() {
		return patronymic;
	}

	/**
	 * Устанавливает значение полю patronymic
	 *
	 * @param patronymic значение поле
	 */
	public void setPatronymic(StringProperty patronymic) {
		this.patronymic = patronymic;
	}

	/**
	 * Возвращает {@link#position}
	 *
	 * @return the position
	 */
	public StringProperty getPosition() {
		return position;
	}

	/**
	 * Устанавливает значение полю position
	 *
	 * @param position значение поле
	 */
	public void setPosition(StringProperty position) {
		this.position = position;
	}

	/**
	 * Возвращает {@link#militaryRank}
	 *
	 * @return the militaryRank
	 */
	public StringProperty getMilitaryRank() {
		return militaryRank;
	}

	/**
	 * Устанавливает значение полю militaryRank
	 *
	 * @param militaryRankValue значение поле
	 */
	public void setMilitaryRankValue(String militaryRankValue) {
		this.militaryRank.set(militaryRankValue);
	}

	/**
	 * Возвращает {@link#subElsProperty}
	 *
	 * @return the {@link#subElsProperty}
	 */
	public StringProperty getSubElsProperty() {
		return subElsProperty;
	}

	/**
	 * Устанавливает значение полю {@link#subElsProperty}
	 *
	 * @param subElsPropertyValue значение поля
	 */
	public void setSubElsPropertyValue(String subElsPropertyValue) {
		this.subElsProperty.set(subElsPropertyValue);
	}

	/**
	 * Возвращает {@link#selectedSubElType}
	 *
	 * @return the {@link#selectedSubElType}
	 */
	public SubElType getSelectedSubElType() {
		return selectedSubElType;
	}

	/**
	 * Устанавливает значение полю {@link#selectedSubElType}
	 *
	 * @param selectedSubElType значение поля
	 */
	public void setSelectedSubElType(SubElType selectedSubElType) {
		this.selectedSubElType = selectedSubElType;
	}

	/**
	 * Возвращает {@link#data}
	 *
	 * @return the {@link#data}
	 */
	public ObservableList<UserEntity> getData() {
		return data;
	}

	/**
	 * Устанавливает значение полю {@link#data}
	 *
	 * @param data значение поля
	 */
	public void setData(ObservableList<UserEntity> data) {
		this.data = data;
	}

	/**
	 * Возвращает {@link#changedUsers}
	 *
	 * @return the changedUsers
	 */
	public ObservableList<UserEntity> changedUsers() {
		return changedUsers;
	}

	/**
	 * Возвращает {@link#boolean}
	 *
	 * @return the changeExistProperty
	 */
	public BooleanProperty getChangeExistProperty() {
		return changeExistProperty;
	}

	/**
	 * Устанавливает значение {@link#changeExistProperty}
	 *
	 * @param changeExistProperty the changeExistProperty to set
	 */
	public void setChangeExistProperty(boolean exists) {
		this.changeExistProperty.set(exists);
	}

	/**
	 * Функция изменения флага Онлайн
	 *
	 * @param user сущность UserEntity
	 */
	public void changeOnline(UserEntity user) {
		if (null != user) {
			user.setOnline(!user.getOnline());
			DialogUtils.getInstance().showPushUp("Панель администратора",
					"Изменен статус у пользователя " + user.getName(), PushUpTypes.HELLO_PUSH,
					null);
			try {
				if (null == user.getImage()) {
					user.setImage(service.get(user.getSuid()).getImage());
				}
				service.update(user, false);
				user.setImage(null);
				System.gc();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Функция удаления пользователя из базы данных
	 *
	 * @param user - Сущность пользователя
	 */
	public void deleteUser(UserEntity user) {
		if (null != user) {
			try {
				service.delete(user.getSuid(), true);
				data.remove(user);
				DialogUtils.getInstance().showPushUp("Панель администратора",
						"Удален пользователь " + user.getName(), PushUpTypes.HELLO_PUSH, null);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addChangedUser(UserEntity user) {
		changedUsers.add(user);
		setChangeExistProperty(true);
	}

	/**
	 * Загружает данные в базу
	 *
	 * @throws Exception в случае ошибки
	 */
	public void loadInDB(byte[] image) throws Exception {
		SubordinatioElementServiceRemote subElService = EJBProxyFactory.getInstance()
				.getService(SubordinatioElementServiceRemote.class);
		SubordinationElementEntity subordinationElementEntity = subElService
				.get(selectedSubElType.getSubElSuid());
		UserEntity entity = new UserEntity();
		Long suid = (long) (Math.random() * 10000000L) + 1000000L;
		entity.setIduser(suid);
		entity.setLogin(getLogin().get());
		entity.setPassword(Utils.getInstance().sha(getPassword().get()));
		entity.setIdrole(subordinationElementEntity.getRoleSuid());
		entity.setPositionSuid(subordinationElementEntity.getSuid());
		entity.setName(getSurname().get() + " " + getName().get().charAt(0) + ". "
				+ getPatronymic().get().charAt(0) + ".");
		entity.setMilitaryRank(getMilitaryRank().get());
		entity.setPosition(getPosition().get());
		entity.setImage(image);
		entity.setOnline(false);

		getService().add(entity, false);
		loadUsers();

	}

	/**
	 * Обновление пользователей в бд
	 */
	public void loadUsersUpdates() {
		Thread thread = new Thread(() -> {
			for (UserEntity user : changedUsers) {
				try {
					if (null == user.getImage()) {
						user.setImage(service.getUserImage(user.getSuid()));
					}
					service.update(user, false);
					user.setImage(null);
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
					System.gc();
				}
			}
			DialogUtils.getInstance().showPushUp("Панель администратора",
					"Обновление данных завершено", PushUpTypes.HELLO_PUSH, null);
			System.gc();
			loadUsers();
			setChangeExistProperty(false);
		});
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public Class<UserServiceRemote> getTypeService() {
		return UserServiceRemote.class;
	}

}
