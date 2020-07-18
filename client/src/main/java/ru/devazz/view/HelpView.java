package ru.devazz.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import ru.devazz.model.HelpViewModel;
import ru.devazz.server.api.model.HelpModel;

/**
 * Представление справки
 */
public class HelpView extends AbstractView<HelpViewModel> {

	/** Корневой композит */
	@FXML
	public AnchorPane rootPane;

	/** Список заголовков */
	@FXML
	public ListView<HelpModel> headsList;

	/** Текст справки */
	@FXML
	public TextArea helpTextArea;

	/** Общий разделитель */
	@FXML
	public SplitPane commonSplitPane;

	/** Стартовая строка */
	private String defaultString = "                                   ИНСТРУКЦИЯ ПОЛЬЗОВАТЕЛЯ “Менеджер задач”\r\n"
			+ "\r\n"
			+ "     Менеджер задач – это программный продукт с дружественным интерфейсом пользователя, предназначенный для управления задачами отдельно взятой организации. В его функционал входит создание (постановка), удаление, редактирование, поиск задач; создание и сохранение отчётов о выполненных задачах, уведомления в режиме реального времени и многое другое.\r\n"
			+ "";

	/**
	 * Конструктор
	 */
	public HelpView() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the rootPane
	 */
	public AnchorPane getRootPane() {
		return rootPane;
	}

	/**
	 * Устанавливает значение полю rootPane
	 *
	 * @param rootPane значение поле
	 */
	public void setRootPane(AnchorPane rootPane) {
		this.rootPane = rootPane;
	}

	@Override
	public void initialize() {
		Bindings.bindBidirectional(headsList.itemsProperty(), model.getHelpEntityListProperty());
		Bindings.bindBidirectional(helpTextArea.textProperty(), model.getHelpTextProperty());

		headsList.setOnMouseClicked(event -> {
			model.setSelection(headsList.getSelectionModel().getSelectedItem());
		});

		helpTextArea.setText(defaultString);

		rootPane.setOnKeyPressed(event -> {
			if (KeyCode.ESCAPE.equals(event.getCode())) {
				helpTextArea.setText(defaultString);
			}
			headsList.getSelectionModel().clearSelection();
		});

		headsList.setOnKeyPressed(event -> {
			if (KeyCode.ESCAPE.equals(event.getCode())) {
				helpTextArea.setText(defaultString);
			}
			headsList.getSelectionModel().clearSelection();
		});

	}

	@Override
	protected HelpViewModel createPresentationModel() {
		// TODO Auto-generated method stub
		return new HelpViewModel();
	}

}
