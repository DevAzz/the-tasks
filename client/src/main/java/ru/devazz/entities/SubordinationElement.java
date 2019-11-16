package ru.devazz.entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.siencesquad.hqtasks.ui.interfaces.HierarchyData;
import ru.siencesquad.hqtasks.ui.interfaces.SelectableObject;

/**
 * Элемент дерева подчиненности
 */
public class SubordinationElement implements SelectableObject, HierarchyData<SubordinationElement> {

	/** Идентификатор подразделения */
	private Long suid;

	/** Имя */
	private String name;

	/** Идентификатор роли */
	private Long roleSuid;

	/** Подчиненные */
	private ObservableList<SubordinationElement> subElements = FXCollections.observableArrayList();

	/** Флаг корневого элемента */
	private Boolean rootElement;

	/**
	 * Конструктор
	 *
	 * @param name имя
	 */
	public SubordinationElement() {
		super();
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
	 * @param name значение поля
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Устанавливает значение полю {@link#subElements}
	 *
	 * @param subElements значение поля
	 */
	public SubordinationElement setSubElements(ObservableList<SubordinationElement> subElements) {
		this.subElements = subElements;
		return this;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Возвращает {@link#suid}
	 *
	 * @return the {@link#suid}
	 */
	public Long getSuid() {
		return suid;
	}

	/**
	 * Устанавливает значение полю {@link#suid}
	 *
	 * @param suid значение поля
	 */
	public void setSuid(Long suid) {
		this.suid = suid;
	}

	/**
	 * Возвращает {@link#roleSuid}
	 *
	 * @return the {@link#roleSuid}
	 */
	public Long getRoleSuid() {
		return roleSuid;
	}

	/**
	 * Устанавливает значение полю {@link#roleSuid}
	 *
	 * @param roleSuid значение поля
	 */
	public void setRoleSuid(Long roleSuid) {
		this.roleSuid = roleSuid;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + ((suid == null) ? 0 : suid.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SubordinationElement other = (SubordinationElement) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (suid == null) {
			if (other.suid != null) {
				return false;
			}
		} else if (!suid.equals(other.suid)) {
			return false;
		}
		return true;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.interfaces.HierarchyData#getChildren()
	 */
	@Override
	public ObservableList<SubordinationElement> getChildren() {
		return subElements;
	}

	/**
	 * Возвращает {@link#rootElement}
	 *
	 * @return the {@link#rootElement}
	 */
	public Boolean isRootElement() {
		return rootElement;
	}

	/**
	 * Устанавливает значение полю {@link#rootElement}
	 *
	 * @param rootElement значение поля
	 */
	public void setRootElement(Boolean rootElement) {
		this.rootElement = rootElement;
	}

	/**
	 * Копирует оригинальный элемент подчиненности
	 *
	 * @return копия
	 */
	public SubordinationElement copy() {
		SubordinationElement copy = new SubordinationElement();
		copy.setName(name);
		copy.setRootElement(rootElement);
		copy.setSubElements(subElements);
		copy.setSuid(suid);
		copy.setRoleSuid(roleSuid);
		return copy;
	}

}
