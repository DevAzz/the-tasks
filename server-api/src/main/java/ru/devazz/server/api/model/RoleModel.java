package ru.devazz.server.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Модель роли
 */
@EqualsAndHashCode
@ToString
@Data
public class RoleModel implements Serializable, IEntity{

    /** Идентификатор роли */
    private Long suid;

    /** Наименование задачи */
    private String name;

}
