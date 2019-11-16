package ru.devazz.service.impl.converters;

import ru.devazz.server.api.model.IEntity;

public interface IEntityConverter<M, E extends IEntity> {

    M entityToModel(E entity);

    E modelToEntity(M model);

}
