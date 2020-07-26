package ru.devazz.server.api.model;

import lombok.*;
import ru.devazz.server.api.model.enums.TaskHistoryType;

import java.io.Serializable;
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
    private Date date;
}
