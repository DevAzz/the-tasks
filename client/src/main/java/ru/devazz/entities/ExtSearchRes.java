package ru.devazz.entities;

import ru.sciencesquad.hqtasks.server.datamodel.IEntity;

/**
 * Сущность результат поиска
 */
public class ExtSearchRes {

	/** Текст панели результата */
	private String name;

	/** Сущность */
	private IEntity entity;

	/**
	 * Конструктор
	 *
	 * @param aName наименование
	 */
	public ExtSearchRes(String aName, IEntity aEntity) {
		this.name = aName;
		setEntity(aEntity);
	}

	/**
	 * Возвращает {@link#name}
	 *
	 * @return the {@link#name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает значение полю {@link#name}
	 *
	 * @param aName значение поля
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * Возвращает {@link#entity}
	 *
	 * @return the {@link#entity}
	 */
	public IEntity getEntity() {
		return entity;
	}

	/**
	 * Устанавливает значение полю {@link#entity}
	 *
	 * @param entity значение поля
	 */
	public void setEntity(IEntity entity) {
		this.entity = entity;
	}

}
