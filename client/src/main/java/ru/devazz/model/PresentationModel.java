package ru.devazz.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.bean.IEntityService;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.UserEntity;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import java.util.List;

/**
 * Абстрактная модель представления
 */
public abstract class PresentationModel<T extends ICommonService, E extends IEntity> {

	/** Признак того, что представление открыто */
	private BooleanProperty openViewFlag;

	/** Сервис */
	protected T service;

	/** Список сущностей задач */
	protected ObservableList<E> listDataModelEntities;

	/**
	 * Инициализация модели
	 */
	protected abstract void initModel();

	/**
	 * Конструктор
	 */
	public PresentationModel() {
		openViewFlag = new SimpleBooleanProperty(this, "openViewFlag", false);

		if (null == listDataModelEntities) {
			listDataModelEntities = FXCollections
					.synchronizedObservableList(FXCollections.observableArrayList());
		}

		try {
			service = getService();
			initModel();

		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Возвращает {@link#openViewFlag}
	 *
	 * @return the {@link#openViewFlag}
	 */
	public BooleanProperty getOpenViewFlag() {
		return openViewFlag;
	}

	/**
	 * Устанавливает значение полю {@link#openViewFlag}
	 *
	 * @param aFlag значение поля
	 */
	public void setOpenFlagValue(Boolean aFlag) {
		if ((null != aFlag) && (null != openViewFlag)) {
			openViewFlag.set(aFlag);
		}
	}

	/**
	 * Устанавливает значение полю {@link#openViewFlag}
	 *
	 * @param openViewFlag значение поля
	 */
	public void setOpenViewFlag(BooleanProperty openViewFlag) {
		this.openViewFlag = openViewFlag;
	}

	/**
	 * Возвращает сервис работы с сущностями
	 *
	 * @return сервис работы с сущностями
	 * @exception Exception в случае ошибки получения сервиса
	 */
	public T getService() {
		Class<T> type = getTypeService();
		if (null != type) {
			try {
				if (null == service) {
					service = EJBProxyFactory.getInstance().getService(getTypeService());
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
		UserEntity user = Utils.getInstance().getCurrentUser();
		List<E> list = (null != service)
				? enttiyService.getAll((null != user) ? user.getSuid() : null)
				: null;
		if (null != list) {
			listDataModelEntities.clear();
			listDataModelEntities.addAll(FXCollections.observableArrayList(list));
		}
	}

	/**
	 * Возвращает {@link#listEntities}
	 *
	 * @return the {@link#listEntities}
	 */
	public ObservableList<E> getListEntities() {
		return listDataModelEntities;
	}

}
