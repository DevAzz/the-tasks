package ru.devazz.server.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Модель помощи
 */
@EqualsAndHashCode
@ToString
@Data
public class HelpModel implements IEntity{

    /** Идентификатор модели */
    private Long suid;

    /** Наименование пункта помощи */
    private String name;

    /** Текст пункта помощи */
    private String helpItemText;

    /** Наименование пункта помощи */
    private long role;
}
