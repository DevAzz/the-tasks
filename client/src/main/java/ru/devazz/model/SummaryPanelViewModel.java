package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.bean.tasks.TaskServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.user.UserServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.ReportEntity;
import ru.sciencesquad.hqtasks.server.datamodel.TaskEntity;
import ru.sciencesquad.hqtasks.server.utils.Filter;
import ru.sciencesquad.hqtasks.server.utils.FilterType;
import ru.sciencesquad.hqtasks.server.utils.TaskTimeInterval;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import javax.naming.NamingException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Модель представления панели сводки по задачам
 */
public class SummaryPanelViewModel extends PresentationModel<ICommonService, IEntity> {

	/** Сущност отчета */
	private ReportEntity entity = null;

	/** Свойство текста лейбла отображения наименования боевого поста */
	private StringProperty subElLabelProperty;

	/** Свойство фотографии ДЛ на БП */
	private ObjectProperty<Image> imageProperty;

	/** Свойство текста лейбла количества успешно выполненых задач */
	private StringProperty successDoneAmountLabelProperty;

	/** Свойство текста лейбла количества задач выполненых с опозданием */
	private StringProperty overdueDoneAmountLabelProperty;

	/** Свойство текста лейбла количества завершенных задач */
	private StringProperty closedAmountLabelProperty;

	/** Свойство текста лейбла количества просроченных задач */
	private StringProperty overdueAmountLabelProperty;

	/** Свойство текста лейбла количества проваленных задач */
	private StringProperty failedAmountLabelProperty;

	/** Свойство текста лейбла количества задач в работе */
	private StringProperty inWorkAmountLabelProperty;

	/** Свойство текста лейбла количества задач на доработке */
	private StringProperty reworkAmountLabelProperty;

	/** Список входящих задач */
	private ObservableList<Task> inTasks;

	/** Список исходящих задач */
	private ObservableList<Task> outTasks;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		inTasks = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		outTasks = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

		subElLabelProperty = new SimpleStringProperty(this, "subElLabelProperty", "");
		imageProperty = new SimpleObjectProperty<>(this, "imageProperty", null);
		successDoneAmountLabelProperty = new SimpleStringProperty(this,
				"successDoneAmountLabelProperty", "");
		overdueDoneAmountLabelProperty = new SimpleStringProperty(this,
				"overdueDoneAmountLabelProperty", "");
		closedAmountLabelProperty = new SimpleStringProperty(this, "closedAmountLabelProperty", "");
		overdueAmountLabelProperty = new SimpleStringProperty(this, "overdueAmountLabelProperty",
				"");
		failedAmountLabelProperty = new SimpleStringProperty(this, "failedAmountLabelProperty", "");
		inWorkAmountLabelProperty = new SimpleStringProperty(this, "inWorkAmountLabelProperty", "");
		reworkAmountLabelProperty = new SimpleStringProperty(this, "reworkAmountLabel", "");
	}

	/**
	 * Возвращает {@link#subElLabelProperty}
	 *
	 * @return the {@link#subElLabelProperty}
	 */
	public StringProperty getSubElLabelProperty() {
		return subElLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#subElLabelProperty}
	 *
	 * @param subElLabelValue значение поля
	 */
	public void setSubElLabelValue(String subElLabelValue) {
		this.subElLabelProperty.set(subElLabelValue);
	}

	/**
	 * Возвращает {@link#imageProperty}
	 *
	 * @return the {@link#imageProperty}
	 */
	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}

	/**
	 * Устанавливает значение полю {@link#imageProperty}
	 *
	 * @param imageValue значение поля
	 */
	public void setImagePropertyValue(Image imageValue) {
		this.imageProperty.set(imageValue);
	}

	/**
	 * Возвращает {@link#successDoneAmountLabelProperty}
	 *
	 * @return the {@link#successDoneAmountLabelProperty}
	 */
	public StringProperty getSuccessDoneAmountLabelProperty() {
		return successDoneAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#successDoneAmountLabelProperty}
	 *
	 * @param successDoneAmountLabelValue значение поля
	 */
	public void setSuccessDoneAmountLabelPropertyValue(String successDoneAmountLabelValue) {
		this.successDoneAmountLabelProperty.set(successDoneAmountLabelValue);
	}

	/**
	 * Возвращает {@link#overdueDoneAmountLabelProperty}
	 *
	 * @return the {@link#overdueDoneAmountLabelProperty}
	 */
	public StringProperty getOverdueDoneAmountLabelProperty() {
		return overdueDoneAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#overdueDoneAmountLabelProperty}
	 *
	 * @param overdueDoneAmountLabelValue значение поля
	 */
	public void setOverdueDoneAmountLabelPropertyValue(String overdueDoneAmountLabelValue) {
		this.overdueDoneAmountLabelProperty.set(overdueDoneAmountLabelValue);
	}

	/**
	 * Возвращает {@link#closedAmountLabelProperty}
	 *
	 * @return the {@link#closedAmountLabelProperty}
	 */
	public StringProperty getClosedAmountLabelProperty() {
		return closedAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#closedAmountLabelProperty}
	 *
	 * @param closedAmountLabelValue значение поля
	 */
	public void setClosedAmountLabelPropertyValue(String closedAmountLabelValue) {
		this.closedAmountLabelProperty.set(closedAmountLabelValue);
	}

	/**
	 * Возвращает {@link#overdueAmountLabelProperty}
	 *
	 * @return the {@link#overdueAmountLabelProperty}
	 */
	public StringProperty getOverdueAmountLabelProperty() {
		return overdueAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#overdueAmountLabelProperty}
	 *
	 * @param overdueAmountLabelValue значение поля
	 */
	public void setOverdueAmountLabelPropertyValue(String overdueAmountLabelValue) {
		this.overdueAmountLabelProperty.set(overdueAmountLabelValue);
	}

	/**
	 * Возвращает {@link#failedAmountLabelProperty}
	 *
	 * @return the {@link#failedAmountLabelProperty}
	 */
	public StringProperty getFailedAmountLabelProperty() {
		return failedAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#failedAmountLabelProperty}
	 *
	 * @param failedAmountLabelValue значение поля
	 */
	public void setFailedAmountLabelPropertyValue(String failedAmountLabelValue) {
		this.failedAmountLabelProperty.set(failedAmountLabelValue);
	}

	/**
	 * Возвращает {@link#inWorkAmountLabelProperty}
	 *
	 * @return the {@link#inWorkAmountLabelProperty}
	 */
	public StringProperty getInWorkAmountLabelProperty() {
		return inWorkAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#inWorkAmountLabelProperty}
	 *
	 * @param inWorkAmountLabelValue значение поля
	 */
	public void setInWorkAmountLabelPropertyValue(String inWorkAmountLabelValue) {
		this.inWorkAmountLabelProperty.set(inWorkAmountLabelValue);
	}

	/**
	 * Возвращает {@link#reworkAmountLabel}
	 *
	 * @return the {@link#reworkAmountLabel}
	 */
	public StringProperty getReworkAmountLabelProperty() {
		return reworkAmountLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#reworkAmountLabel}
	 *
	 * @param reworkAmountLabelValue значение поля
	 */
	public void setReworkAmountLabelPropertyValue(String reworkAmountLabelValue) {
		this.reworkAmountLabelProperty.set(reworkAmountLabelValue);
	}

	/**
	 * Возвращает {@link#inTasks}
	 *
	 * @return the {@link#inTasks}
	 */
	public ObservableList<Task> getInTasks() {
		return inTasks;
	}

	/**
	 * Возвращает {@link#outTasks}
	 *
	 * @return the {@link#outTasks}
	 */
	public ObservableList<Task> getOutTasks() {
		return outTasks;
	}

	/**
	 * Возвращает {@link#entity}
	 *
	 * @return the {@link#entity}
	 */
	public ReportEntity getEntity() {
		return entity;
	}

	/**
	 * Устанавливает значение полю {@link#entity}
	 *
	 * @param aEntity значение поля
	 */
	public void setReportEntity(ReportEntity aEntity) {
		if (null != aEntity) {
			this.entity = aEntity;
			setSubElLabelValue(aEntity.getBattlePostName());
			setSuccessDoneAmountLabelPropertyValue(String.valueOf(aEntity.getSuccessDoneAmount()));
			setClosedAmountLabelPropertyValue(String.valueOf(aEntity.getClosedAmount()));
			setFailedAmountLabelPropertyValue(String.valueOf(aEntity.getFailedAmount()));
			setInWorkAmountLabelPropertyValue(String.valueOf(aEntity.getInWorkAmount()));
			setOverdueAmountLabelPropertyValue(String.valueOf(aEntity.getOverdueAmount()));
			setOverdueDoneAmountLabelPropertyValue(String.valueOf(aEntity.getOverdueDoneAmount()));
			setReworkAmountLabelPropertyValue(String.valueOf(aEntity.getReworkAmount()));
			setUserImage(aEntity.getBattlePostSuid());
			loadTasks(aEntity);
		}
	}

	/**
	 * Загрузка записей
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 */
	private void loadTasks(final ReportEntity aEntity) {
		Thread threadIn = new Thread(() -> {
			try {
				TaskServiceRemote service = EJBProxyFactory.getInstance()
						.getService(TaskServiceRemote.class);
				inTasks.clear();

				List<TaskEntity> entities = service.getAllUserTasksExecutorWithFilter(
						aEntity.getBattlePostSuid(), getFilter(aEntity));
				for (TaskEntity entity : entities) {
					inTasks.add(EntityConverter.getInstatnce()
							.convertTaskEntityToClientWrapTask(entity));
					Thread.sleep(70L);
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}

		});
		threadIn.setDaemon(true);
		threadIn.start();
		Thread threadOut = new Thread(() -> {
			try {
				TaskServiceRemote service = EJBProxyFactory.getInstance()
						.getService(TaskServiceRemote.class);
				outTasks.clear();

				List<TaskEntity> entities = service.getAllUserTasksAuthorWithFilter(
						aEntity.getBattlePostSuid(), getFilter(aEntity));
				for (TaskEntity entity : entities) {
					outTasks.add(EntityConverter.getInstatnce()
							.convertTaskEntityToClientWrapTask(entity));
					Thread.sleep(70L);
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		threadOut.setDaemon(true);
		threadOut.start();

	}

	/**
	 * Формирует фильтр для загрузки задач
	 *
	 * @param aEntity сущность отчета
	 * @return сформированный фильтр
	 */
	private Filter getFilter(final ReportEntity aEntity) {
		Filter filter = new Filter();
		filter.setStartDate(aEntity.getDateStart());
		filter.setEndDate(aEntity.getDateEnd());

		List<String> list = new ArrayList<>();
		list.add(TaskTimeInterval.CUSTOM_TIME_INTERVAL.getMenuSuid());
		Map<FilterType, List<String>> mapFilterType = new HashMap<>();
		mapFilterType.put(FilterType.FILTER_BY_DATE, list);
		filter.setFilterTypeMap(mapFilterType);
		return filter;
	}

	/**
	 * Устанавливает пользовательскую фотографию
	 *
	 * @param aSubElSuid идентификатор элемента подчиненности
	 */
	private void setUserImage(Long aSubElSuid) {
		Thread thread = new Thread(() -> {
			try {
				Image image = null;
				UserServiceRemote userService = EJBProxyFactory.getInstance()
						.getService(UserServiceRemote.class);
				Long userSuid = userService.getUserSuidBySubElSuid(aSubElSuid);
				File file = new File(Utils.getInstance().getUserImageName(userSuid));
				if (file.exists()) {
					image = new Image(file.toURI().toString(), 145, 145, true, false, true);
				} else {
					byte[] imageArr = userService.getUserImage(userSuid);
					file = Utils.getInstance().createFileImage(imageArr, userSuid);
					image = new Image(file.toURI().toString(), 145, 145, true, false, true);
				}

				setImagePropertyValue(image);
				image = null;
				file = null;
				System.gc();
			} catch (NamingException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		// TODO Auto-generated method stub
		return null;
	}

}