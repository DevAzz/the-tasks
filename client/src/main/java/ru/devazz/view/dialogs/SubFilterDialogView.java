package ru.devazz.view.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.model.dialogmodel.SubFilterDialogModel;
import ru.siencesquad.hqtasks.ui.view.AbstractView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление диалога выбора подразделений фильтра журнала событий по боевым
 * постам
 */
public class SubFilterDialogView extends AbstractView<SubFilterDialogModel> {

	/** Корневой контейнер */
	@FXML
	private AnchorPane rootPaneDialog;

	/** Представление списка кнопок выбора подразделения */
	@FXML
	private ListView<CheckBox> listSub;

	/** Режим выбора нескольких позиций */
	private Boolean multiplieSelection;

	/** Выбранные чек-боксы */
	private List<CheckBox> selectedCheckBoxes = new ArrayList<>();

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#initialize()
	 */
	@Override
	public void initialize() {
	}

	/**
	 * Инициализирует список элементов подлежащих выбору
	 */
	public void initListView(Boolean aMultiplySelectionMode) {
		ObservableList<CheckBox> listCheckBoxes = FXCollections.observableArrayList();
		model.loadEntities();
		for (SubordinationElement element : model.getAllSubList()) {
			if (null != element) {
				CheckBox box = new CheckBox(element.getName());
				box.selectedProperty()
						.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
							if (newValue) {
								if (!model.selectedSubsContains(box.getText())) {
									model.getSelectedSubList().add(element);
									selectedCheckBoxes.add(box);
								}
							} else {
								model.getSelectedSubList().remove(element);
								selectedCheckBoxes.remove(box);
							}

						});
				listCheckBoxes.add(box);
			}
		}
		listSub.setItems(listCheckBoxes);

		if (!aMultiplySelectionMode)

		{
			listSub.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				listSub.requestFocus();
				if (2 == selectedCheckBoxes.size()) {
					CheckBox selectedBox = selectedCheckBoxes.get(1);
					for (CheckBox box : listSub.getItems()) {
						box.setSelected(false);
					}
					for (CheckBox box : listSub.getItems()) {
						if (box.equals(selectedBox)) {
							box.setSelected(true);
						}
					}
				}
			});
		} else {
			for (SubordinationElement element : model.getSelectedSubList()) {
				CheckBox box = getCheckBoxByName(element.getName());
				if (null != box) {
					box.setSelected(true);
				}
			}
		}
	}

	/**
	 * Возвращает чек-бокс по имени
	 *
	 * @param aName имя
	 * @return чек-бокс
	 */
	private CheckBox getCheckBoxByName(String aName) {
		CheckBox box = null;
		for (CheckBox value : listSub.getItems()) {
			if (aName.equals(value.getText())) {
				box = value;
			}
		}
		return box;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.view.AbstractView#createPresentaionModel()
	 */
	@Override
	protected SubFilterDialogModel createPresentaionModel() {
		return new SubFilterDialogModel();
	}

	/**
	 * Возвращает {@link#rootPane}
	 *
	 * @return the {@link#rootPane}
	 */
	public AnchorPane getRootPane() {
		return rootPaneDialog;
	}

	/**
	 * Устанавливает значение полю {@link#rootPane}
	 *
	 * @param rootPane значение поля
	 */
	public void setRootPane(AnchorPane rootPane) {
		this.rootPaneDialog = rootPane;
	}

	/**
	 * Возвращает {@link#multiplieSelection}
	 *
	 * @return the {@link#multiplieSelection}
	 */
	public Boolean getMultiplieSelection() {
		return multiplieSelection;
	}

	/**
	 * Устанавливает значение полю {@link#multiplieSelection}
	 *
	 * @param multiplieSelection значение поля
	 */
	public void setMultiplieSelection(Boolean multiplieSelection) {
		this.multiplieSelection = multiplieSelection;
	}

}
