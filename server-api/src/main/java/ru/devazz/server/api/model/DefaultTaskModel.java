package ru.devazz.server.api.model;

import lombok.*;

import java.io.Serializable;

/**
 * Модель задачи по умолчанию
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultTaskModel implements Serializable, IEntity {

    /** Идентифтикатор задачи по умолчанию */
    private Long suid;

    /** Наименование */
    private String name;

    /** Примечание */
    private String note;

    /** Время начала задачи */
    private String startTime;

    /** Время конца задачи */
    private String endTime;

    /** идентификатор должности (исполнитель задачи) */

    private long subordinationSUID;

    /** Идентификатор автора типовой задачи */

    private long authorSuid;

    /** Флаг принадлежности задачи к следующему дню */
    private Boolean nextDay;
}

