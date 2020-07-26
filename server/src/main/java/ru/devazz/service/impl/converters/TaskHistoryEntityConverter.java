package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.TaskHistoryEntity;
import ru.devazz.server.api.model.TaskHistoryModel;

@Component
public class TaskHistoryEntityConverter implements IEntityConverter<TaskHistoryModel, TaskHistoryEntity> {
    @Override
    public TaskHistoryModel entityToModel(TaskHistoryEntity entity) {
        TaskHistoryModel model = null;
        if (null != entity) {
            model = TaskHistoryModel.builder().build();
            model.setHistoryType(entity.getHistoryType());
            model.setText(entity.getText());
            model.setName(entity.getTitle());
            model.setActorSuid(entity.getActorSuid());
            model.setSuid(entity.getSuid());
            model.setDate(entity.getDate());
            model.setTaskSuid(entity.getTaskSuid());
        }
        return model;
    }

    @Override
    public TaskHistoryEntity modelToEntity(TaskHistoryModel model) {
        TaskHistoryEntity entity = null;
        if (null != model) {
            entity = TaskHistoryEntity.builder().build();
            entity.setSuid(model.getSuid());
            entity.setHistoryType(model.getHistoryType());
            entity.setText(model.getText());
            entity.setTitle(model.getName());
            entity.setActorSuid(model.getActorSuid());
            entity.setDate(model.getDate());
            entity.setTaskSuid(model.getTaskSuid());
        }
        return entity;
    }
}
