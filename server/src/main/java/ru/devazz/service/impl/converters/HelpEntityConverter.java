package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.HelpEntity;
import ru.devazz.server.api.model.HelpModel;

@Component
public class HelpEntityConverter implements IEntityConverter<HelpModel, HelpEntity> {

    @Override
    public HelpModel entityToModel(HelpEntity entity) {
        return null;
    }

    @Override
    public HelpEntity modelToEntity(HelpModel model) {
        return null;
    }
}
