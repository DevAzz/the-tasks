package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.TaskEntity;
import ru.devazz.server.api.model.TaskModel;

@Component
public class TaskEntityConverter implements IEntityConverter<TaskModel, TaskEntity> {
    @Override
    public TaskModel entityToModel(TaskEntity entity) {
        TaskModel model = null;
        if (null != entity) {
            model = TaskModel.builder().build();
            model.setSuid(entity.getSuid());
            model.setStatus(entity.getStatus());
            model.setDocument(entity.getDocument());
            model.setDocumentName(entity.getDocumentName());
            model.setEndDate(entity.getEndDate());
            model.setStartDate(entity.getStartDate());
            model.setAuthorSuid(entity.getAuthorSuid());
            model.setCycleTime(entity.getCycleTime());
            model.setCycleType(entity.getCycleType());
            model.setDescription(entity.getDescription());
            model.setExecutorSuid(entity.getExecutorSuid());
            model.setName(entity.getName());
            model.setNote(entity.getNote());
            model.setPriority(entity.getPriority());
            model.setTaskType(entity.getTaskType());
        }
        return model;
    }

    @Override
    public TaskEntity modelToEntity(TaskModel model) {
        TaskEntity entity = null;
        if (null != model) {
            entity = TaskEntity.builder().build();
            entity.setStatus(model.getStatus());
            entity.setDocument(model.getDocument());
            entity.setDocumentName(model.getDocumentName());
            entity.setEndDate(model.getEndDate());
            entity.setStartDate(model.getStartDate());
            entity.setAuthorSuid(model.getAuthorSuid());
            entity.setCycleTime(model.getCycleTime());
            entity.setCycleType(model.getCycleType());
            entity.setDescription(model.getDescription());
            entity.setExecutorSuid(model.getExecutorSuid());
            entity.setName(model.getName());
            entity.setNote(model.getNote());
            entity.setPriority(model.getPriority());
            entity.setTaskType(model.getTaskType());
        }
        return entity;
    }
}
