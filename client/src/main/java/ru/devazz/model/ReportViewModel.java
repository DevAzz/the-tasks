package ru.devazz.model;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import ru.devazz.server.api.IReportService;
import ru.devazz.server.api.model.ReportModel;
import ru.devazz.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Модель представления отчетов
 */
public class ReportViewModel extends PresentationModel<IReportService, ReportModel> {

	/** Текстовое свойство наименования должности */
	private StringProperty positionNameTextProperty;

	/** Текстовое свойство даты начала */
	private StringProperty startDateTextProperty;

	/** Текстовое свойство даты завершения */
	private StringProperty endDateTextProperty;

	/** Текстовое свойство наименования отчета */
	private StringProperty reportTitleTextProperty;

	/** Текстовое свойство количество выполненных задач */
	private StringProperty countDoneTasksTextProperty;

	/** Текстовое свойство количества просроченных задач */
	private StringProperty countOverdueDoneTasksTextProperty;

	/** Текстовое свойство колчества проваленных задач */
	private StringProperty countFaildTasksTextProperty;

	/** Текстовое поле количества задач в работе */
	private StringProperty countInWorksTaskTextProperty;

	/** Свойство текста лейбла количества завершенных задач */
	private StringProperty closedAmountLabelProperty;

	/** Свойство текста лейбла количества просроченных задач */
	private StringProperty overdueAmountLabel;

	/** Свойство текста лейбла количества задач на доработке */
	private StringProperty reworkAmountLabel;

	/** Свойство доступности генерации отчета */
	private BooleanProperty generateReportDisable;

	/** Идентификатор должности пользователя */
	private Long positionSuid;

	/** Отчтет в формате HTML */
	private File htmlFileReport;

	/** Отчет в формате PDF */
	private File pdfFileReport;

	/** Сгенерированный отчет */
	private JasperPrint jasperPrintReport;

	@Override
	protected void initModel() {
		positionNameTextProperty = new SimpleStringProperty(this, "positionNameTextProperty");
		startDateTextProperty = new SimpleStringProperty(this, "startDateTextProperty");
		endDateTextProperty = new SimpleStringProperty(this, "endDateTextProperty");
		reportTitleTextProperty = new SimpleStringProperty(this, "reportTitleTextProperty",
				"Отчет по:");
		countDoneTasksTextProperty = new SimpleStringProperty(this, "countDoneTasksTextProperty",
				"");
		countOverdueDoneTasksTextProperty = new SimpleStringProperty(this,
				"countOverDueTasksTextProperty", "");
		countFaildTasksTextProperty = new SimpleStringProperty(this, "countFaildTasksTextProperty",
				"");
		countInWorksTaskTextProperty = new SimpleStringProperty(this,
				"countInWorksTaskTextProperty", "");
		closedAmountLabelProperty = new SimpleStringProperty(this, "closedAmountLabelProperty", "");
		overdueAmountLabel = new SimpleStringProperty(this, "overdueAmountLabel", "");
		reworkAmountLabel = new SimpleStringProperty(this, "reworkAmountLabel", "");

		generateReportDisable = new SimpleBooleanProperty(this, "generateReportEnable", true);
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public StringProperty getClosedAmountLabelProperty() {
		return closedAmountLabelProperty;
	}

	private void setClosedAmountLabelPropertyValue(String closedAmountLabelValue) {
		this.closedAmountLabelProperty.set(closedAmountLabelValue);
	}

	public StringProperty getOverdueAmountLabel() {
		return overdueAmountLabel;
	}

	private void setOverdueAmountLabelValue(String overdueAmountLabelValue) {
		this.overdueAmountLabel.set(overdueAmountLabelValue);
	}

	public StringProperty getReworkAmountLabel() {
		return reworkAmountLabel;
	}

	private void setReworkAmountLabelValue(String reworkAmountLabelValue) {
		this.reworkAmountLabel.set(reworkAmountLabelValue);
	}

	public StringProperty getPositionNameTextProperty() {
		return positionNameTextProperty;
	}

	public void setPositionNameTextPropertyValue(String positionNameTextValue) {
		this.positionNameTextProperty.set(positionNameTextValue);
	}
	public StringProperty getStartDateTextProperty() {
		return startDateTextProperty;
	}

	public StringProperty getEndDateTextProperty() {
		return endDateTextProperty;
	}

	public StringProperty getCountDoneTasksTextProperty() {
		return countDoneTasksTextProperty;
	}

	private void setCountDoneTasksTextPropertyValue(String countDoneTasksTextValue) {
		this.countDoneTasksTextProperty.set(countDoneTasksTextValue);
	}

	public StringProperty getCountOverDueTasksTextProperty() {
		return countOverdueDoneTasksTextProperty;
	}

	private void setCountOverdueDoneTasksTextPropertyValue(String countOverDueTasksTextValue) {
		this.countOverdueDoneTasksTextProperty.set(countOverDueTasksTextValue);
	}

	public StringProperty getCountFaildTasksTextProperty() {
		return countFaildTasksTextProperty;
	}

	private void setCountFaildTasksTextPropertyValue(String countFaildTasksTextValue) {
		this.countFaildTasksTextProperty.set(countFaildTasksTextValue);
	}

	public StringProperty getCountInWorksTaskTextProperty() {
		return countInWorksTaskTextProperty;
	}

	private void setCountInWorksTaskTextPropertyValue(String countInWorksTaskTextValue) {
		this.countInWorksTaskTextProperty.set(countInWorksTaskTextValue);
	}

	public BooleanProperty getGenerateReportDisable() {
		return generateReportDisable;
	}

	public void setGenerateReportDisableValue(Boolean generateReportEnable) {
		this.generateReportDisable.set(generateReportEnable);
	}

	public Long getPositionSuid() {
		return positionSuid;
	}

	public void setPositionSuid(Long positionSuid) {
		this.positionSuid = positionSuid;
	}

	/**
	 * Генерация отчета
	 */
	public void generateReport() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		try {
			ReportModel entity = getService().createReportEntity(positionSuid,
					LocalDateTime.parse(startDateTextProperty.get(), formatter),
					LocalDateTime.parse(endDateTextProperty.get(), formatter));
			if (null != entity) {

				JasperPrint jasperReport = generateReportByEntity(entity);
				jasperPrintReport = jasperReport;

				htmlFileReport = getHTML(jasperReport, entity);
				pdfFileReport = getPDF(jasperReport, entity);

				Platform.runLater(() -> {
					setCountDoneTasksTextPropertyValue(
							String.valueOf(entity.getSuccessDoneAmount()));
					setCountOverdueDoneTasksTextPropertyValue(
							String.valueOf(entity.getOverdueDoneAmount()));
					setCountFaildTasksTextPropertyValue(String.valueOf(entity.getFailedAmount()));
					setCountInWorksTaskTextPropertyValue(String.valueOf(entity.getInWorkAmount()));
					setClosedAmountLabelPropertyValue(String.valueOf(entity.getClosedAmount()));
					setReworkAmountLabelValue(String.valueOf(entity.getReworkAmount()));
					setOverdueAmountLabelValue(String.valueOf(entity.getOverdueAmount()));
				});
			}

		} catch (Exception e) {
			// TODO Логирование
			e.printStackTrace();
		}
	}

	/**
	 * Отправляет отчёт на печать
	 *
	 */
	public void printReport() {
		Thread thread = new Thread(() -> {
			try {
				JasperPrintManager.printReport(jasperPrintReport, true);
			} catch (JRException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * Сохранить отчет как
	 *
	 *
	 * @param absolutePath путь куда сохранять
	 * @throws JRException в случае ошибки
	 */
	public void saveReportAs(String absolutePath) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrintReport, absolutePath);
	}

	/**
	 *
	 * Генерирует отчет
	 *
	 * @param aEntity сущность отчета
	 * @return отчет
	 * @throws JRException в случае ошибки
	 */
	private JasperPrint generateReportByEntity(ReportModel aEntity) throws JRException {
		ArrayList<ReportModel> dataArr = new ArrayList<>();
		dataArr.add(aEntity);
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataArr);
		Map<String, Object> parameters = new HashMap<>();
		InputStream jasperStream = getClass().getResourceAsStream("/report/postReport.jrxml");
		JasperDesign jasperDesign = JRXmlLoader.load(jasperStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		return JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
	}

	/**
	 * Возвращает отчет в формате PDF
	 *
	 * @param aReport отчет
	 * @return отчет в формате PDF
	 */
	private File getPDF(JasperPrint aReport, ReportModel aEntity) {
		File file = null;
		try (FileOutputStream outputStream = new FileOutputStream(
				Utils.getInstance().getTempDir() + aEntity.getPostName() + ".pdf")) {
			outputStream.write(JasperExportManager.exportReportToPdf(aReport));

			file = new File(
					Utils.getInstance().getTempDir() + aEntity.getPostName() + ".pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Возвращает отчет в формате HTML
	 *
	 * @param aReport отчет
	 * @param aEntity сущность отчета
	 *
	 * @return отчет в формате HTML
	 * @throws JRException в случае ошибки
	 */
	private File getHTML(JasperPrint aReport, ReportModel aEntity) throws JRException {
		File file = new File(
				Utils.getInstance().getTempDir() + aEntity.getPostName() + ".html");
		JasperExportManager.exportReportToHtmlFile(aReport, file.getAbsolutePath());
		return file;
	}

	public File getHtmlFileReport() {
		return htmlFileReport;
	}

	@Override
	public Class<IReportService> getTypeService() {
		return IReportService.class;
	}

}
