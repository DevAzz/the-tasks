package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.EventEntity;
import ru.devazz.server.api.model.EventModel;

@Component
public class EventEntityConverter implements IEntityConverter<EventModel, EventEntity> {

    @Override
    public EventModel entityToModel(EventEntity entity) {
        EventModel model = null;
        if (null != entity) {
            model = new EventModel();
            model.setAuthorSuid(entity.getAuthorSuid());
            model.setDate(entity.getDate());
            model.setEventType(entity.getEventType());
            model.setExecutorSuid(entity.getExecutorSuid());
            model.setName(entity.getName());
            model.setSuid(entity.getSuid());
            model.setTaskSuid(entity.getTaskSuid());
        }
        return model;
    }

    @Override
    public EventEntity modelToEntity(EventModel model) {
        EventEntity eventEntity = null;
        if (null != model) {
            eventEntity = new EventEntity();
            eventEntity.setSuid(model.getSuid());
            eventEntity.setAuthorSuid(model.getAuthorSuid());
            eventEntity.setDate(model.getDate());
            eventEntity.setEventType(model.getEventType());
            eventEntity.setExecutorSuid(model.getExecutorSuid());
            eventEntity.setName(model.getName());
            eventEntity.setTaskSuid(model.getTaskSuid());
        }
        return eventEntity;
    }
}
