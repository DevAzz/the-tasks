package ru.devazz.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import ru.siencesquad.hqtasks.ui.model.LegendOfIconsViewModel;

public class LegendOfIconsView extends AbstractView<LegendOfIconsViewModel> {
	/** Вкладка с легендой */
	@FXML
	private TabPane tabPane;

	/** Список с легендой статусов */
	@FXML
	private ListView<Label> statusPlace;

	/** Список с легендой приоритетов */
	@FXML
	private ListView<Label> priorityPlace;

	/**
	 * Инициализация
	 */
	@Override
	public void initialize() {
		addPriority("– приоритет “Повседневный”", "legend_everyday");
		addPriority("–  приоритет “Низкий”", "legend_priority1");
		addPriority("– приоритет “Средний”", "legend_priority2");
		addPriority("– приоритет “Высокий”", "legend_priority3");
		addPriority("– приоритет “Критический”", "legend_priority4");
		addStatus("–  статус “В работе”", "legend_inwork");
		addStatus("–  статус “Завершена”", "legend_complited");
		addStatus("–  статус “Закрыта”", "legend_complitedAndLocked");
		addStatus("–  статус “Не выполнена”", "legend_faild");
		addStatus("–  статус “На доработке”", "legend_rework");
		addStatus("–  статус “Просрочена”", "legend_timeoutcomplited");
	}

	/**
	 * Функуция добавления статуса в список легенд
	 *
	 * @param name - текст
	 * @param style - стиль
	 */
	private void addStatus(String name, String style) {
		Label legendLabel = new Label();
		legendLabel.setText(name);
		legendLabel.getStyleClass().add(style);
		statusPlace.getItems().add(legendLabel);
	}

	/**
	 * Функуция добавления приоритета в список легенд
	 *
	 * @param name - текст
	 * @param style - стиль
	 */
	private void addPriority(String name, String style) {
		Label legendLabel = new Label();
		legendLabel.setText(name);
		legendLabel.getStyleClass().add(style);
		priorityPlace.getItems().add(legendLabel);
	}

	/**
	 * @return the tabPane
	 */
	@Override
	public TabPane getTabPane() {
		return tabPane;
	}

	/**
	 * @param tabPane the tabPane to set
	 */
	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	/**
	 * @return the statusPlace
	 */
	public ListView<Label> getStatusPlace() {
		return statusPlace;
	}

	/**
	 * @param statusPlace the statusPlace to set
	 */
	public void setStatusPlace(ListView<Label> statusPlace) {
		this.statusPlace = statusPlace;
	}

	/**
	 * @return the priorityPlace
	 */
	public ListView<Label> getPriorityPlace() {
		return priorityPlace;
	}

	/**
	 * @param priorityPlace the priorityPlace to set
	 */
	public void setPriorityPlace(ListView<Label> priorityPlace) {
		this.priorityPlace = priorityPlace;
	}

	@Override
	protected LegendOfIconsViewModel createPresentaionModel() {
		// TODO Auto-generated method stub
		return new LegendOfIconsViewModel();
	}

}
