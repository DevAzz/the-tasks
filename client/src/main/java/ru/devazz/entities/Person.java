package ru.devazz.entities;

public class Person {

	protected String fio;

	protected String position;

	public Person(String fio, String position) {
		this.fio = fio;
		this.position = position;
	}

	/**
	 * Возвращает {@link#fio}
	 *
	 * @return the {@link#fio}
	 */
	public String getFio() {
		return fio;
	}

	/**
	 * Устанавливает значение полю {@link#fio}
	 *
	 * @param fio значение поля
	 */
	public void setFio(String fio) {
		this.fio = fio;
	}

	/**
	 * Возвращает {@link#position}
	 *
	 * @return the {@link#position}
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * Устанавливает значение полю {@link#position}
	 *
	 * @param position значение поля
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	public void initPersonField() {

	}
}
