package ru.devazz.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import ru.devazz.entities.Event;
import ru.devazz.model.EventIndicatorViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление идикатора событий
 */
public class EventIndicatorView extends AbstractView<EventIndicatorViewModel> {

	/** Графический список событий */
	@FXML
	private ListView<Event> listView;

	/** Вкладка представления */
	@FXML
	private TabPane eventTabPane;

	/** Кнопка очистки списка событий */
	@FXML
	private Button clearListButton;

	/** Пункт контекстного меню "Перейти к журналу событий" */
	@FXML
	private MenuItem goToJournalItem;

	/** Пункт контекстного меню "Перейти к задаче" */
	@FXML
	private MenuItem goToTaskItem;

	/** Добавленное событие */
	private Event addedEvent;

	/** Таймлайн */
	private Timeline timeline;

	/**
	 * Устанавливает значение полю слушателю двойного клика по записи в списке
	 *
	 * @param doubleClickHandler слушатель
	 */
	public void setDoubleClickHandler(EventHandler<MouseEvent> doubleClickHandler) {
		listView.addEventFilter(MouseEvent.MOUSE_CLICKED, doubleClickHandler);
	}

	/**
	 * Устанавливает значение слушателю выбора пункта меню "Перейти к журналу"
	 *
	 * @param itemClickHandler слушатель
	 */
	public void setJournalItemClickHandler(EventHandler<ActionEvent> itemClickHandler) {
		goToJournalItem.addEventHandler(ActionEvent.ACTION, itemClickHandler);
	}

	/**
	 * Устанавливает значение слушателю выбора пункта меню "Перейти к журналу"
	 *
	 * @param itemClickHandler слушатель
	 */
	public void setTaskItemClickHandler(EventHandler<ActionEvent> itemClickHandler) {
		goToTaskItem.addEventHandler(ActionEvent.ACTION, itemClickHandler);
	}

	@Override
	@FXML
	public void initialize() {
		Bindings.bindBidirectional(listView.itemsProperty(), model.getListProperty());
		timeline = new Timeline(
				new KeyFrame(Duration.seconds(0.4),
						evt -> listView.getSelectionModel().select(null)),
				new KeyFrame(Duration.seconds(0.8),
						evt -> listView.getSelectionModel().select(addedEvent)));
		timeline.setCycleCount(Animation.INDEFINITE);
		listView.getItems().addListener((ListChangeListener<Event>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					addedEvent = change.getAddedSubList().get(0);
					timeline.play();
				}
			}
		});
		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			timeline.stop();
		});
	}

	@Override
	protected EventIndicatorViewModel createPresentationModel() {
		return new EventIndicatorViewModel();
	}

	@Override
	public TabPane getTabPane() {
		return eventTabPane;
	}

	/**
	 * Возвращает вкладку панели
	 *
	 * @return вкладка панели
	 */
	@Override
	public Tab getTab() {
		Tab tab = (!eventTabPane.getTabs().isEmpty()) ? eventTabPane.getTabs().get(0) : null;
		return tab;
	}

	/**
	 * Возвращает выделенное событие
	 *
	 * @return Событие
	 */
	public Event getSelection() {
		return listView.getSelectionModel().getSelectedItem();
	}

	/**
	 * Очистить список событий
	 */
	@FXML
	public void clearList() {
		model.deleteEvents(model.getListEvents());
	}

	/**
	 * Удалить выделенную запись
	 */
	@FXML
	public void deleteEvent() {
		List<Event> list = new ArrayList<>();
		list.add(getSelection());
		model.deleteEvents(list);
	}

}
