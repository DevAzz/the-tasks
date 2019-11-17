package ru.devazz.utils;

import javafx.fxml.FXMLLoader;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.EJBProxyFactory;
import ru.devazz.server.api.IRoleService;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.model.RoleModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.server.api.model.enums.DayOfWeek;
import ru.devazz.server.api.model.enums.UserRoles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

	/** экземпляр класса */
	private static Utils instance;

	/** Сущность текщуего пользователя */
	private UserModel currentUser;

	/** Текущий элемент подчиненности */
	private SubordinationElement currentElement;

	/** URL подключения для подписчика на системные события */
	private String connectionURL;

	/** Имя пользователя на сервере приложений */
	private String serverConnectionUser;

	/** Пароль пользователя на сервере приложений */
	private String serverConnectionPassword;

	/**
	 * Конструктор
	 *
	 */
	private Utils() {
	}

	/**
	 * Возвращает экземпляр класса Utils
	 *
	 * @return экземпляр класса Utils
	 */
	public static Utils getInstance() {
		if (null == instance) {
			instance = new Utils();
		}
		return instance;
	}

	/**
	 * Инициализация представления
	 *
	 * @param <T> тип представления
	 * @param aType объект типа представления
	 * @return инициализированное представление
	 * @throws IOException в случае ошибки загрузки FXML файла
	 */
	public <T> T loadView(Class<T> aType) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(aType.getResource(ViewType.getInstance().getFilePath(aType)));
		loader.load();
		T view = loader.getController();
		return view;
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За день"
	 *
	 * @return дата начала отсчета
	 */
	public Date getStartDateForFilterDate() {
		GregorianCalendar calendarStart = new GregorianCalendar();
		calendarStart.set(Calendar.AM_PM, Calendar.AM);
		calendarStart.set(Calendar.HOUR, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За день"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterDate() {
		GregorianCalendar calendarEnd = new GregorianCalendar();
		calendarEnd.set(Calendar.AM_PM, Calendar.PM);
		calendarEnd.set(Calendar.HOUR, 11);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За неделю"
	 *
	 * @return дата начала отсчета
	 */
	public Date getStartDateForFilterWeek() {
		GregorianCalendar calendarStart = new GregorianCalendar();
		calendarStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendarStart.set(Calendar.AM_PM, Calendar.AM);
		calendarStart.set(Calendar.HOUR, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За неделю"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterWeek() {
		GregorianCalendar calendarEnd = new GregorianCalendar();
		calendarEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendarEnd.set(Calendar.AM_PM, Calendar.PM);
		calendarEnd.set(Calendar.HOUR, 11);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
	}

	/**
	 * Возвращает дату начала отсчета для фильтра "За месяц"
	 *
	 * @return дата начала отсчета
	 */
	public Date getStartDateForFilterMonth() {
		GregorianCalendar commonCalendar = new GregorianCalendar();
		GregorianCalendar calendarStart = new GregorianCalendar(commonCalendar.get(Calendar.YEAR),
				commonCalendar.get(Calendar.MONTH), 1, 0, 0, 0);
		Date dateStart = calendarStart.getTime();
		return dateStart;
	}

	/**
	 * Возвращает дату конца отсчета для фильтра "За месяц"
	 *
	 * @return дата конца отсчета
	 */
	public Date getEndDateForFilterMonth() {
		GregorianCalendar commonCalendar = new GregorianCalendar();
		GregorianCalendar calendarEnd = new GregorianCalendar(commonCalendar.get(Calendar.YEAR),
				commonCalendar.get(Calendar.MONTH),
				commonCalendar.getMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
		Date dateEnd = calendarEnd.getTime();
		return dateEnd;
	}

	/**
	 * Определяет ОС и возвращает временную папку системы
	 *
	 * @return путь к TEMP
	 */
	public String getTempDir() {
		String systemName = System.getProperty("os.name").toLowerCase();
		if (systemName.indexOf("win") != -1) {
			return System.getProperty("java.io.tmpdir");
		} else {
			return "/tmp/";
		}
	}

	/**
	 * Возвращает {@link#subscriberURL}
	 *
	 * @return the {@link#subscriberURL}
	 */
	public String getConnectionURL() {
		return connectionURL;
	}

	/**
	 * Устанавливает значение полю {@link#subscriberURL}
	 *
	 * @param subscriberURL значение поля
	 */
	public void setConnectionURL(String subscriberURL) {
		this.connectionURL = subscriberURL;
	}

	/**
	 * Возвращает {@link#serverConnectionUser}
	 *
	 * @return the {@link#serverConnectionUser}
	 */
	public String getServerConnectionUser() {
		return serverConnectionUser;
	}

	/**
	 * Устанавливает значение полю {@link#serverConnectionUser}
	 *
	 * @param serverConnectionUser значение поля
	 */
	public void setServerConnectionUser(String serverConnectionUser) {
		this.serverConnectionUser = serverConnectionUser;
	}

	/**
	 * Возвращает {@link#serverConnectionPassword}
	 *
	 * @return the {@link#serverConnectionPassword}
	 */
	public String getServerConnectionPassword() {
		return serverConnectionPassword;
	}

	/**
	 * Устанавливает значение полю {@link#serverConnectionPassword}
	 *
	 * @param serverConnectionPassword значение поля
	 */
	public void setServerConnectionPassword(String serverConnectionPassword) {
		this.serverConnectionPassword = serverConnectionPassword;
	}

	/**
	 * Конвертирует массив байт строки в Base64
	 *
	 * @param data ковертируемая строка
	 * @return сконвертированная строка
	 */
	public String toBase64(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	/**
	 * Конвертирует Base64 строку в обычный текст
	 *
	 * @param data Base54 строка
	 * @return сконвертированный текст
	 * @exception IllegalArgumentException в случае, если строка является обычным
	 *                текстом
	 */
	public String fromBase64(String data) {
		String result = "";
		if (null != data) {
			try {
				result = new String(Base64.getDecoder().decode(data));
			} catch (IllegalArgumentException e) {
				result = data;
			}
		}
		return result;
	}

	/**
	 * Проверяет привилегии текущего пользователя на соответствие заданной роли
	 *
	 * @param aRole роль
	 * @return {@code true} - если текущий пользователь соответствует роли
	 */
	public boolean checkUserAccess(UserRoles aRole){
		IRoleService roleService = EJBProxyFactory.getInstance()
				.getService(IRoleService.class);
		return roleService.checkUserPrivilege(aRole, currentUser.getSuid());
	}

	/**
	 * Проверяет роль пользователя на соответствие заданной роли
	 *
	 * @param aRole заданная роль
	 * @return {@code true} - если заданная роль соответствует роли текущего
	 *         пользователя
	 */
	public boolean checkRoleEquals(UserRoles aRole){
		Boolean result = false;
		if (null != currentUser) {
			IRoleService roleService = EJBProxyFactory.getInstance()
					.getService(IRoleService.class);
			RoleModel role = roleService.get(currentUser.getIdRole());
			UserRoles currentUserRole = UserRoles.getUserRoleByName(role.getName());
			result = currentUserRole.equals(aRole);
		}
		return result;
	}

	/**
	 * Возвращает {@link#currentUser}
	 *
	 * @return the {@link#currentUser}
	 */
	public UserModel getCurrentUser() {
		return currentUser;
	}

	/**
	 * Устанавливает значение полю {@link#currentUser}
	 *
	 * @param currentUser значение поля
	 */
	public void setCurrentUser(UserModel currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Функция определяющая разрешенный или нет тип файла, для его загрузки в БД
	 *
	 * @param name - полное имя файла
	 * @return - true/false
	 */
	public boolean isCorrectFileType(String name) {
		String[] incorrectTypes = { "exe", "bat", "com", "vbs", "msc", "sh", "bash", "py", "pyc",
				"reg", "msi", "msc", "ps", "ps1", "psm1", "psd1", "ps1xml", "jar" };
		String fileType = getFileType(name);
		for (String incorrect : incorrectTypes) {
			if (fileType.equals(incorrect)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Функция определяющая расширение файла из полного имени файла
	 *
	 * @param name - полное имя файла
	 * @return расширение файла
	 */
	public String getFileType(String name) {
		char dot = '.';
		int dotPosition = -1;
		String fileType = "";
		for (int i = name.length() - 1; i > 0; i--) {
			if (name.charAt(i) == dot) {
				dotPosition = i;
				break;
			}
		}
		for (int i = dotPosition + 1; i < name.length(); i++) {
			fileType += name.charAt(i);
		}
		return fileType;
	}

	/**
	 * Обновляет флаг нахождения пользователя в сети
	 *
	 * @param aOnlineFlag флаг нахождения пользоователя в сети
	 * @throws Exception в случае ошибки
	 */
	public void updateUserOnlineFlag(Boolean aOnlineFlag) {
		new Thread(() -> {
			if ((null != aOnlineFlag) && (null != currentUser)) {
				currentUser.setOnline(aOnlineFlag);
				try {
					EJBProxyFactory.getInstance().getService(IUserService.class)
							.update(currentUser, true);
				} catch (Exception e) {
					// TODO Логирование
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * Возвращает элемент подчиненности текущего пользователя
	 *
	 * @return элемент подчиненности текущего пользователя
	 */
	public SubordinationElement getCurrentUserSubEl() {
		if (null == currentElement) {
			if (null != currentUser) {
				ISubordinationElementService service = EJBProxyFactory.getInstance()
						.getService(ISubordinationElementService.class);
				currentElement = EntityConverter.getInstatnce()
						.convertSubElEntityToClientWrap(service.get(currentUser.getPositionSuid()));
			}
		}
		return currentElement;
	}

	/**
	 * Функция хеш суммы SHA1
	 *
	 * @param input - входные данные
	 * @return sha1 хеш от input
	 * @throws NoSuchAlgorithmException
	 */
	public String sha(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (byte element : result) {
			sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Обрезает заданную строку
	 *
	 * @param aData обрабатываемая сторка
	 * @param symbolsCount количество символов в результирующей строке
	 * @return обрезанная строка
	 */
	public String ellipsString(String aData, int symbolsCount) {
		String result = aData;
		if ((null != aData) && (symbolsCount <= aData.length())) {
			result = aData.substring(0, symbolsCount - 3) + "...";
		}
		return result;
	}

	/**
	 * Создает файл с изображением
	 *
	 * @param aImageArr массив байт пользовательского изображения
	 * @return файл изображения
	 */
	public File createFileImage(byte[] aImageArr, Long aSubElSuid) {
		File file = null;
		if (null != aImageArr) {
			String path = getUserImageName(aSubElSuid);
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			try (FileOutputStream fos = new FileOutputStream(path)) {
				byte[] buffer = aImageArr;
				if (null != buffer) {
					fos.write(buffer, 0, buffer.length);
					file = new File(path);
				}
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return file;
	}

	/**
	 * Возвращает наименование файла пользовательской фотографии
	 *
	 * @param aSuid идентификатор
	 * @return наименование файла пользовательской фотографии
	 */
	public String getUserImageName(Long aSuid) {
		String result = getTempDir() + aSuid + ".png";
		return result;
	}

	/**
	 * Возвращает день недели по дате
	 *
	 * @param aDate дата
	 * @return день недели
	 */
	public DayOfWeek getDayOfWeekByDate(Date aDate) {
		DayOfWeek result = null;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(aDate);
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			result = DayOfWeek.MONDAY;
			break;
		case Calendar.TUESDAY:
			result = DayOfWeek.TUESDAY;
			break;
		case Calendar.WEDNESDAY:
			result = DayOfWeek.WEDNESDAY;
			break;
		case Calendar.THURSDAY:
			result = DayOfWeek.THURSDAY;
			break;
		case Calendar.FRIDAY:
			result = DayOfWeek.FRIDAY;
			break;
		case Calendar.SATURDAY:
			result = DayOfWeek.SATURDAY;
			break;
		case Calendar.SUNDAY:
			result = DayOfWeek.SUNDAY;
			break;
		default:
			break;
		}
		return result;
	}

}
