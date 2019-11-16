package ru.devazz.server.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.devazz.server.api.model.enums.TaskHistoryType;

import java.util.Date;

@EqualsAndHashCode
@ToString
@Data
@Builder
public class TaskHistoryModel implements IEntity{

    private Long suid;

    /** Идентификатор задачи */
    private Long taskSuid;

    /** Тип задачи */
    private TaskHistoryType historyType;

    /** Заголовок */
    private String title;

    /** Основной текст */
    private String text;

    /** Идентификатор элетмента подчиненности */
    private Long actorSuid;

    /** Дата */
    private Date date;

    @Override
    public String getName() {
        return title;
    }
}
