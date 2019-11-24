package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.SubordinationElementEntity;
import ru.devazz.server.api.model.SubordinationElementModel;

import java.util.stream.Collectors;

@Component
public class SubElEntityConverter implements IEntityConverter<SubordinationElementModel,
        SubordinationElementEntity> {
    @Override
    public SubordinationElementModel entityToModel(SubordinationElementEntity entity) {
        SubordinationElementModel model = null;
        if (null != entity) {
            model = new SubordinationElementModel();
            model.setRootElement(entity.getRootElement());
            model.setSubordinates(entity.getSubordinates().stream().map(this::entityToModel).collect(
                    Collectors.toList()));
            model.setName(entity.getName());
            model.setRoleSuid(entity.getRoleSuid());
            model.setSuid(entity.getSuid());
        }
        return model;
    }

    @Override
    public SubordinationElementEntity modelToEntity(SubordinationElementModel model) {
        SubordinationElementEntity entity = null;
        if (null != model) {
            entity = new SubordinationElementEntity();
            entity.setRootElement(model.getRootElement());
            entity.setSubordinates(model.getSubordinates().stream().map(this::modelToEntity).collect(
                    Collectors.toList()));
            entity.setName(model.getName());
            entity.setRoleSuid(model.getRoleSuid());
        }
        return entity;
    }
}
