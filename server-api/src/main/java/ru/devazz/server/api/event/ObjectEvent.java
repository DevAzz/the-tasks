package ru.devazz.server.api.event;

import lombok.Data;
import ru.devazz.server.api.model.IEntity;

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
