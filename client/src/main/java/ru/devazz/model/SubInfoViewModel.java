package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.IMessageListener;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.IUserService;
import ru.devazz.server.api.event.ObjectEvent;
import ru.devazz.server.api.event.QueueNameEnum;
import ru.devazz.server.api.event.UserEvent;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.PushUpTypes;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;

import java.io.File;

/**
 * Модель представления информации о подразделении
 */
public class SubInfoViewModel
		extends PresentationModel<ISubordinationElementService, SubordinationElementModel> {

	/** Свойство текста заголовка */
	private StringProperty titleProperty;

	/** Свойство текста ФИО пользователя */
	private StringProperty fioProperty;

	/** Свойство текста должности пользователя */
	private StringProperty positionProperty;

	/** Выбранный элемент подчиненности */
	private SubordinationElement selectionSub;

	/** Свойство изображения пользователя */
	private ObjectProperty<Image> imageProperty;

	/** Свойство текста лейбла отображения состояния пользователя в системе */
	private StringProperty onlineProperty;

	/** Текущий пользователь */
	private UserModel userData = null;

	@Override
	protected void initModel() {
		titleProperty = new SimpleStringProperty(this, "titleProperty", "Заголовок");
		fioProperty = new SimpleStringProperty(this, "fioProperty", "fio");
		positionProperty = new SimpleStringProperty(this, "positionProperty", "position");
		imageProperty = new SimpleObjectProperty<>(this, "imageProperty", null);
		onlineProperty = new SimpleStringProperty(this, "positionProperty", "Не в сети");

		addJmsListener(getMessageListener());
	}

	private IMessageListener getMessageListener() {
		return event -> {
			IEntity entity = event.getEntity();
			if (event instanceof UserEvent) {
				userData = (UserModel) entity;
				SubordinationElementModel subEl = service
						.get(userData.getPositionSuid());
				if ((null != selectionSub)
					&& selectionSub.getSuid().equals(userData.getPositionSuid())) {
					setSelectionSub(EntityConverter.getInstatnce()
											.convertSubElEntityToClientWrap(subEl));
				}

				if (userData.getOnline() && !Utils.getInstance().getCurrentUser()
						.getSuid().equals(userData.getSuid())) {
					Platform.runLater(() -> {
						DialogUtils.getInstance().showPushUp(
								"Должностное лицо " + subEl.getName() + ".",
								"Пользователь " + userData.getName() + " в системе",
								PushUpTypes.USER_ONLINE_PUSH, null);
					});
				}
			}
		};
	}

	@Override
    protected String getQueueName() {
		return QueueNameEnum.SUB_EL_QUEUE;
	}

	public StringProperty getTitleProperty() {
		return titleProperty;
	}

	public StringProperty getFioProperty() {
		return fioProperty;
	}

	private void setFioValue(String fioProperty) {
		this.fioProperty.set(fioProperty);
	}

	public StringProperty getPositionProperty() {
		return positionProperty;
	}

	private void setTitleValue(String titleProperty) {
		this.titleProperty.set(titleProperty);
	}

	private void setPositionValue(String positionProperty) {
		this.positionProperty.set(positionProperty);
	}

	public SubordinationElement getSelectionSub() {
		return selectionSub;
	}

	public void setSelectionSub(SubordinationElement selectionSub) {
		if (null != selectionSub) {
			this.selectionSub = selectionSub;
			setTitleValue(selectionSub.getName());
			setParams(selectionSub.getSuid());
		}
	}

	/**
	 * Устанавливает параметры формы
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 */
	private void setParams(final Long aSubElSuid) {
		Thread thread = new Thread(() -> {
			String imageURI = "";
			byte[] imageArr = null;
			IUserService userService = ProxyFactory.getInstance()
					.getService(IUserService.class);
			userData = userService.getUserBySubElSuid(aSubElSuid);
			if (null == userData) {
				userData = new UserModel();
				userData.setName("ФИО");
				userData.setPosition("Должность");
			}
			File file = new File(Utils.getInstance().getUserImageName(userData.getSuid()));
			if (file.exists()) {
				imageURI = file.toURI().toString();
			} else {
				imageArr = userService.getUserImage(userData.getSuid());
				file = Utils.getInstance().createFileImage(imageArr, userData.getSuid());
				if (null != file) {
					imageURI = file.toURI().toString();
				}
			}
			file = null;
			imageArr = null;
			System.gc();

			final String imageFile = (imageURI.isEmpty()) ? "/css/user.png" : imageURI;
			Platform.runLater(() -> {
				setFioValue(userData.getName());
				setPositionValue(userData.getPosition());
				setOnlineValue((userData.getOnline()) ? "В сети" : "Не в сети");
				setImageValue(new Image(imageFile, 293, 289, true, false, true));
			});
		});
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public Class<ISubordinationElementService> getTypeService() {
		return ISubordinationElementService.class;
	}

	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}

	private void setImageValue(Image imageValue) {
		this.imageProperty.set(imageValue);
	}

	public StringProperty getOnlineProperty() {
		return onlineProperty;
	}

	private void setOnlineValue(String onlineValue) {
		this.onlineProperty.set(onlineValue);
	}

}
