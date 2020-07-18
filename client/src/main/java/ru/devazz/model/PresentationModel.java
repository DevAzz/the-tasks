package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.IEntityService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.UserModel;
import ru.devazz.utils.Utils;

import javax.jms.MessageListener;
import java.util.List;
import java.util.UUID;

/**
 * Абстрактная модель представления
 */
public abstract class PresentationModel<T extends ICommonService, E extends IEntity> {

	private UUID viewModelId;

	/** Признак того, что представление открыто */
	private BooleanProperty openViewFlag;

	/** Сервис */
	protected T service;

	/** Список сущностей задач */
	ObservableList<E> listDataModelEntities;

	/**
	 * Инициализация модели
	 */
	protected abstract void initModel();

	/**
	 * Конструктор
	 */
	public PresentationModel() {
		viewModelId = UUID.randomUUID();
		openViewFlag = new SimpleBooleanProperty(this, "openViewFlag", false);

		listDataModelEntities = FXCollections
				.synchronizedObservableList(FXCollections.observableArrayList());

		try {
			service = getService();
			initModel();

		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	public void deleteJmsListener() {
		ProxyFactory.getInstance().deleteMessageListener(viewModelId.toString());
	}

	/**
	 * Подключение к службе рассылки системных JMS сообщений
	 */
	public void addJmsListener(String queueName, MessageListener listener) {
		ProxyFactory.getInstance()
				.addMessageListener(viewModelId.toString(), queueName, listener);
	}

	public BooleanProperty getOpenViewFlag() {
		return openViewFlag;
	}

	public void setOpenFlagValue(Boolean aFlag) {
		if ((null != aFlag) && (null != openViewFlag)) {
			openViewFlag.set(aFlag);
		}
	}

	/**
	 * Возвращает сервис работы с сущностями
	 *
	 * @return сервис работы с сущностями
	 */
	public T getService() {
		Class<T> type = getTypeService();
		if (null != type) {
			try {
				if (null == service) {
					service = ProxyFactory.getInstance().getService(getTypeService());
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		}
		return service;
	}

	/**
	 * Возвращает тип сервиса
	 *
	 * @return тип сервиса
	 */
	public abstract Class<T> getTypeService();

	/**
	 * Загружает записи
	 */
	public void loadEntities() {
		@SuppressWarnings("unchecked")
		IEntityService<E> enttiyService = (IEntityService<E>) service;
		UserModel user = Utils.getInstance().getCurrentUser();
		List<E> list = (null != service)
				? enttiyService.getAll((null != user) ? user.getSuid() : null)
				: null;
		if (null != list) {
			listDataModelEntities.clear();
			listDataModelEntities.addAll(FXCollections.observableArrayList(list));
		}
	}

	public ObservableList<E> getListEntities() {
		return listDataModelEntities;
	}

}
