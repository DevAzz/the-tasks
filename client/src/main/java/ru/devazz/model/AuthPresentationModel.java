package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.UserEntity;
import ru.sciencesquad.hqtasks.server.exceptions.AuthorizeUserOnlineException;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.Utils;

/**
 * Модель представления авторизации
 */
public class AuthPresentationModel extends PresentationModel<UserServiceRemote, UserEntity> {

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

	public String getLogin() {
		return login.get();
	}

	public String getPassword() {
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

		login.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!authProcessFlag) {
				setEnterDisabled(newValue.isEmpty() || getPassword().isEmpty());
			}
		});
		password.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!authProcessFlag) {
				setEnterDisabled(newValue.isEmpty() || getLogin().isEmpty());
			}
		});
	}

	/**
	 * Авторизация пользователя
	 *
	 * @return {@code true} - в случае успеха
	 * @throws AuthorizeUserOnlineException в случае, если пользователь с заданным
	 *             идентификатором уже подключен к системе
	 */
	public boolean authorization() throws Exception {
		boolean result = false;
		UserEntity user = getService().checkUser(login.get(), password.get());
		if (null != user) {
			Utils.getInstance().setCurrentUser(user);
			MyShutdownHook shutdownHook = new MyShutdownHook(user.getSuid());
			Runtime.getRuntime().addShutdownHook(shutdownHook);
			result = true;
		}
		return result;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<UserServiceRemote> getTypeService() {
		return UserServiceRemote.class;
	}

	/**
	 * Возвращает {@link#authProcessFlag}
	 *
	 * @return the {@link#authProcessFlag}
	 */
	public Boolean getAuthProcessFlag() {
		return authProcessFlag;
	}

	/**
	 * Устанавливает значение полю {@link#authProcessFlag}
	 *
	 * @param authProcessFlag значение поля
	 */
	public void setAuthProcessFlag(Boolean authProcessFlag) {
		this.authProcessFlag = authProcessFlag;
	}

	/**
	 * Shutdown ловушка
	 */
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
				EJBProxyFactory.getInstance().getService(UserServiceRemote.class)
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
