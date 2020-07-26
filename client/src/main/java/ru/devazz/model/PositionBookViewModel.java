package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.User;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель представления книги должностных лиц
 */
public class PositionBookViewModel extends PresentationModel<IUserService, UserModel> {

	/** Список пользователей */
	private ObservableList<User> data;

	/** Свойство списка пользователей */
	private ObjectProperty<ObservableList<User>> dataProperty;

	@Override
	protected void initModel() {
		data = FXCollections.observableArrayList();
		dataProperty = new SimpleObjectProperty<>(data);

		loadEntities();
		data.addAll(createClientWrapUserList());
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	/**
	 * Создает клиентскую обертку над списком сущностей пользователей
	 *
	 * @return список пользователей
	 */
	private List<User> createClientWrapUserList() {
		List<User> result = new ArrayList<>();
		for (UserModel entity : listDataModelEntities) {
			User user = EntityConverter.getInstatnce().convertUserEntitytoClientWrapUser(entity);
			result.add(user);
		}
		return result;
	}

	@Override
	public Class<IUserService> getTypeService() {
		return IUserService.class;
	}

	/**
	 * Возвращает пользователя по его идентфикатору
	 *
	 * @param aUserSuid идентфикатор пользователя
	 * @return пользователь
	 */
	public User getUserBySuid(Long aUserSuid) {
		User result = null;
		for (User user : data) {
			if (user.getSuid().equals(aUserSuid)) {
				result = user;
			}
		}
		return result;
	}

	/**
	 * Возвращает должностных лиц по идентификатору элемента подчиненности
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 * @return список пользователей
	 */
	public List<User> getUsersBySubElSuid(Long aSubElSuid) {
		List<User> result = new ArrayList<>();
		for (User user : data) {
			if (user.getSubElementSuid().equals(aSubElSuid)) {
				result.add(user);
			}
		}
		return result;
	}

	/**
	 * Возвращает URI изображения
	 *
	 * @param aUserSuid идентификатор пользователя
	 * @return URI изображения
	 */
	public String getImageURI(Long aUserSuid) {
		String result = "";
		File file = new File(Utils.getInstance().getUserImageName(aUserSuid));
		if (!file.exists()) {
			byte[] imageArr = service.getUserImage(aUserSuid);
			file = Utils.getInstance().createFileImage(imageArr, aUserSuid);
			imageArr = null;
		}
		if (null != file) {
			result = file.toURI().toString();
			file = null;
			// Runtime.getRuntime().gc();
			// Runtime rt = Runtime.getRuntime();
			// try {
			// rt.exec("free -m");
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			System.gc();
		}
		return result;
	}

	public ObjectProperty<ObservableList<User>> getDataProperty() {
		return dataProperty;
	}

}
