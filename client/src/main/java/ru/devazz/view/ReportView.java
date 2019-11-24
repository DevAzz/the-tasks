package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import jfxtras.scene.control.LocalDateTimeTextField;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.model.ReportViewModel;
import ru.devazz.server.ProxyFactory;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.LoadAnimation;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Представление отчетов
 */
public class ReportView extends AbstractView<ReportViewModel> {

	/** Контейнер вкладок отчетов */
	@FXML
	private TabPane reportTabPane;

	/** Вкладка отчетов */
	@FXML
	private Tab reportTab;

	/** Поля вывода наименования боевого поста */
	@FXML
	private TextField positionNameTextField;

	/** Кнопка открытия диалога выбора боевого поста */
	@FXML
	private Button searchBattlePostButton;

	/** Поле вывода даты начала */
	@FXML
	private LocalDateTimeTextField startDateTimeIntervalTextField;

	/** Поле вывода даты завершения */
	@FXML
	private LocalDateTimeTextField endDateTimeIntervalTextField;

	/** Кнопка генерации отчета */
	@FXML
	private Button generateReportButton;

	/** Контейнер отчета */
	@FXML
	private TitledPane reportPane;

	/** Лейбл наименования боевого поста */
	@FXML
	private Label nameButtlePostLabel;

	/** Лейбл количества выполненных задач */
	@FXML
	private Label countDoneTaskLabel;

	/** Лейбл количества просроченных задач */
	@FXML
	private Label countOverdueDoneTasksLabel;

	/** Лейбл количества проваленных задач */
	@FXML
	private Label countFaildTasksLabel;

	/** Лейбл количества задач в работе */
	@FXML
	private Label countInWorkTasksLabel;

	/** Лейбл количества завершенных задач */
	@FXML
	private Label closedAmountLabel;

	/** Лейбл количества просроченных задач */
	@FXML
	private Label overdueAmountLabel;

	/** Лейбл количества задач на доработке */
	@FXML
	private Label reworkAmountLabel;

	/** Кнопка вывода на печать */
	@FXML
	private Button printButton;

	/** Кнопка сохранения отчтета */
	@FXML
	private Button saveButton;

	/** Вкладка сформированного отчета */
	@FXML
	private TitledPane formedReport;

	/** Аккордион */
	@FXML
	private Accordion accordion;

	/** Браузер */
	@FXML
	private WebView webView;

	/** Анимация загрузки */
	private LoadAnimation loadAnimation;

	/** Корневая анчор панель представления отчетов */
	@FXML
	private AnchorPane reportAnchorPane;

	/** Корневая скролл панель */
	@FXML
	private ScrollPane reportScrollPane;

	/** Панель анимации */
	@FXML
	private AnchorPane animationBorder;

	/** Лейбл загрузки */
	@FXML
	private Label loadLabel;

	@Override
	public void initialize() {
		bindView();
		startDateTimeIntervalTextField
				.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
		endDateTimeIntervalTextField
				.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
		formedReport.setVisible(false);

		searchBattlePostButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			try {
				ISubordinationElementService subElsService = ProxyFactory.getInstance()
						.getService(ISubordinationElementService.class);

				Consumer<? super List<SubordinationElement>> consumer = t -> {
					model.setPositionNameTextPropertyValue(t.get(0).getName());
					model.setPositionSuid(t.get(0).getSuid());
				};

				ObservableList<SubordinationElement> listSubEls = FXCollections
						.observableArrayList();

				for (SubordinationElementModel entity : subElsService
						.getAll(Utils.getInstance().getCurrentUser().getSuid())) {
					if ((19 != entity.getSuid()) && (2 != entity.getSuid())) {
						listSubEls.add(EntityConverter.getInstatnce()
								.convertSubElEntityToClientWrap(entity));
					}
				}

				DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, false,
																 false);

			} catch (Exception e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
		// Печать отчета
		printButton.setOnMouseClicked(e -> {
			reportPane.setExpanded(false);
			reportPane.setDisable(true);
			formedReport.setExpanded(false);
			formedReport.setDisable(true);
			animationBorder.getParent().setVisible(true);
			loadLabel.setText("Подготовка к выводу на печать...");
			loadAnimation.start();
			model.printReport();
			loadAnimation.stop();
			animationBorder.getParent().setVisible(false);
			reportPane.setDisable(false);
			formedReport.setExpanded(true);
			formedReport.setDisable(false);
		});
		// Сохранение отчета
		saveButton.setOnMouseClicked(e -> {
			try {
				FileChooser fileChooser = new FileChooser();// Класс работы с диалогом выборки и
															// сохранения
				fileChooser.setTitle("Save Report");// Заголовок диалога
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"PDF files (*.pdf)", "*.pdf");// Расширение
				fileChooser.getExtensionFilters().add(extFilter);
				File report = fileChooser.showSaveDialog(stage);
				if (null != report) {
					model.saveReportAs(report.getAbsolutePath());
				}
			} catch (Exception e1) {
				// TODO Логирование
				e1.printStackTrace();
			}
		});

		printButton.setDisable(true);
		saveButton.setDisable(true);
		loadAnimation = new LoadAnimation();
		animationBorder.getChildren().addAll(loadAnimation.getCircles(50, 50, 35, 6, 100, 1200));
	}

	/**
	 * Генерация отчета
	 */
	@FXML
	private void generateReport() {
		animationBorder.getParent().setVisible(true);
		reportPane.setExpanded(false);
		reportPane.setDisable(true);
		formedReport.setVisible(false);
		loadLabel.setText("Формирование отчета...");
		loadAnimation.start();
		Thread thread = new Thread(() -> {
			model.generateReport();
			Platform.runLater(() -> {
				accordion.setExpandedPane(reportPane);
				File file = model.getHtmlFileReport();
				webView.getEngine().load("file:/" + file.getAbsolutePath());
				loadAnimation.stop();
				animationBorder.getParent().setVisible(false);
				formedReport.setVisible(true);
				reportPane.setDisable(false);
				printButton.setDisable(false);
				saveButton.setDisable(false);
			});
		});
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * Связывание представления с моделью
	 */
	private void bindView() {
		Bindings.bindBidirectional(positionNameTextField.textProperty(),
				model.getPositionNameTextProperty());
		Bindings.bindBidirectional(startDateTimeIntervalTextField.textProperty(),
				model.getStartDateTextProperty());
		Bindings.bindBidirectional(endDateTimeIntervalTextField.textProperty(),
				model.getEndDateTextProperty());
		Bindings.bindBidirectional(reportPane.textProperty(), model.getPositionNameTextProperty());
		Bindings.bindBidirectional(nameButtlePostLabel.textProperty(),
				model.getPositionNameTextProperty());
		Bindings.bindBidirectional(countDoneTaskLabel.textProperty(),
				model.getCountDoneTasksTextProperty());
		Bindings.bindBidirectional(countOverdueDoneTasksLabel.textProperty(),
				model.getCountOverDueTasksTextProperty());
		Bindings.bindBidirectional(countFaildTasksLabel.textProperty(),
				model.getCountFaildTasksTextProperty());
		Bindings.bindBidirectional(countInWorkTasksLabel.textProperty(),
				model.getCountInWorksTaskTextProperty());
		Bindings.bindBidirectional(closedAmountLabel.textProperty(),
				model.getClosedAmountLabelProperty());
		Bindings.bindBidirectional(overdueAmountLabel.textProperty(),
				model.getOverdueAmountLabel());
		Bindings.bindBidirectional(reworkAmountLabel.textProperty(), model.getReworkAmountLabel());
		Bindings.bindBidirectional(reportPane.disableProperty(), saveButton.disableProperty());

		// Доступность компонентов
		Bindings.bindBidirectional(generateReportButton.disableProperty(),
				model.getGenerateReportDisable());

		String errStyle = "textFieldErr";
		positionNameTextField.getStyleClass().add(errStyle);
		model.getStartDateTextProperty()
				.addListener(new ChangeDateTextListener(startDateTimeIntervalTextField));
		model.getEndDateTextProperty()
				.addListener(new ChangeDateTextListener(endDateTimeIntervalTextField));
		model.getPositionNameTextProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue.isEmpty()) {
						positionNameTextField.getStyleClass().add(errStyle);
					} else {
						positionNameTextField.getStyleClass().remove(errStyle);
					}
					String startDate = (null != model.getStartDateTextProperty().get())
							? model.getStartDateTextProperty().get()
							: "";
					String endDate = (null != model.getEndDateTextProperty().get())
							? model.getEndDateTextProperty().get()
							: "";
					model.setGenerateReportDisableValue(
							newValue.isEmpty() || startDate.isEmpty() || endDate.isEmpty());
				});

	}

	/**
	 * Слушатель изменения поля ввода дат
	 */
	private class ChangeDateTextListener implements ChangeListener<String> {

		/** Поле ввода даты */
		private LocalDateTimeTextField filed;

		/**
		 * Конструктор
		 *
		 * @param filed
		 */
		private ChangeDateTextListener(LocalDateTimeTextField filed) {
			super();
			this.filed = filed;
		}

		/**
		 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue,
		 *      Object, Object)
		 */
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String errStyle = "dateTimeTextFieldErr";
			String normal = "dateTimeTextFieldNormal";
			if (newValue.isEmpty()) {
				filed.getStyleClass().add(errStyle);
				filed.getStyleClass().remove(normal);
			} else {
				filed.getStyleClass().remove(errStyle);
				filed.getStyleClass().add(normal);
			}

			String startDate = (null != model.getStartDateTextProperty().get())
					? model.getStartDateTextProperty().get()
					: "";
			String endDate = (null != model.getEndDateTextProperty().get())
					? model.getEndDateTextProperty().get()
					: "";
			String name = (null != model.getPositionNameTextProperty().get())
					? model.getPositionNameTextProperty().get()
					: "";
			model.setGenerateReportDisableValue(
					name.isEmpty() || startDate.isEmpty() || endDate.isEmpty());
		}

	}

	@Override
	public TabPane getTabPane() {
		return reportTabPane;
	}

	/**
	 * Возвращает вкладку панели
	 *
	 * @return вкладка панели
	 */
	@Override
	public Tab getTab() {
		return reportTab;
	}

	@Override
	protected ReportViewModel createPresentaionModel() {
		return new ReportViewModel();
	}

}
