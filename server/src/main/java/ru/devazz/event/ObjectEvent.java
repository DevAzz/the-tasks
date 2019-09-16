package ru.devazz.event;

import lombok.Data;
import ru.devazz.entity.IEntity;

import java.io.Serializable;

/**
 * Объект события, содержащий тему и сущность
 */
@Data
public abstract class ObjectEvent implements Serializable {

	/** Тип события */
	private String type;

	/** Сущность, к которой привязано событие */
	private IEntity entity;

}
