package ru.devazz.model;

import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

public class LegendOfIconsViewModel extends PresentationModel<ICommonService, IEntity> {

	@Override
	protected void initModel() {

	}

	@Override
	protected String getQueueName() {
		return null;
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
