package ru.devazz.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import ru.siencesquad.hqtasks.ui.entities.Event;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.model.EventJournalModel;
import ru.siencesquad.hqtasks.ui.utils.EventType;
import ru.siencesquad.hqtasks.ui.utils.dialogs.DialogUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Предсталение журнала событий
 */
public class EventJournalView extends AbstractView<EventJournalModel> {

	/** Контейнер вкладок журнала событий */
	@FXML
	private TabPane eventJournalTabPane;

	/** Основная вкладка журнала событий */
	@FXML
	private TableView<Event> eventsTable;

	/** Столбец журнала событий "Наименование события" */
	@FXML
	private TableColumn<Event, String> nameColumn;

	/** Столбец журнала событий "Дата события" */
	@FXML
	private TableColumn<Event, String> dateColumn;

	/** Столбец журнала событий "Тип события" */
	@FXML
	private TableColumn<Event, String> typeEventColumn;

	/** Столбец журнала событий "Автор" */
	@FXML
	private TableColumn<Event, String> authorColumn;

	/** Столбец журнала событий "Исполнитель" */
	@FXML
	private TableColumn<Event, String> executorColumn;

	/** Меню фильтрвции по типу события */
	@FXML
	private Menu eventTypeMenu;

	/** Пункт меню "удалёные задачи" для фильтра событий */
	@FXML
	private CheckMenuItem deleted;

	/** Пункт меню "созданные задачи" для фильтра событий */
	@FXML
	private CheckMenuItem created;

	/** Пункт меню "просроченные задачи" для фильтра событий */
	@FXML
	private CheckMenuItem overdue;

	/** Пункт меню "выполненные задачи" для фильтра событий */
	@FXML
	private CheckMenuItem done;

	@FXML
	private Menu dateFilterMenu;

	@FXML
	private CheckMenuItem dayFilterItem;

	@FXML
	private CheckMenuItem weekFilterItem;

	@FXML
	private CheckMenuItem monthFilterItem;

	@FXML
	private CheckMenuItem allTimeFilterItem;

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	@FXML
	public void initialize() {
		// Связывание данных
		Bindings.bindBidirectional(eventsTable.itemsProperty(), model.getDataProperty());

		// устанавливаем тип и значение которое должно хранится в колонке
		nameColumn.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
		dateColumn.setCellValueFactory(event -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
			property.setValue(dateFormat.format(event.getValue().getDate()));
			return property;
		});
		authorColumn.setCellValueFactory(event -> {
			SimpleStringProperty property = new SimpleStringProperty();
			SubordinationElement author = event.getValue().getAuthor();
			if (null != author) {
				property.setValue(author.getName());
			}
			return property;
		});

		executorColumn.setCellValueFactory(event -> {
			SimpleStringProperty property = new SimpleStringProperty();
			SubordinationElement executor = event.getValue().getExecutor();
			if (null != executor) {
				property.setValue(executor.getName());
			}
			return property;
		});

		typeEventColumn.setCellValueFactory(event -> {
			SimpleStringProperty property = new SimpleStringProperty();
			EventType type = event.getValue().getEventType();
			if (null != type) {
				property.setValue(type.getName());
			}
			return property;
		});
		refresh();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected EventJournalModel createPresentaionModel() {
		return new EventJournalModel();
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#getTabPane()
	 */
	@Override
	public TabPane getTabPane() {
		return eventJournalTabPane;
	}

	/**
	 * Возвращает вкладку панели
	 *
	 * @return вкладка панели
	 */
	@Override
	public Tab getTab() {
		Tab tab = (!eventJournalTabPane.getTabs().isEmpty()) ? eventJournalTabPane.getTabs().get(0)
				: null;
		return tab;
	}

	/**
	 * Выделяет запись в таблице
	 *
	 * @param aEvent событие
	 */
	public void setSelectedEvent(Event aEvent) {
		Platform.runLater(() -> {
			eventsTable.requestFocus();
			eventsTable.getSelectionModel().select(aEvent);
			eventsTable.getFocusModel().focus(eventsTable.getSelectionModel().getSelectedIndex());
			eventsTable.scrollTo(aEvent);
		});

	}

	/**
	 * Фильтрация по типу события
	 *
	 * @param event
	 */
	@FXML
	public void filterByEventType(ActionEvent event) {
		clearAllNotes();

		List<String> checkList = new ArrayList<>();

		for (MenuItem menuItem : eventTypeMenu.getItems()) {
			if (menuItem instanceof CheckMenuItem) {
				if (((CheckMenuItem) menuItem).isSelected()) {
					checkList.add(menuItem.getId());
				}
			}
		}

		eventsTable.setItems(
				FXCollections.observableArrayList(model.changedDataByEventType(checkList)));
	}

	/**
	 * Фильтрует события по дате
	 *
	 * @param event
	 */
	@FXML
	public void filterByDate(ActionEvent event) {
		clearAllNotes();

		String date = ((CheckMenuItem) event.getSource()).getId();
		for (MenuItem menuItem : dateFilterMenu.getItems()) {
			if (menuItem instanceof CheckMenuItem) {
				if (!menuItem.getId().equals(date)) {
					((CheckMenuItem) menuItem).setSelected(false);
				}
			}
		}

		model.setDataPropertyValue(model.changedDataByDate(date));

	}

	/**
	 * Фильтрует события по боевому посту
	 */
	@FXML
	public void filterByExecutor() {
		Consumer<? super List<SubordinationElement>> consumer = t -> {
			clearAllNotes();
			model.setDataPropertyValue(model.changedDataByExecutors(t));
		};
		DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, true, false);
	}

	/**
	 * Фильтрует события по боевому посту
	 */
	@FXML
	public void filterByAuthor() {
		Consumer<? super List<SubordinationElement>> consumer = t -> {
			clearAllNotes();
			model.setDataPropertyValue(model.changedDataByAuthors(t));
		};
		DialogUtils.getInstance().showSelectSubElsDialog(getStage(), consumer, true, false);
	}

	/**
	 * Очищает все записи таблицы
	 */
	@FXML
	public void clearAllNotes() {
		eventsTable.getItems().clear(); // удаляем все пункты в таблицы
		Label label = new Label("Нет записей в таблице!");
		label.setFont(new Font("Arial", 100));
		eventsTable.setPlaceholder(label);

	}

	/**
	 * Отображает все записи в таблице
	 */
	@FXML
	public void showAllNotes() {
		model.setDataPropertyValue(model.getAllEntries());

		for (MenuItem menuItem : eventTypeMenu.getItems()) {
			((CheckMenuItem) menuItem).setSelected(false);
		}

		for (MenuItem menuItem : dateFilterMenu.getItems()) {
			((CheckMenuItem) menuItem).setSelected(false);
		}
	}

}
