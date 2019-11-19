package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.DefaultTaskEntity;
import ru.devazz.server.api.model.DefaultTaskModel;

@Component
public class DefaultTaskConverter implements IEntityConverter<DefaultTaskModel, DefaultTaskEntity> {
    @Override
    public DefaultTaskModel entityToModel(DefaultTaskEntity entity) {
        DefaultTaskModel model = null;
        if (null != entity) {
            model = new DefaultTaskModel();
            entity.setAuthorSuid(entity.getAuthorSuid());
            entity.setDefaultTaskID(entity.getDefaultTaskID());
            entity.setEndTime(entity.getEndTime());
            entity.setName(entity.getName());
            entity.setNextDay(entity.getNextDay());
            entity.setNote(entity.getNote());
            entity.setStartTime(entity.getStartTime());
            entity.setSubordinationSUID(entity.getSubordinationSUID());
        }
        return model;
    }

    @Override
    public DefaultTaskEntity modelToEntity(DefaultTaskModel model) {
        DefaultTaskEntity entity = null;
        if (null != model) {
            entity = new DefaultTaskEntity();
            entity.setAuthorSuid(model.getAuthorSuid());
            entity.setEndTime(model.getEndTime());
            entity.setName(model.getName());
            entity.setNextDay(model.getNextDay());
            entity.setNote(model.getNote());
            entity.setStartTime(model.getStartTime());
            entity.setSubordinationSUID(model.getSubordinationSUID());
        }
        return entity;
    }
}
