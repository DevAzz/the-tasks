package ru.devazz.server.api.model;

import lombok.*;

import java.io.Serializable;

/**
 * Модель помощи
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HelpModel implements Serializable, IEntity{

    /** Идентификатор модели */
    private Long suid;

    /** Наименование пункта помощи */
    private String name;

    /** Текст пункта помощи */
    private String helpItemText;

    /** Наименование пункта помощи */
    private long role;
}
