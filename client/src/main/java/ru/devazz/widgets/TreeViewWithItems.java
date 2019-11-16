package ru.devazz.widgets;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import ru.siencesquad.hqtasks.ui.interfaces.HierarchyData;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends the {@link TreeView} to use items as a data source.
 * <p/>
 * This allows you to treat a {@link TreeView} in a similar way as a
 * {@link javafx.scene.control.ListView} or
 * {@link javafx.scene.control.TableView}.
 * <p/>
 * Each item in the list must implement the {@link HierarchyData} interface, in
 * order to map the recursive nature of the tree data to the tree view.
 * <p/>
 * Each change in the underlying data (adding, removing, sorting) will then be
 * automatically reflected in the UI.
 *
 */
public class TreeViewWithItems<T extends HierarchyData<T>> extends TreeView<T> {
	/**
	 * Keep hard references for each listener, so that they don't get garbage
	 * collected too soon.
	 */
	private final Map<TreeItem<T>, ListChangeListener<T>> hardReferences = new HashMap<>();

	/**
	 * Also store a reference from each tree item to its weak listeners, so that the
	 * listener can be removed, when the tree item gets removed.
	 */
	private final Map<TreeItem<T>, WeakListChangeListener<T>> weakListeners = new HashMap<>();

	private ObjectProperty<ObservableList<? extends T>> items = new SimpleObjectProperty<>(this, "items");

	public TreeViewWithItems() {
		super();
		init();
	}

	/**
	 * Creates the tree view.
	 *
	 * @param root The root tree item.
	 * @see TreeView#TreeView(javafx.scene.control.TreeItem)
	 */
	public TreeViewWithItems(TreeItem<T> root) {
		super(root);
		init();
	}

	/**
	 * Initializes the tree view.
	 */
	private void init() {
		rootProperty().addListener((ChangeListener<TreeItem<T>>) (observableValue, oldRoot, newRoot) -> {
			clear(oldRoot);
			updateItems();
		});

		setItems(FXCollections.<T>observableArrayList());

		// Do not use ChangeListener, because it won't trigger if old list equals new
		// list (but in fact different references).
		items.addListener((InvalidationListener) observable -> {
			clear(getRoot());
			updateItems();
		});
	}

	/**
	 * Removes all listener from a root.
	 *
	 * @param root The root.
	 */
	private void clear(TreeItem<T> root) {
		if (root != null) {
			for (TreeItem<T> treeItem : root.getChildren()) {
				removeRecursively(treeItem);
			}

			removeRecursively(root);
			root.getChildren().clear();
		}
	}

	/**
	 * Updates the items.
	 */
	private void updateItems() {

		if (getItems() != null) {
			for (T value : getItems()) {
				getRoot().getChildren().add(addRecursively(value));
			}

			ListChangeListener<T> rootListener = getListChangeListener(getRoot().getChildren());
			WeakListChangeListener<T> weakListChangeListener = new WeakListChangeListener<>(rootListener);
			hardReferences.put(getRoot(), rootListener);
			weakListeners.put(getRoot(), weakListChangeListener);
			getItems().addListener(weakListChangeListener);
		}
	}

	/**
	 * Gets a {@link javafx.collections.ListChangeListener} for a {@link TreeItem}.
	 * It listens to changes on the underlying list and updates the UI accordingly.
	 *
	 * @param treeItemChildren The associated tree item's children list.
	 * @return The listener.
	 */
	private ListChangeListener<T> getListChangeListener(final ObservableList<TreeItem<T>> treeItemChildren) {
		return change -> {
			while (change.next()) {
				if (change.wasUpdated()) {
					// http://javafx-jira.kenai.com/browse/RT-23434
					continue;
				}
				if (change.wasRemoved()) {
					for (int i1 = change.getRemovedSize() - 1; i1 >= 0; i1--) {
						removeRecursively(treeItemChildren.remove(change.getFrom() + i1));
					}
				}
				// If items have been added
				if (change.wasAdded()) {
					// Get the new items
					for (int i2 = change.getFrom(); i2 < change.getTo(); i2++) {
						treeItemChildren.add(i2, addRecursively(change.getList().get(i2)));
					}
				}
				// If the list was sorted.
				if (change.wasPermutated()) {
					// Store the new order.
					Map<Integer, TreeItem<T>> tempMap = new HashMap<>();

					for (int i3 = change.getTo() - 1; i3 >= change.getFrom(); i3--) {
						int a = change.getPermutation(i3);
						tempMap.put(a, treeItemChildren.remove(i3));
					}

					getSelectionModel().clearSelection();

					// Add the items in the new order.
					for (int i4 = change.getFrom(); i4 < change.getTo(); i4++) {
						treeItemChildren.add(tempMap.remove(i4));
					}
				}
			}
		};
	}

	/**
	 * Removes the listener recursively.
	 *
	 * @param item The tree item.
	 */
	private TreeItem<T> removeRecursively(TreeItem<T> item) {
		if ((item.getValue() != null) && (item.getValue().getChildren() != null)) {

			if (weakListeners.containsKey(item)) {
				item.getValue().getChildren().removeListener(weakListeners.remove(item));
				hardReferences.remove(item);
			}
			for (TreeItem<T> treeItem : item.getChildren()) {
				removeRecursively(treeItem);
			}
		}
		return item;
	}

	/**
	 * Adds the children to the tree recursively.
	 *
	 * @param value The initial value.
	 * @return The tree item.
	 */
	private TreeItem<T> addRecursively(T value) {

		TreeItem<T> treeItem = new TreeItem<>();
		treeItem.setValue(value);
		treeItem.setExpanded(true);

		if ((value != null) && (value.getChildren() != null)) {
			ListChangeListener<T> listChangeListener = getListChangeListener(treeItem.getChildren());
			WeakListChangeListener<T> weakListener = new WeakListChangeListener<>(listChangeListener);
			value.getChildren().addListener(weakListener);

			hardReferences.put(treeItem, listChangeListener);
			weakListeners.put(treeItem, weakListener);
			for (T child : value.getChildren()) {
				treeItem.getChildren().add(addRecursively(child));
			}
		}
		return treeItem;
	}

	/**
	 * Возвращает дочерние элементы
	 *
	 * @return дочерние элементы
	 */
	public ObservableList<? extends T> getItems() {
		return items.get();
	}

	/**
	 * Sets items for the tree.
	 *
	 * @param items The list.
	 */
	public void setItems(ObservableList<? extends T> items) {
		this.items.set(items);
	}
}
