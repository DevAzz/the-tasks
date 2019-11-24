package ru.devazz.service.impl.converters;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import ru.devazz.entity.RoleEntity;
import ru.devazz.server.api.model.RoleModel;

@Component
public class RoleEntityConverter implements IEntityConverter<RoleModel, RoleEntity> {
    @Override
    public RoleModel entityToModel(RoleEntity entity) {
        RoleModel model = null;
        if (null != entity) {
            model = new RoleModel();
            model.setName(entity.getName());
            model.setSuid(entity.getSuid());
        }
        return model;
    }

    @Override
    public RoleEntity modelToEntity(RoleModel model) {
        RoleEntity entity = null;
        if (null != model) {
            entity = new RoleEntity();
            entity.setIdRole(model.getSuid());
            entity.setName(entity.getName());
        }
        return entity;
    }
}
