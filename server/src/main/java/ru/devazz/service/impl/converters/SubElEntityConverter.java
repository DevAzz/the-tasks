package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.server.api.model.SubordinationElementModel;

@Component
public class SubElEntityConverter implements IEntityConverter<SubordinationElementModel,
        SubordinationElementEntity> {
    @Override
    public SubordinationElementModel entityToModel(SubordinationElementEntity entity) {
        return null;
    }

    @Override
    public SubordinationElementEntity modelToEntity(SubordinationElementModel model) {
        return null;
    }
}
