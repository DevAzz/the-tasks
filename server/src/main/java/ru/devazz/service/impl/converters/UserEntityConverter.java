package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.UserEntity;
import ru.devazz.server.api.model.UserModel;

@Component
public class UserEntityConverter implements IEntityConverter<UserModel, UserEntity> {
    @Override
    public UserModel entityToModel(UserEntity entity) {
        return null;
    }

    @Override
    public UserEntity modelToEntity(UserModel model) {
        return null;
    }
}
