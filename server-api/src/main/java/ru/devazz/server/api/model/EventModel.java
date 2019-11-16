package ru.devazz.server.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * Модель события
 */
@EqualsAndHashCode
@ToString
@Data
public class EventModel implements IEntity {

    /** Идентификатор события */
    private Long suid;

    /** Наименование события */
    private String name;

    /** Дата события */
    private Date date;

    /** Тип события */
    private String eventType;

    /** Идентификатор боевого поста */
    private Long authorSuid;

    /** Идентификатор боевого поста */
    private Long executorSuid;

    /** Идентификатор задачи */
    private Long taskSuid;
}
