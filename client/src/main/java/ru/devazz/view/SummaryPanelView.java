package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ru.siencesquad.hqtasks.ui.entities.Task;
import ru.siencesquad.hqtasks.ui.model.SummaryPanelViewModel;
import ru.siencesquad.hqtasks.ui.utils.Utils;
import ru.siencesquad.hqtasks.ui.view.RootView.OpenTaskInSummaryHandler;

import java.io.IOException;

/**
 * Представление панели сводки по задачам
 */
public class SummaryPanelView extends AbstractView<SummaryPanelViewModel> {

	/** Корневая панель представления сводки */
	@FXML
	private SplitPane summaryPanelSplitPane;

	/** Корневой анчор */
	@FXML
	private AnchorPane rootAnchorPane;

	/** Лейбл наименования ДП */
	@FXML
	private Label subElLabel;

	/** Компонент вывода фото текущего ДЛ на заданном БП */
	@FXML
	private ImageView subElImage;

	/** Лейбл количества задач выволненых в срок */
	@FXML
	private Label successDoneAmountLabel;

	/** Лейбл количества задач выполненых с опозданием */
	@FXML
	private Label overdueDoneAmountLabel;

	/** Лейбл количества завершенных задач */
	@FXML
	private Label closedAmountLabel;

	/** Лейбл количества просроченных задач */
	@FXML
	private Label overdueAmountLabel;

	/** Лейбл количества проваленных задач */
	@FXML
	private Label failedAmountLabel;

	/** Лейбл количества задач в работе */
	@FXML
	private Label inWorkAmountLabel;

	/** Лейбл количества задач на доработке */
	@FXML
	private Label reworkAmountLabel;

	/** Компонент отображения входящих задач */
	@FXML
	private ListView<HBox> inListView;

	/** Компонент отображения исходящих задач */
	@FXML
	private ListView<HBox> outListView;

	/** Верхний лейбл ресайза */
	@FXML
	private Label topResizeLabel;

	/** Нижний лейбл ресайза */
	@FXML
	private Label bottomResizeLabel;

	/** Координаты клика мышкой на панели */
	private Point2D clickMouseLocation;

	/** Обработчик открытия задачи */
	private OpenTaskInSummaryHandler openTaskHandler;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
		Bindings.bindBidirectional(subElLabel.textProperty(), model.getSubElLabelProperty());
		Bindings.bindBidirectional(successDoneAmountLabel.textProperty(),
				model.getSuccessDoneAmountLabelProperty());
		Bindings.bindBidirectional(overdueDoneAmountLabel.textProperty(),
				model.getOverdueDoneAmountLabelProperty());
		Bindings.bindBidirectional(closedAmountLabel.textProperty(),
				model.getClosedAmountLabelProperty());
		Bindings.bindBidirectional(overdueAmountLabel.textProperty(),
				model.getOverdueAmountLabelProperty());
		Bindings.bindBidirectional(failedAmountLabel.textProperty(),
				model.getFailedAmountLabelProperty());
		Bindings.bindBidirectional(inWorkAmountLabel.textProperty(),
				model.getInWorkAmountLabelProperty());
		Bindings.bindBidirectional(reworkAmountLabel.textProperty(),
				model.getReworkAmountLabelProperty());
		model.getImageProperty()
				.addListener((ChangeListener<Image>) (observable, oldValue, newValue) -> {
					Platform.runLater(() -> {
						subElImage.setImage(newValue);
					});

				});
		model.getInTasks().addListener((ListChangeListener<Task>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (Task task : c.getAddedSubList()) {
						createTaskPanel(task, true);
					}
				} else if (c.wasRemoved()) {
					inListView.getItems().clear();
				}
			}

		});
		model.getOutTasks().addListener((ListChangeListener<Task>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (Task task : c.getAddedSubList()) {
						createTaskPanel(task, false);
					}
				} else if (c.wasRemoved()) {
					outListView.getItems().clear();
				}
			}

		});
		inListView.setFixedCellSize(40);
		outListView.setFixedCellSize(40);

		rootAnchorPane.addEventHandler(MouseEvent.DRAG_DETECTED, event -> {
			clickMouseLocation = new Point2D(event.getScreenX(), event.getScreenY());
		});

		bottomResizeLabel.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (null != clickMouseLocation) {
				Point2D location1 = new Point2D(event.getScreenX(), event.getScreenY());
				double newHeight = (rootAnchorPane.getHeight()
						+ (location1.getY() - clickMouseLocation.getY()));
				if (!((newHeight) < 200) && !((newHeight) > 500)) {
					rootAnchorPane.setPrefHeight(newHeight);
				}
			}
		});
	}

	/**
	 * Устанавливает значение полю {@link#openTaskHandler}
	 *
	 * @param openTaskHandler значение поля
	 */
	public void setOpenTaskHandler(OpenTaskInSummaryHandler openTaskHandler) {
		this.openTaskHandler = openTaskHandler;
	}

	/**
	 * Создает панель задачи
	 *
	 * @param aTask задача
	 * @param aInFlag признак создания входящих задач
	 */
	private void createTaskPanel(Task aTask, Boolean aInFlag) {
		Platform.runLater(() -> {
			try {
				SummaryTaskView view = Utils.getInstance().loadView(SummaryTaskView.class);
				if (null != view) {
					view.getModel().setTaskParams(aTask);
					view.addOpenTaskListener(openTaskHandler);
					if (aInFlag) {
						inListView.getItems().add(view.getRootPanel());
					} else {
						outListView.getItems().add(view.getRootPanel());
					}
				}
			} catch (IOException e) {
				// TODO Логирование
				e.printStackTrace();
			}
		});
	}

	/**
	 * Верхний ресайз
	 */
	@FXML
	private void topResize() {

	}

	/**
	 * Нижний рейсайз
	 */
	@FXML
	private void bottomResize() {

	}

	/**
	 * Возвращает {@link#summaryPanelSplitPane}
	 *
	 * @return the {@link#summaryPanelSplitPane}
	 */
	public AnchorPane getSummaryPanel() {
		return rootAnchorPane;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected SummaryPanelViewModel createPresentaionModel() {
		return new SummaryPanelViewModel();
	}

}
