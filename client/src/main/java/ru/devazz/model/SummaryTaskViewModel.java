package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import ru.devazz.entities.Task;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;

/**
 * Модель представления панели задачи сводки
 */
public class SummaryTaskViewModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство изображения приоритета задачи */
	private ObjectProperty<Image> priorityImageProperty;

	/** Свойство текста лейбла задачи */
	private StringProperty nameTaskProperty;

	/** Свойство изображения статуса задачи */
	private ObjectProperty<Image> statusImageProperty;

	/** Идентификатор задачи */
	private Long taskSuid;

	@Override
	protected void initModel() {
		priorityImageProperty = new SimpleObjectProperty<>(this, "priorityImageProperty", null);
		nameTaskProperty = new SimpleStringProperty(this, "priorityImageProperty", "");
		statusImageProperty = new SimpleObjectProperty<>(this, "statusImageProperty", null);
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public Long getTaskSuid() {
		return taskSuid;
	}

	private void setTaskSuid(Long aTaskSuid) {
		this.taskSuid = aTaskSuid;
	}

	public ObjectProperty<Image> getPriorityImageProperty() {
		return priorityImageProperty;
	}

	private void setPriorityImagePropertyValue(Image priorityImageValue) {
		this.priorityImageProperty.set(priorityImageValue);
	}

	public StringProperty getNameTaskProperty() {
		return nameTaskProperty;
	}

	private void setNameTaskPropertyValue(String nameTaskValue) {
		this.nameTaskProperty.set(nameTaskValue);
	}

	public ObjectProperty<Image> getStatusImageProperty() {
		return statusImageProperty;
	}

	private void setStatusImagePropertyValue(Image statusImageValue) {
		this.statusImageProperty.set(statusImageValue);
	}

	/**
	 * Устанавливает параметры задачи
	 *
	 * @param aTask задача
	 */
	public void setTaskParams(Task aTask) {
		if (null != aTask) {
			setTaskSuid(aTask.getSuid());
			setNameTaskPropertyValue(aTask.getName());
			switch (aTask.getPriority()) {
			case CRITICAL:
				setPriorityImagePropertyValue(new Image("/css/priority4.png"));
				break;
			case MAJOR:
				setPriorityImagePropertyValue(new Image("/css/priority3.png"));
				break;
			case MINOR:
				setPriorityImagePropertyValue(new Image("/css/priority2.png"));
				break;
			case TRIVIAL:
				setPriorityImagePropertyValue(new Image("/css/priority1.png"));
				break;
			case EVERYDAY:
				setPriorityImagePropertyValue(new Image("/css/everyDay.png"));
				break;
			default:
				break;
			}

			switch (aTask.getStatus()) {
			case DONE:
				setStatusImagePropertyValue(new Image("/css/complited.png"));
				break;
			case CLOSED:
				setStatusImagePropertyValue(new Image("/css/complitedAndLocked.png"));
				break;
			case FAILD:
				setStatusImagePropertyValue(new Image("/css/faild.png"));
				break;
			case OVERDUE:
				setStatusImagePropertyValue(new Image("/css/timeoutcomplited.png"));
				break;
			case REWORK:
				setStatusImagePropertyValue(new Image("/css/rework.png"));
				break;
			case WORKING:
				setStatusImagePropertyValue(new Image("/css/inwork.png"));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
