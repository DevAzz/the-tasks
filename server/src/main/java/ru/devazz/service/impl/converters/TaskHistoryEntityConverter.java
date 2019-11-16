package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.TaskHistoryEntity;
import ru.devazz.server.api.model.TaskHistoryModel;

@Component
public class TaskHistoryEntityConverter implements IEntityConverter<TaskHistoryModel, TaskHistoryEntity> {
    @Override
    public TaskHistoryModel entityToModel(TaskHistoryEntity entity) {
        return null;
    }

    @Override
    public TaskHistoryEntity modelToEntity(TaskHistoryModel model) {
        return null;
    }
}
