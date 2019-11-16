package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.TaskEntity;
import ru.devazz.server.api.model.TaskModel;

@Component
public class TaskEntityConverter implements IEntityConverter<TaskModel, TaskEntity> {
    @Override
    public TaskModel entityToModel(TaskEntity entity) {
        return null;
    }

    @Override
    public TaskEntity modelToEntity(TaskModel model) {
        return null;
    }
}
