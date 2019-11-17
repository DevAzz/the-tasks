package ru.devazz.view;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.input.MouseEvent;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.interfaces.SelectableObject;
import ru.devazz.model.SubordinationViewModel;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.widgets.TreeViewWithItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Представление дерева подчиненности
 */
public class SubordinationTreeView extends AbstractView<SubordinationViewModel>
		implements SelectableObject {

	/** Контейнер вкладок дерева поддчиненности */
	@FXML
	private TabPane tabPaneSubTree;

	/** Пункт контекстного меню "Создать задачу" */
	@FXML
	private MenuItem createTaskMenuItem;

	/** Пункт контекстного меню "Отчет по задачам" */
	@FXML
	private MenuItem generateTasksReport;

	/** Дерево подчиненности */
	@FXML
	private TreeViewWithItems<SubordinationElement> leftTreeView;

	/** Правое дерево */
	@FXML
	private TreeViewWithItems<SubordinationElement> rightTreeView;

	/** Ханддер создания задачи */
	@FXML
	private EventHandler<ActionEvent> createTaskHandler;

	/** Лейбл процесса загрузки дерева */
	@FXML
	private Label downloadLabel;

	/** Контекстное меню дерева подчиненности */
	@FXML
	private ContextMenu subTreeMenu;

	/** Панель разделителя дерева подчиненности */
	@FXML
	private SplitPane subTreeSplitPane;

	/** Признак недоступности разделителя дерева */
	private Boolean disableDivider = true;

	/**
	 * Возвращает {@link#subTree}
	 *
	 * @return the {@link#subTree}
	 */
	public TreeView<SubordinationElement> getTreeView() {
		return leftTreeView;
	}

	@Override
	protected SubordinationViewModel createPresentaionModel() {
		return new SubordinationViewModel();
	}

	@Override
	public void initialize() {
		model.getListEntities().addListener((ListChangeListener<SubordinationElementModel>) c -> {
			loadTreeView();
		});
		Thread thread = new Thread(() -> model.loadTree());
		thread.setDaemon(true);
		thread.start();
		subTreeMenu.setOnShowing(event -> {
			SubordinationElement element = getSelection();
			if (null != element) {
				if (getRoot().getValue().equals(element)) {
					subTreeMenu.getItems().get(0).setVisible(false);
					if (element.getSuid().equals(1L)) {
						subTreeMenu.getItems().get(1).setVisible(false);
					}
				} else {
					subTreeMenu.getItems().get(0).setVisible(true);
					subTreeMenu.getItems().get(1).setVisible(true);
				}
			}
		});

		subTreeSplitPane.getDividers().get(0).setPosition(1);
	}

	/**
	 * Загрузка дерева подчиненности
	 */
	private void loadTreeView() {
		Platform.runLater(() -> {
			downloadLabel.setVisible(false);
			TreeItem<SubordinationElement> root = new TreeItem<>(model.getRootElement());
			root.setExpanded(true);
			leftTreeView.setRoot(root);
			leftTreeView.setItems(model.getRootElement().getChildren());

			TreeItem<SubordinationElement> subordiniersRoot = new TreeItem<>(
					model.getSubordiniersRoot());
			root.setExpanded(true);
			rightTreeView.setRoot(subordiniersRoot);

			leftTreeView.setOnMouseClicked(event -> {
				// Обработка выделения элемента подчиненности
				selectElement(event);

				// Установка значений дереву подчиненных
				TreeItem<SubordinationElement> item = leftTreeView.getSelectionModel()
						.getSelectedItem();
				SubordinationElement element = item.getValue();
				ObservableList<SubordinationElement> subordiniers = model
						.getSubordiniers(element.getSuid());

				rightTreeView.setItems(subordiniers);
				rightTreeView.getRoot().setExpanded(true);

				// Управление разделителем
				if ((null == subordiniers) || (subordiniers.isEmpty())) {
					subTreeSplitPane.getDividers().get(0).setPosition(1);
					disableSplitDivider(true);
				} else {
					subTreeSplitPane.getDividers().get(0).setPosition(0.5);
					disableSplitDivider(false);
				}
			});
			rightTreeView.setOnMouseClicked(event -> {
				selectElement(event);
			});

			setSelection(root.getValue());
		});
	}

	/**
	 * Управляет доступностью разделителя сплит панели
	 *
	 * @param aDisable доступность
	 */
	public void disableSplitDivider(Boolean aDisable) {
		Set<Node> nodes = subTreeSplitPane.lookupAll(".split-pane-divider");
		setDisableDivider(aDisable);
		for (Node node : nodes) {
			node.setMouseTransparent(aDisable);
		}
	}

	/**
	 * Выделяет эелмент дерева
	 *
	 * @param event событие клика по дереву
	 */
	@SuppressWarnings("unchecked")
	private void selectElement(MouseEvent event) {
		TreeViewWithItems<SubordinationElement> tree = (TreeViewWithItems<SubordinationElement>) event
				.getSource();
		TreeItem<SubordinationElement> item = tree.getSelectionModel().getSelectedItem();
		if (null != item) {
			SubordinationElement element = item.getValue();
			if (null != element.getSuid()) {
				model.setSelectedElement(element);
				setSelection(item.getValue());
			}
		}
	}

	@Override
	public TabPane getTabPane() {
		return tabPaneSubTree;
	}

	/**
	 * Устанавливает значение полю {@link#createTaskHandler}
	 *
	 * @param createTaskHandler значение поля
	 */
	public void setCreateTaskHandler(EventHandler<ActionEvent> createTaskHandler) {
		this.createTaskHandler = createTaskHandler;
		createTaskMenuItem.setOnAction(createTaskHandler);
	}

	/**
	 * Устанавливает обработчик открытия формы Генерации отчета
	 *
	 * @param aGenerateTasksReportHandler обработчик открытия формы Генерации отчета
	 */
	public void setGenerateReportHandler(EventHandler<ActionEvent> aGenerateTasksReportHandler) {
		generateTasksReport.setOnAction(aGenerateTasksReportHandler);
	}

	/**
	 * Возвращает выделенный элемент
	 *
	 * @return выделенный элемент
	 */
	public SubordinationElement getSelection() {
		return model.getSelectedElement();
	}

	/**
	 * Рекурсивная функция построения списка всех элементов дерева, заданного
	 * корневым элементом
	 *
	 * @param aItem корневой элемент
	 * @return список элементов дерева
	 */
	private List<TreeItem<SubordinationElement>> getTreeItemList(
			TreeItem<SubordinationElement> aItem) {
		List<TreeItem<SubordinationElement>> result = new ArrayList<>();
		result.add(aItem);
		for (TreeItem<SubordinationElement> item : aItem.getChildren()) {
			result.addAll(getTreeItemList(item));
		}
		return result;
	}

	/**
	 * Функция поиска элемента дерева по модели данных
	 *
	 * @param aElement модель данных
	 * @return найденный элемент дерева
	 */
	private TreeItem<SubordinationElement> getTreeItemBySubElement(SubordinationElement aElement) {
		TreeItem<SubordinationElement> result = null;
		for (TreeItem<SubordinationElement> item : getTreeItemList(leftTreeView.getRoot())) {
			if (item.getValue().equals(aElement)) {
				result = item;
				break;
			}
		}
		return result;
	}

	/**
	 * Выделяет элемент дерева
	 *
	 * @param aElement элемент
	 */
	public void setSelection(SubordinationElement aElement) {
		leftTreeView.getSelectionModel().select(getTreeItemBySubElement(aElement));
		model.setSelectedElement(aElement);
		fireSelect(aElement);
	}

	/**
	 * Выделяет элемент дерева
	 *
	 * @param aItem элемент
	 */
	public void setSelection(TreeItem<SubordinationElement> aItem) {
		leftTreeView.getSelectionModel().select(aItem);
		model.setSelectedElement(aItem.getValue());
		fireSelect(aItem.getValue());
	}

	/**
	 * Возвращает корневой элемент дерева
	 *
	 * @return корневой элемент дерева
	 */
	public TreeItem<SubordinationElement> getRoot() {
		return leftTreeView.getRoot();
	}

	/**
	 * Возвращает {@link#disableDivider}
	 *
	 * @return the {@link#disableDivider}
	 */
	public Boolean getDisableDivider() {
		return disableDivider;
	}

	/**
	 * Устанавливает значение полю {@link#disableDivider}
	 *
	 * @param disableDivider значение поля
	 */
	public void setDisableDivider(Boolean disableDivider) {
		this.disableDivider = disableDivider;
	}

	/**
	 * Возвращает разделитель дерева
	 *
	 * @return разделитель
	 */
	public Divider getDivider() {
		return subTreeSplitPane.getDividers().get(0);
	}

}
