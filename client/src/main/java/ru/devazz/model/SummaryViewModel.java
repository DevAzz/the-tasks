package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.IReportService;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.server.api.model.ReportModel;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Модель представления сводки по задачам
 */
public class SummaryViewModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство текста лейбла даты */
	private StringProperty dateLabelProperty;

	/** Модель виджета выбора временного промежутка */
	private CustomTimeIntervalModel customTimeIntervalModel;

	/** Список сущностей отчетов по должностям */
	private ObservableList<ReportModel> panelsList;

	/** Список идентификаторов элементов подчиненности */
	private ObservableList<SubordinationElement> subElList;

	/** Дата начала */
	private LocalDateTime startDate;

	/** Дата завершения */
	private LocalDateTime endDate;

	/** Сервис работы с элементами подчиненности */
	private ISubordinationElementService subElsService;

	@Override
	protected void initModel() {
		dateLabelProperty = new SimpleStringProperty(this, "dateLabelProperty", "");
		panelsList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		subElList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

		initDateLabelValue(null, null);

		Thread thread = new Thread(() -> {
			subElsService = ProxyFactory.getInstance()
					.getService(ISubordinationElementService.class);
			setSubElSuidList(getListSubEls());
		});
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	/**
	 * Выбирает в качестве временного интревала текущий день
	 */
	public void selectCurrentDayInterval() {
		initDateLabelValue(LocalDateTime.now(), Utils.getInstance().getEndDateForFilterDate());
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
	private void initDateLabelValue(LocalDateTime aStartDate, LocalDateTime aEndDate) {
		LocalDateTime startDate = aStartDate;
		LocalDateTime endDate = aEndDate;
		if (null == startDate) {
			startDate = Utils.getInstance().getStartDateForFilterDate();
		}
		if (null == endDate) {
			endDate = Utils.getInstance().getEndDateForFilterDate();
		}

		if ((null != startDate) && (null != endDate)) {
			this.startDate = startDate;
			this.endDate = endDate;
			DateTimeFormatter parser = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
			setDateLabelPropertyValue(
					"C " + parser.format(getStartDate()) + " по " + parser.format(getEndDate()));
		}
	}

	/**
	 * Загружает сущности отчетов по задачам
	 */
	private void loadReportEntities() {
		Thread thread = new Thread(() -> {
			try {
				panelsList.clear();
				IReportService reportService = ProxyFactory.getInstance()
						.getService(IReportService.class);
				if ((null != startDate) && (null != endDate)) {
					for (SubordinationElement subEl : subElList) {
						ReportModel report = reportService.createReportEntity(subEl.getSuid(),
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
		return () -> {
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

	}

	public void setCustomTimeIntervalModel(CustomTimeIntervalModel customTimeIntervalModel) {
		this.customTimeIntervalModel = customTimeIntervalModel;
	}

	public ObservableList<SubordinationElement> getSubElSuidList() {
		return subElList;
	}

	public void setSubElSuidList(ObservableList<SubordinationElement> subElSuidList) {
		this.subElList = subElSuidList;
		loadReportEntities();
	}

	private LocalDateTime getStartDate() {
		return startDate;
	}

	private void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
		initDateLabelValue(startDate, endDate);
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
		initDateLabelValue(startDate, endDate);
	}

	public ObservableList<ReportModel> getPanelsList() {
		return panelsList;
	}

	public StringProperty getDateLabelProperty() {
		return dateLabelProperty;
	}

	private void setDateLabelPropertyValue(String dateLabelValue) {
		Platform.runLater(() -> this.dateLabelProperty.set(dateLabelValue));
	}

	/**
	 * Возвращает список элементов подчиненности
	 *
	 * @return список элементов подчиненности
	 */
	private ObservableList<SubordinationElement> getListSubEls() {
		ObservableList<SubordinationElement> result = FXCollections.observableArrayList();
		for (SubordinationElementModel entity : subElsService.getTotalSubElList()) {
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
			LocalDateTime startDate = aDate.atStartOfDay();
			LocalDateTime endDate = aDate.atTime(LocalTime.MAX);
			setEndDate(endDate);
			setStartDate(startDate);
			loadReportEntities();
		}
	}

	@Override
	public Class<ICommonService> getTypeService() {
		return null;
	}

}
