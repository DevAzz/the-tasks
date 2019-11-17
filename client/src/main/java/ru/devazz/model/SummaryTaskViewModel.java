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

	/**
	 * Возвращает {@link#aTaskSuid}
	 *
	 * @return the {@link#aTaskSuid}
	 */
	public Long getTaskSuid() {
		return taskSuid;
	}

	/**
	 * Устанавливает значение полю {@link#aTaskSuid}
	 *
	 * @param aTaskSuid значение поля
	 */
	public void setTaskSuid(Long aTaskSuid) {
		this.taskSuid = aTaskSuid;
	}

	/**
	 * Возвращает {@link#priorityImageProperty}
	 *
	 * @return the {@link#priorityImageProperty}
	 */
	public ObjectProperty<Image> getPriorityImageProperty() {
		return priorityImageProperty;
	}

	/**
	 * Устанавливает значение полю {@link#priorityImageProperty}
	 *
	 * @param priorityImageValue значение поля
	 */
	public void setPriorityImagePropertyValue(Image priorityImageValue) {
		this.priorityImageProperty.set(priorityImageValue);
	}

	/**
	 * Возвращает {@link#nameTaskProperty}
	 *
	 * @return the {@link#nameTaskProperty}
	 */
	public StringProperty getNameTaskProperty() {
		return nameTaskProperty;
	}

	/**
	 * Устанавливает значение полю {@link#nameTaskProperty}
	 *
	 * @param nameTaskValue значение поля
	 */
	public void setNameTaskPropertyValue(String nameTaskValue) {
		this.nameTaskProperty.set(nameTaskValue);
	}

	/**
	 * Возвращает {@link#statusImageProperty}
	 *
	 * @return the {@link#statusImageProperty}
	 */
	public ObjectProperty<Image> getStatusImageProperty() {
		return statusImageProperty;
	}

	/**
	 * Устанавливает значение полю {@link#statusImageProperty}
	 *
	 * @param statusImageValue значение поля
	 */
	public void setStatusImagePropertyValue(Image statusImageValue) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
