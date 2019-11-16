package ru.devazz.service.impl.converters;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import ru.devazz.entity.RoleEntity;
import ru.devazz.server.api.model.RoleModel;

@Component
public class RoleEntityConverter implements IEntityConverter<RoleModel, RoleEntity> {
    @Override
    public RoleModel entityToModel(RoleEntity entity) {
        return null;
    }

    @Override
    public RoleEntity modelToEntity(RoleModel model) {
        return null;
    }
}
