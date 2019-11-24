package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.UserEntity;
import ru.devazz.server.api.model.UserModel;

@Component
public class UserEntityConverter implements IEntityConverter<UserModel, UserEntity> {
    @Override
    public UserModel entityToModel(UserEntity entity) {
        UserModel model = null;
        if (null != entity) {
            model = new UserModel();
            model.setSuid(entity.getIduser());
            model.setImage(entity.getImage());
            model.setOnline(entity.getOnline());
            model.setName(entity.getName());
            model.setPassword(entity.getPassword());
            model.setPosition(entity.getPosition());
            model.setLogin(entity.getLogin());
            model.setPositionSuid(entity.getPositionSuid());
            model.setIdRole(entity.getIdrole());
        }
        return model;
    }

    @Override
    public UserEntity modelToEntity(UserModel model) {
        UserEntity entity = null;
        if (null != model) {
            entity = new UserEntity();
            entity.setIduser(model.getSuid());
            entity.setImage(model.getImage());
            entity.setOnline(model.getOnline());
            entity.setName(model.getName());
            entity.setPassword(model.getPassword());
            entity.setPosition(model.getPosition());
            entity.setLogin(model.getLogin());
            entity.setPositionSuid(model.getPositionSuid());
            entity.setIdrole(model.getIdRole());
        }
        return entity;
    }
}
