package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.Utils;

/**
 * Модель представления авторизации
 */
public class AuthPresentationModel extends PresentationModel<IUserService, UserModel> {

	/** Логин */
	private StringProperty login;

	/** Пароль */
	private StringProperty password;

	/** Признак доступности кнопки входа */
	private BooleanProperty enterDisabled;

	/** Флаг процесса авторизации */
	private Boolean authProcessFlag = false;

	public void setEnterDisabled(Boolean aEnabled) {
		enterDisabled.set(aEnabled);
	}

	public Boolean getEnterDisabled() {
		return enterDisabled.getValue();
	}

	public BooleanProperty enterDisabledProperty() {
		return enterDisabled;
	}

	public void setLogin(String aLogin) {
		login.set(aLogin);
	}

	public StringProperty loginProperty() {
		return login;
	}

	public StringProperty passwordProperty() {
		return password;
	}

	private String getLogin() {
		return login.get();
	}

	private String getPassword() {
		return password.get();
	}

	/**
	 * Инициализирует модель
	 */
	@Override
	protected void initModel() {
		login = new SimpleStringProperty(this, "login", "");
		password = new SimpleStringProperty(this, "password", "");
		enterDisabled = new SimpleBooleanProperty(this, "enterEnabled", true);

		login.addListener((observable, oldValue, newValue) -> {
			if (!authProcessFlag) {
				setEnterDisabled(newValue.isEmpty() || getPassword().isEmpty());
			}
		});
		password.addListener((observable, oldValue, newValue) -> {
			if (!authProcessFlag) {
				setEnterDisabled(newValue.isEmpty() || getLogin().isEmpty());
			}
		});
	}

	@Override
	protected String getQueueName() {
		return QueueNameEnum.USERS_QUEUE;
	}

	/**
	 * Авторизация пользователя
	 *
	 * @return {@code true} - в случае успеха
	 * @throws Exception в случае, если пользователь с заданным
	 *             идентификатором уже подключен к системе
	 */
	public boolean authorization() throws Exception {
		boolean result = false;
		UserModel user = getService().checkUser(login.get(), password.get());
		if (null != user) {
			Utils.getInstance().setCurrentUser(user);
			MyShutdownHook shutdownHook = new MyShutdownHook(user.getSuid());
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			result = true;
		}
		return result;
	}

	@Override
	public Class<IUserService> getTypeService() {
		return IUserService.class;
	}

	public Boolean getAuthProcessFlag() {
		return authProcessFlag;
	}

	public void setAuthProcessFlag(Boolean authProcessFlag) {
		this.authProcessFlag = authProcessFlag;
	}

	private class MyShutdownHook extends Thread {

		/** Идентификатор пользователя */
		private Long userSuid;

		/**
		 * Конструктор
		 *
		 * @param userSuid
		 */
		public MyShutdownHook(Long userSuid) {
			super();
			this.userSuid = userSuid;
		}

		/**
		 * @see Thread#run()
		 */
		@Override
		public void run() {
			try {
				ProxyFactory.getInstance().getService(IUserService.class)
						.disableUser(userSuid);
				Thread.sleep(1000L);
				System.out.println("Сеанс завершен ");
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}

		}
	}

}
