package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.UserEntity;
import ru.siencesquad.hqtasks.ui.entities.User;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель представления книги должностных лиц
 */
public class PositionBookViewModel extends PresentationModel<UserServiceRemote, UserEntity> {

	/** Список пользователей */
	private ObservableList<User> data;

	/** Свойство списка пользователей */
	private ObjectProperty<ObservableList<User>> dataProperty;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		data = FXCollections.observableArrayList();
		dataProperty = new SimpleObjectProperty<>(data);

		loadEntities();
		data.addAll(createClientWrapUserList());
	}

	/**
	 * Создает клиентскую обертку над списком сущностей пользователей
	 *
	 * @return список пользователей
	 */
	private List<User> createClientWrapUserList() {
		List<User> result = new ArrayList<>();
		for (UserEntity entity : listDataModelEntities) {
			User user = EntityConverter.getInstatnce().convertUserEntitytoClientWrapUser(entity);
			result.add(user);
		}
		return result;
	}

	/**
	 * Возвращает {@link#data}
	 *
	 * @return the {@link#data}
	 */
	public ObservableList<User> getData() {
		return data;
	}

	/**
	 * Устанавливает значение полю {@link#data}
	 *
	 * @param data значение поля
	 */
	public void setData(ObservableList<User> data) {
		this.data = data;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<UserServiceRemote> getTypeService() {
		return UserServiceRemote.class;
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

	/**
	 * Возвращает {@link#dataProperty}
	 *
	 * @return the {@link#dataProperty}
	 */
	public ObjectProperty<ObservableList<User>> getDataProperty() {
		return dataProperty;
	}

}
