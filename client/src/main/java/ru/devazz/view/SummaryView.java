package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import jfxtras.scene.control.LocalDateTextField;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.model.SummaryViewModel;
import ru.devazz.server.api.model.ReportModel;
import ru.devazz.utils.Utils;
import ru.devazz.utils.dialogs.DialogUtils;
import ru.devazz.widgets.CustomTimeIntervalView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * Представлене сводки по задачам
 */
public class SummaryView extends AbstractView<SummaryViewModel> {

	/** Корневая панель вкладок */
	@FXML
	private TabPane summaryTabPane;

	/** Корневая вкладка */
	@FXML
	private Tab summaryTab;

	/** Компонент отображения панелей */
	@FXML
	private ListView<AnchorPane> listPanes;

	/** Лейбл даты */
	@FXML
	private Label dateLabel;

	/** Пункт меню выбора интервала времени */
	@FXML
	private CustomMenuItem selectTimeIntervalMenuItem;

	/** Выбор определенного дня */
	@FXML
	private LocalDateTextField selectCustomDayField;

	/** Маскировочный бордер */
	@FXML
	private BorderPane maskBorderPane;

	/** панель отображения анимации загрузки */
	@FXML
	private AnchorPane loadAnchorPane;

	/** Обработчик открытия задачи */
	private RootView.OpenTaskInSummaryHandler openTaskHandler;

	@Override
	public void initialize() {
		Bindings.bindBidirectional(dateLabel.textProperty(), model.getDateLabelProperty());
		listPanes.setPadding(new Insets(15.0));
		try {
			CustomTimeIntervalView customTimeView = Utils.getInstance()
					.loadView(CustomTimeIntervalView.class);
			model.setCustomTimeIntervalModel(customTimeView.getModel());
			customTimeView.setSearchRunnable(model.createSearchRunnable());

			selectTimeIntervalMenuItem.setContent(customTimeView.getCustomFilterDatePanel());
			selectTimeIntervalMenuItem.setHideOnClick(false);
		} catch (IOException e) {
			// TODO Логирование
			e.printStackTrace();
		}

		selectCustomDayField.localDateProperty()
				.addListener((ChangeListener<LocalDate>) (observable, oldValue, newValue) -> {
					model.selectCustomDay(newValue);
				});

		model.getPanelsList().addListener((ListChangeListener<ReportModel>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (ReportModel entity : c.getAddedSubList()) {
						createSummaryPanel(entity);
					}
				} else if (c.wasRemoved()) {
					Platform.runLater(() -> {
						listPanes.getItems().clear();
					});
				}
			}

		});
	}

	/**
	 * Выбор текщуего дня
	 */
	@FXML
	private void selectCurrentDayInterval() {
		model.selectCurrentDayInterval();
	}

	/**
	 * Выбор текущей недели
	 */
	@FXML
	private void selectWeekInterval() {
		model.selectCurrentWeekInterval();
	}

	/**
	 * Выбор текущего месяца
	 */
	@FXML
	private void selectMonthInterval() {
		model.selectCurrentMonthInterval();
	}

	/**
	 * Выбор должностей
	 */
	@FXML
	private void selectSubEls() {
		Consumer<? super List<SubordinationElement>> consumer = t -> {
			model.setSubElSuidList(FXCollections.observableArrayList(t));
		};
		DialogUtils.getInstance().showSelectSubElsDialogWithSelectedSubs(getStage(),
																		 model.getSubElSuidList(), consumer, true, false);
	}

	/**
	 * Создает панель сводки для должности
	 *
	 * @param entity сущность отчета по должности
	 * @throws IOException в случае ошибки загрузки преставления панели сводки
	 */
	private void createSummaryPanel(ReportModel entity) {
		Platform.runLater(() -> {
			try {
				SummaryPanelView view = Utils.getInstance().loadView(SummaryPanelView.class);
				view.setOpenTaskHandler(openTaskHandler);
				if (null != view) {
					view.getModel().setReportEntity(entity);
					listPanes.getItems().add(view.getSummaryPanel());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Возвращает основную вкладку представления
	 *
	 * @return основная вкладка
	 */
	@Override
	public Tab getTab() {
		return summaryTab;
	}

	/**
	 * Возвращает панель вкладок
	 *
	 * @return панель вкладок
	 */
	@Override
	public TabPane getTabPane() {
		return summaryTabPane;
	}

	@Override
	protected SummaryViewModel createPresentaionModel() {
		return new SummaryViewModel();
	}

	/**
	 * Устанавливает значение полю {@link#openTaskHandler}
	 *
	 * @param openTaskHandler значение поля
	 */
	public void setOpenTaskHandler(RootView.OpenTaskInSummaryHandler openTaskHandler) {
		this.openTaskHandler = openTaskHandler;
	}

}
