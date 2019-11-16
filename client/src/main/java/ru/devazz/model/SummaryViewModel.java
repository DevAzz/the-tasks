package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.ICommonService;
import ru.sciencesquad.hqtasks.server.bean.report.ReportServiceRemote;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.sciencesquad.hqtasks.server.datamodel.ReportEntity;
import ru.sciencesquad.hqtasks.server.datamodel.SubordinationElementEntity;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.server.EJBProxyFactory;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import javax.naming.NamingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Модель представления сводки по задачам
 */
public class SummaryViewModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста лейбла даты */
	private StringProperty dateLabelProperty;

	/** Модель виджета выбора временного промежутка */
	private CustomTimeIntervalModel customTimeIntervalModel;

	/** Список сущностей отчетов по боевым постам */
	private ObservableList<ReportEntity> panelsList;

	/** Список идентификаторов элементов подчиненности */
	private ObservableList<SubordinationElement> subElList;

	/** Дата начала */
	private Date startDate;

	/** Дата завершения */
	private Date endDate;

	/** Сервис работы с элементами подчиненности */
	private SubordinatioElementServiceRemote subElsService;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "");
		panelsList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		subElList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

		initDateLabelValue(null, null);

		Thread thread = new Thread(() -> {
			try {
				subElsService = EJBProxyFactory.getInstance()
						.getService(SubordinatioElementServiceRemote.class);
				setSubElSuidList(getListSubEls());
			} catch (NamingException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Выбирает в качестве временного интревала текущий день
	 */
	public void selectCurrentDayInterval() {
		initDateLabelValue(new Date(), Utils.getInstance().getEndDateForFilterDate());
		loadReportEntities();
	}

	/**
	 * Выбирает в качестве временного интревала текущую неделю
	 */
	public void selectCurrentWeekInterval() {
		initDateLabelValue(Utils.getInstance().getStartDateForFilterWeek(),
				Utils.getInstance().getEndDateForFilterWeek());
		loadReportEntities();
	}

	/**
	 * Выбирает в качестве временного интревала текущий месяц
	 */
	public void selectCurrentMonthInterval() {
		initDateLabelValue(Utils.getInstance().getStartDateForFilterMonth(),
				Utils.getInstance().getEndDateForFilterMonth());
		loadReportEntities();
	}

	/**
	 * Инициализирует значение лейбла даты сводки
	 */
	private void initDateLabelValue(Date aStartDate, Date aEndDate) {
		Date startDate = aStartDate;
		Date endDate = aEndDate;
		if (null == startDate) {
			startDate = Utils.getInstance().getStartDateForFilterDate();
		}
		if (null == endDate) {
			endDate = Utils.getInstance().getEndDateForFilterDate();
		}

		if ((null != startDate) && (null != endDate)) {
			this.startDate = startDate;
			this.endDate = endDate;
			SimpleDateFormat parser = new SimpleDateFormat("HH:mm dd.MM.yyyy");
			setDateLabelPropertyValue(
					"C " + parser.format(getStartDate()) + " по " + parser.format(getEndDate()));
		}
	}

	/**
	 * Загружает сущности отчетов по задачам
	 */
	public void loadReportEntities() {
		Thread thread = new Thread(() -> {
			try {
				panelsList.clear();
				ReportServiceRemote reportService = EJBProxyFactory.getInstance()
						.getService(ReportServiceRemote.class);
				if ((null != startDate) && (null != endDate)) {
					for (SubordinationElement subEl : subElList) {
						ReportEntity report = reportService.createReportEntity(subEl.getSuid(),
								startDate, endDate);
						panelsList.add(report);
						Thread.sleep(70L);
					}
				}
			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Создает поток поиска записей по заданному промежутку времени
	 *
	 * @return поток поиска записей по заданному промежутку времени
	 */
	public Runnable createSearchRunnable() {
		Runnable runnable = () -> {
			try {
				setStartDate(customTimeIntervalModel.getStartDate());
				setEndDate(customTimeIntervalModel.getEndDate());
				if ((null != startDate) && (null != endDate)) {
					loadReportEntities();
				}
			} catch (ParseException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		};
		return runnable;

	}

	/**
	 * Возвращает {@link#customTimeIntervalModel}
	 *
	 * @return the {@link#customTimeIntervalModel}
	 */
	public CustomTimeIntervalModel getCustomTimeIntervalModel() {
		return customTimeIntervalModel;
	}

	/**
	 * Устанавливает значение полю {@link#customTimeIntervalModel}
	 *
	 * @param customTimeIntervalModel значение поля
	 */
	public void setCustomTimeIntervalModel(CustomTimeIntervalModel customTimeIntervalModel) {
		this.customTimeIntervalModel = customTimeIntervalModel;
	}

	/**
	 * Возвращает {@link#subElSuidList}
	 *
	 * @return the {@link#subElSuidList}
	 */
	public ObservableList<SubordinationElement> getSubElSuidList() {
		return subElList;
	}

	/**
	 * Устанавливает значение полю {@link#subElSuidList}
	 *
	 * @param subElSuidList значение поля
	 */
	public void setSubElSuidList(ObservableList<SubordinationElement> subElSuidList) {
		this.subElList = subElSuidList;
		loadReportEntities();
	}

	/**
	 * Возвращает {@link#startDate}
	 *
	 * @return the {@link#startDate}
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Устанавливает значение полю {@link#startDate}
	 *
	 * @param startDate значение поля
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		initDateLabelValue(startDate, endDate);
	}

	/**
	 * Возвращает {@link#endDate}
	 *
	 * @return the {@link#endDate}
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Устанавливает значение полю {@link#endDate}
	 *
	 * @param endDate значение поля
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		initDateLabelValue(startDate, endDate);
	}

	/**
	 * Возвращает {@link#panelsList}
	 *
	 * @return the {@link#panelsList}
	 */
	public ObservableList<ReportEntity> getPanelsList() {
		return panelsList;
	}

	/**
	 * Возвращает {@link#dateLabelProperty}
	 *
	 * @return the {@link#dateLabelProperty}
	 */
	public StringProperty getDateLabelProperty() {
		return dateLabelProperty;
	}

	/**
	 * Устанавливает значение полю {@link#dateLabelProperty}
	 *
	 * @param dateLabelValue значение поля
	 */
	public void setDateLabelPropertyValue(String dateLabelValue) {
		Platform.runLater(() -> {
			this.dateLabelProperty.set(dateLabelValue);
		});
	}

	/**
	 * Возвращает список элементов подчиненности
	 *
	 * @return список элементов подчиненности
	 */
	private ObservableList<SubordinationElement> getListSubEls() {
		ObservableList<SubordinationElement> result = FXCollections.observableArrayList();
		for (SubordinationElementEntity entity : subElsService.getTotalSubElList()) {
			result.add(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity));
		}
		return result;
	}

	/**
	 * Выбор произвольного дня
	 *
	 * @param aDate дата
	 */
	public void selectCustomDay(LocalDate aDate) {
		if (null != aDate) {
			Date startDate = Date
					.from(aDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date
					.from(aDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant());
			setEndDate(endDate);
			setStartDate(startDate);
			loadReportEntities();
		}
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
