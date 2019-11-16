package ru.devazz.interfaces;

/**
 * Слушатель выбора задачи
 */
public interface SelectionListener {

	/**
	 * Выполняет действия при селекшене
	 *
	 * @param aOjbect выделенный объект
	 */
	public void fireSelect(SelectableObject aOjbect);

}
