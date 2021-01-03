package ru.devazz.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Модель события
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventModel implements Serializable,  IEntity {

    /** Идентификатор события */
    private Long suid;

    /** Наименование события */
    private String name;

    /** Дата события */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    /** Тип события */
    private String eventType;

    /** Идентификатор должности автора */
    private Long authorSuid;

    /** Идентификатор должности исполнителя */
    private Long executorSuid;

    /** Идентификатор задачи */
    private Long taskSuid;
}
