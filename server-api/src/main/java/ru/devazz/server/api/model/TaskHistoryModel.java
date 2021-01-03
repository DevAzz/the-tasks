package ru.devazz.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import ru.devazz.server.api.model.enums.TaskHistoryType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskHistoryModel implements Serializable, IEntity{

    private Long suid;

    /** Идентификатор задачи */
    private Long taskSuid;

    /** Тип задачи */
    private TaskHistoryType historyType;

    /** Заголовок */
    private String name;

    /** Основной текст */
    private String text;

    /** Идентификатор элетмента подчиненности */
    private Long actorSuid;

    /** Дата */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
}
