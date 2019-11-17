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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Модель представления отчетов
 */
public class ReportViewModel extends PresentationModel<IReportService, ReportModel> {

	/** Текстовое свойство наименования боевого поста */
	private StringProperty battleNameTextProperty;

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
		battleNameTextProperty = new SimpleStringProperty(this, "battleNameTextProperty");
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
	 * Возвращает {@link#overdueAmountLabel}
	 *
	 * @return the {@link#overdueAmountLabel}
	 */
	public StringProperty getOverdueAmountLabel() {
		return overdueAmountLabel;
	}

	/**
	 * Устанавливает значение полю {@link#overdueAmountLabel}
	 *
	 * @param overdueAmountLabelValue значение поля
	 */
	public void setOverdueAmountLabelValue(String overdueAmountLabelValue) {
		this.overdueAmountLabel.set(overdueAmountLabelValue);
	}

	/**
	 * Возвращает {@link#reworkAmountLabel}
	 *
	 * @return the {@link#reworkAmountLabel}
	 */
	public StringProperty getReworkAmountLabel() {
		return reworkAmountLabel;
	}

	/**
	 * Устанавливает значение полю {@link#reworkAmountLabel}
	 *
	 * @param reworkAmountLabelValue значение поля
	 */
	public void setReworkAmountLabelValue(String reworkAmountLabelValue) {
		this.reworkAmountLabel.set(reworkAmountLabelValue);
	}

	/**
	 * Возвращает {@link#battleNameTextProperty}
	 *
	 * @return the {@link#battleNameTextProperty}
	 */
	public StringProperty getBattleNameTextProperty() {
		return battleNameTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#battleNameTextProperty}
	 *
	 * @param battleNameTextValue значение поля
	 */
	public void setBattleNameTextPropertyValue(String battleNameTextValue) {
		this.battleNameTextProperty.set(battleNameTextValue);
	}

	/**
	 * Возвращает {@link#startDateTextProperty}
	 *
	 * @return the {@link#startDateTextProperty}
	 */
	public StringProperty getStartDateTextProperty() {
		return startDateTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#startDateTextProperty}
	 *
	 * @param startDateTextValue значение поля
	 */
	public void setStartDateTextPropertyValue(String startDateTextValue) {
		this.startDateTextProperty.set(startDateTextValue);
	}

	/**
	 * Возвращает {@link#endDateTextProperty}
	 *
	 * @return the {@link#endDateTextProperty}
	 */
	public StringProperty getEndDateTextProperty() {
		return endDateTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#endDateTextProperty}
	 *
	 * @param endDateTextValue значение поля
	 */
	public void setEndDateTextPropertyValue(String endDateTextValue) {
		this.endDateTextProperty.set(endDateTextValue);
	}

	/**
	 * Возвращает {@link#reportTitleTextProperty}
	 *
	 * @return the {@link#reportTitleTextProperty}
	 */
	public StringProperty getReportTitleTextProperty() {
		return reportTitleTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#reportTitleTextProperty}
	 *
	 * @param reportTitleTextValue значение поля
	 */
	public void setReportTitleTextPropertyValue(String reportTitleTextValue) {
		this.reportTitleTextProperty.set(reportTitleTextValue);
	}

	/**
	 * Возвращает {@link#countDoneTasksTextProperty}
	 *
	 * @return the {@link#countDoneTasksTextProperty}
	 */
	public StringProperty getCountDoneTasksTextProperty() {
		return countDoneTasksTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#countDoneTasksTextProperty}
	 *
	 * @param countDoneTasksTextValue значение поля
	 */
	public void setCountDoneTasksTextPropertyValue(String countDoneTasksTextValue) {
		this.countDoneTasksTextProperty.set(countDoneTasksTextValue);
	}

	/**
	 * Возвращает {@link#countOverDueTasksTextProperty}
	 *
	 * @return the {@link#countOverDueTasksTextProperty}
	 */
	public StringProperty getCountOverDueTasksTextProperty() {
		return countOverdueDoneTasksTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#countOverDueTasksTextProperty}
	 *
	 * @param countOverDueTasksTextValue значение поля
	 */
	public void setCountOverdueDoneTasksTextPropertyValue(String countOverDueTasksTextValue) {
		this.countOverdueDoneTasksTextProperty.set(countOverDueTasksTextValue);
	}

	/**
	 * Возвращает {@link#countFaildTasksTextProperty}
	 *
	 * @return the {@link#countFaildTasksTextProperty}
	 */
	public StringProperty getCountFaildTasksTextProperty() {
		return countFaildTasksTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#countFaildTasksTextProperty}
	 *
	 * @param countFaildTasksTextValue значение поля
	 */
	public void setCountFaildTasksTextPropertyValue(String countFaildTasksTextValue) {
		this.countFaildTasksTextProperty.set(countFaildTasksTextValue);
	}

	/**
	 * Возвращает {@link#countInWorksTaskTextProperty}
	 *
	 * @return the {@link#countInWorksTaskTextProperty}
	 */
	public StringProperty getCountInWorksTaskTextProperty() {
		return countInWorksTaskTextProperty;
	}

	/**
	 * Устанавливает значение полю {@link#countInWorksTaskTextProperty}
	 *
	 * @param countInWorksTaskTextValue значение поля
	 */
	public void setCountInWorksTaskTextPropertyValue(String countInWorksTaskTextValue) {
		this.countInWorksTaskTextProperty.set(countInWorksTaskTextValue);
	}

	/**
	 * Возвращает {@link#generateReportEnable}
	 *
	 * @return the {@link#generateReportEnable}
	 */
	public BooleanProperty getGenerateReportDisable() {
		return generateReportDisable;
	}

	/**
	 * Устанавливает значение полю {@link#generateReportEnable}
	 *
	 * @param generateReportEnable значение поля
	 */
	public void setGenerateReportDisableValue(Boolean generateReportEnable) {
		this.generateReportDisable.set(generateReportEnable);
	}

	/**
	 * Возвращает {@link#positionSuid}
	 *
	 * @return the {@link#positionSuid}
	 */
	public Long getPositionSuid() {
		return positionSuid;
	}

	/**
	 * Устанавливает значение полю {@link#positionSuid}
	 *
	 * @param positionSuid значение поля
	 */
	public void setPositionSuid(Long positionSuid) {
		this.positionSuid = positionSuid;
	}

	/**
	 * Генерация отчета
	 */
	public void generateReport() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		try {
			ReportModel entity = getService().createReportEntity(positionSuid,
					formatter.parse(startDateTextProperty.get()),
					formatter.parse(endDateTextProperty.get()));
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
	 * @throws JRException в случае ошибки
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
		InputStream jasperStream = getClass().getResourceAsStream("/report/battlePostReport.jrxml");
		JasperDesign jasperDesign = JRXmlLoader.load(jasperStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		return JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
	}

	/**
	 * Возвращает отчет в формате PDF
	 *
	 * @param aReport отчет
	 * @return отчет в формате PDF
	 * @throws JRException в случае ошибки
	 */
	public File getPDF(JasperPrint aReport, ReportModel aEntity) throws JRException {
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
	public File getHTML(JasperPrint aReport, ReportModel aEntity) throws JRException {
		File file = new File(
				Utils.getInstance().getTempDir() + aEntity.getPostName() + ".html");
		JasperExportManager.exportReportToHtmlFile(aReport, file.getAbsolutePath());
		return file;
	}

	/**
	 * Возвращает {@link#htmlFileReport}
	 *
	 * @return the {@link#htmlFileReport}
	 */
	public File getHtmlFileReport() {
		return htmlFileReport;
	}

	/**
	 * Возвращает {@link#pdfFileReport}
	 *
	 * @return the {@link#pdfFileReport}
	 */
	public File getPdfFileReport() {
		return pdfFileReport;
	}

	@Override
	public Class<IReportService> getTypeService() {
		return IReportService.class;
	}

}
