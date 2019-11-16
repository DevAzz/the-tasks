package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.DefaultTaskEntity;
import ru.devazz.server.api.model.DefaultTaskModel;

@Component
public class DefaultTaskConverter implements IEntityConverter<DefaultTaskModel, DefaultTaskEntity> {
    @Override
    public DefaultTaskModel entityToModel(DefaultTaskEntity entity) {
        return null;
    }

    @Override
    public DefaultTaskEntity modelToEntity(DefaultTaskModel model) {
        return null;
    }
}
