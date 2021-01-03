package ru.devazz.server.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import ru.devazz.server.api.model.enums.CycleTypeTask;
import ru.devazz.server.api.model.enums.TaskPriority;
import ru.devazz.server.api.model.enums.TaskStatus;
import ru.devazz.server.api.model.enums.TaskType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Модель задачи
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskModel implements Serializable, IEntity {

    /** идентификатор задачи */
    private Long suid;

    /** Наименование задачи */
    private String name;

    /** Примечание задачи */
    private String note;

    /** Описание задачи */
    private String description;

    /** Статус задачи */
    private TaskStatus status;

    /** Приоритет задачи */
    private TaskPriority priority;

    /** Дата начала */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /** Дата конца */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /** идентификатор должностного лица (автор задачи) */
    private Long authorSuid;

    /** идентификатор должностного лица (исполнитель задачи) */
    private Long executorSuid;

    /** Прикрепляемый документ */
    private byte[] document;

    /** Наименование документа */
    private String documentName;

    /** Тип задачи */
    private TaskType taskType;

    /** Тип циклического назначения */
    private CycleTypeTask cycleType;

    /** Время цикличного назначения (часы или дата) */
    private String cycleTime;

}
