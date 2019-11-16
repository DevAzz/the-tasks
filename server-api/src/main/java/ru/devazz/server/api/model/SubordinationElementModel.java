package ru.devazz.server.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Модель элмента подчиненности
 */
@EqualsAndHashCode
@ToString
@Data
public class SubordinationElementModel implements IEntity{

    /** Идентификатор модели элмента подчиненности */
    private Long suid;

    /** Наименование */
    private String name;

    /** Подчиненные */
    private List<SubordinationElementModel> subordinates;

    /** Идентификатор роли */
    private Long roleSuid;

    /** Флаг корневого элемента */
    private Boolean rootElement;

}
