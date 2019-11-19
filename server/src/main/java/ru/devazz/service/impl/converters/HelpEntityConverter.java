package ru.devazz.service.impl.converters;

import org.springframework.stereotype.Component;
import ru.devazz.entity.HelpEntity;
import ru.devazz.server.api.model.HelpModel;

@Component
public class HelpEntityConverter implements IEntityConverter<HelpModel, HelpEntity> {

    @Override
    public HelpModel entityToModel(HelpEntity entity) {
        HelpModel model = null;
        if (null != entity) {
            model = new HelpModel();
            model.setHelpItemText(entity.getHelpItemText());
            model.setName(entity.getName());
            model.setRole(entity.getRole());
            model.setSuid(entity.getSuid());
        }
        return model;
    }

    @Override
    public HelpEntity modelToEntity(HelpModel model) {
        HelpEntity entity = null;
        if (null != model) {
            entity = new HelpEntity();
            entity.setHelpItemText(model.getHelpItemText());
            entity.setHelpItemName(model.getName());
            entity.setRole(model.getRole());
        }
        return entity;
    }
}
