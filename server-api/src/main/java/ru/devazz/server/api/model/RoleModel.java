package ru.devazz.server.api.model;

import lombok.*;

import java.io.Serializable;

/**
 * Модель роли
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel implements Serializable, IEntity{

    /** Идентификатор роли */
    private Long suid;

    /** Наименование задачи */
    private String name;

}
