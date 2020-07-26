package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.PushUpTypes;
import ru.devazz.utils.SubElType;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;

/**
 * Модель регистрации пользователей
 */
public class RegistryModel extends PresentationModel<IUserService, UserModel> {

	/** Список пользователей */
	private ObservableList<UserModel> data;

	/** Список измененных пользователей */
	private ObservableList<UserModel> changedUsers;

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

	/** Свойство текста элемента подчиненности' */
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
		subElsProperty = new SimpleStringProperty(this, "subElsProperty", "");
		changeExistProperty = new SimpleBooleanProperty(this, "changeExistProperty", false);
		loadUsers();
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	private void loadUsers() {
		changedUsers.clear();
		data.clear();
		data.addAll(service.getServiceUserList());
		setChangeExistProperty(false);

	}

	public StringProperty getLogin() {
		return login;
	}

	public StringProperty getPassword() {
		return password;
	}

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

	public StringProperty getSurname() {
		return surname;
	}

	public StringProperty getPatronymic() {
		return patronymic;
	}

	public StringProperty getPosition() {
		return position;
	}

	public StringProperty getSubElsProperty() {
		return subElsProperty;
	}

	public void setSubElsPropertyValue(String subElsPropertyValue) {
		this.subElsProperty.set(subElsPropertyValue);
	}

	public void setSelectedSubElType(SubElType selectedSubElType) {
		this.selectedSubElType = selectedSubElType;
	}

	public ObservableList<UserModel> getData() {
		return data;
	}

	public BooleanProperty getChangeExistProperty() {
		return changeExistProperty;
	}

	private void setChangeExistProperty(boolean exists) {
		this.changeExistProperty.set(exists);
	}

	/**
	 * Функция изменения флага Онлайн
	 *
	 * @param user сущность UserEntity
	 */
	public void changeOnline(UserModel user) {
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
	public void deleteUser(UserModel user) {
		if (null != user) {
			service.delete(user.getSuid(), true);
			data.remove(user);
			DialogUtils.getInstance().showPushUp("Панель администратора",
					"Удален пользователь " + user.getName(), PushUpTypes.HELLO_PUSH, null);
		}
	}

	public void addChangedUser(UserModel user) {
		changedUsers.add(user);
		setChangeExistProperty(true);
	}

	/**
	 * Загружает данные в базу
	 *
	 * @throws Exception в случае ошибки
	 */
	public void loadInDB(byte[] image) throws Exception {
		ISubordinationElementService subElService = ProxyFactory.getInstance()
				.getService(ISubordinationElementService.class);
		SubordinationElementModel subordinationElementEntity = subElService
				.get(selectedSubElType.getSubElSuid());
		UserModel entity = new UserModel();
		Long suid = (long) (Math.random() * 10000000L) + 1000000L;
		entity.setSuid(suid);
		entity.setLogin(getLogin().get());
		entity.setPassword(Utils.getInstance().sha(getPassword().get()));
		entity.setIdRole(subordinationElementEntity.getRoleSuid());
		entity.setPositionSuid(subordinationElementEntity.getSuid());
		entity.setName(getSurname().get() + " " + getName().get().charAt(0) + ". "
				+ getPatronymic().get().charAt(0) + ".");
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
			for (UserModel user : changedUsers) {
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
	public Class<IUserService> getTypeService() {
		return IUserService.class;
	}

}
