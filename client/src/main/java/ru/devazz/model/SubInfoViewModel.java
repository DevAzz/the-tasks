package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.apache.activemq.artemis.jms.client.ActiveMQMessage;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.SubordinationElementEntity;
import ru.sciencesquad.hqtasks.server.datamodel.UserEntity;
import ru.sciencesquad.hqtasks.server.events.ObjectEvent;
import ru.sciencesquad.hqtasks.server.events.UserEvent;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.PushUpTypes;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

import javax.naming.NamingException;
import java.io.File;

/**
 * Модель представления информации о подразделении
 */
public class SubInfoViewModel
		extends PresentationModel<SubordinatioElementServiceRemote, SubordinationElementEntity> {

	/** Свойство текста заголовка */
	private StringProperty titleProperty;

	/** Свойство текста ФИО пользователя */
	private StringProperty fioProperty;

	/** Свойство текста звания пользователя */
	private StringProperty rankProperty;

	/** Свойство текста должности пользователя */
	private StringProperty positionProperty;

	/** Выбранное подразделение */
	private SubordinationElement selectionSub;

	/** Свойство изображения пользователя */
	private ObjectProperty<Image> imageProperty;

	/** Свойство текста лейбла отображения состояния пользователя в системе */
	private StringProperty onlineProperty;

	/** Текущий пользователь */
	private UserEntity userData = null;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		titleProperty = new SimpleStringProperty(this, "titleProperty", "Заголовок");
		fioProperty = new SimpleStringProperty(this, "fioProperty", "fio");
		rankProperty = new SimpleStringProperty(this, "rankProperty", "rank");
		positionProperty = new SimpleStringProperty(this, "positionProperty", "position");
		imageProperty = new SimpleObjectProperty<>(this, "imageProperty", null);
		onlineProperty = new SimpleStringProperty(this, "positionProperty", "Не в сети");

		EJBProxyFactory.getInstance().addMessageListener(message -> {
			try {
				if (message instanceof ActiveMQMessage) {
					ActiveMQMessage objectMessage = (ActiveMQMessage) message;
					if (objectMessage.isBodyAssignableTo(ObjectEvent.class)) {
						ObjectEvent event = objectMessage.getBody(ObjectEvent.class);
						IEntity entity = event.getEntity();
						if (event instanceof UserEvent) {
							userData = (UserEntity) entity;
							SubordinationElementEntity subEl = service
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
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO Логирование
			}
		});
	}

	/**
	 * Возвращает {@link #titleProperty}
	 *
	 * @return {@link #titleProperty}
	 */
	public StringProperty getTitleProperty() {
		return titleProperty;
	}

	/**
	 * Возвращает {@link#fioProperty}
	 *
	 * @return the fioProperty
	 */
	public StringProperty getFioProperty() {
		return fioProperty;
	}

	/**
	 * Устанавливает значение полю fioProperty
	 *
	 * @param fioProperty значение поле
	 */
	public void setFioValue(String fioProperty) {
		this.fioProperty.set(fioProperty);
	}

	/**
	 * Возвращает {@link#rankProperty}
	 *
	 * @return the rankProperty
	 */
	public StringProperty getRankProperty() {
		return rankProperty;
	}

	/**
	 * Устанавливает значение полю rankProperty
	 *
	 * @param rankProperty значение поле
	 */
	public void setRankValue(String rankProperty) {
		this.rankProperty.set(rankProperty);
	}

	/**
	 * Возвращает {@link#positionProperty}
	 *
	 * @return the positionProperty
	 */
	public StringProperty getPositionProperty() {
		return positionProperty;
	}

	/**
	 * Устанавливает значение полю {@link#titleProperty}
	 *
	 * @param titleProperty значение поля
	 */
	public void setTitleValue(String titleProperty) {
		this.titleProperty.set(titleProperty);
	}

	/**
	 * Устанавливает значение полю {@link#positionProperty}
	 *
	 * @param positionProperty значение поля
	 */
	public void setPositionValue(String positionProperty) {
		this.positionProperty.set(positionProperty);
	}

	/**
	 * Возвращает {@link #selectionSub}
	 *
	 * @return {@link #selectionSub}
	 */
	public SubordinationElement getSelectionSub() {
		return selectionSub;
	}

	/**
	 * Устанавливает значение полю {@link#selectionSub}
	 *
	 * @param selectionSub значение поля
	 */
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
			try {
				UserServiceRemote userService = EJBProxyFactory.getInstance()
						.getService(UserServiceRemote.class);
				userData = userService.getUserBySubElSuid(aSubElSuid);
				if (null == userData) {
					userData = new UserEntity();
					userData.setName("ФИО");
					userData.setMilitaryRank("Звание");
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
					setRankValue(userData.getMilitaryRank());
					setPositionValue(userData.getPosition());
					setOnlineValue((userData.getOnline()) ? "В сети" : "Не в сети");
					setImageValue(new Image(imageFile, 293, 289, true, false, true));
				});
			} catch (NamingException e) {
				Platform.runLater(() -> {
					setFioValue("ФИО " + selectionSub.getName());
					setRankValue("Звание " + selectionSub.getName());
					setPositionValue("Должность " + selectionSub.getName());
					setImageValue(new Image("/css/user.png"));
				});
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<SubordinatioElementServiceRemote> getTypeService() {
		return SubordinatioElementServiceRemote.class;
	}

	/**
	 * Возвращает {@link#imageProperty}
	 *
	 * @return the {@link#imageProperty}
	 */
	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}

	/**
	 * Устанавливает значение полю {@link#imageProperty}
	 *
	 * @param imageValue значение поля
	 */
	public void setImageValue(Image imageValue) {
		this.imageProperty.set(imageValue);
	}

	/**
	 * Возвращает {@link#onlineProperty}
	 *
	 * @return the {@link#onlineProperty}
	 */
	public StringProperty getOnlineProperty() {
		return onlineProperty;
	}

	/**
	 * Устанавливает значение полю {@link#onlineProperty}
	 *
	 * @param onlineValue значение поля
	 */
	public void setOnlineValue(String onlineValue) {
		this.onlineProperty.set(onlineValue);
	}

}
