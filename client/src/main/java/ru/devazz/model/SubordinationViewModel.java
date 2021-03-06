package ru.devazz.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.utils.EntityConverter;

/**
 * Модель представления дерева подчиненности
 */
public class SubordinationViewModel
		extends PresentationModel<ISubordinationElementService, SubordinationElementModel> {

	/** Общий корень иерархии */
	private SubordinationElement root;

	/** Корневой элемент основного дерева */
	private SubordinationElement rootElement;

	/** Корневой элемент дерева подчиненных */
	private SubordinationElement subordiniersRoot;

	/** Выделенный элемент */
	private SubordinationElement selectedElement;

	/**
	 * Инициализирует модель
	 */
	@Override
	protected void initModel() {
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public SubordinationElement getRootElement() {
		return rootElement;
	}

	public SubordinationElement getSelectedElement() {
		return selectedElement;
	}

	public void setSelectedElement(SubordinationElement selectedElement) {
		this.selectedElement = selectedElement;
	}

	public void loadTree() {
		loadEntities();
		if (!listDataModelEntities.isEmpty()) {
			root = EntityConverter.getInstatnce()
					.convertSubElEntityToClientWrap(listDataModelEntities.get(0));
			rootElement = root.copy();
			rootElement.setSubElements(FXCollections.observableArrayList());

			subordiniersRoot = new SubordinationElement();
			subordiniersRoot.setName("Подчиненные");

			for (SubordinationElement value : root.getChildren()) {
				SubordinationElement subord = value.copy();
				subord.setSubElements(FXCollections.observableArrayList());
				rootElement.getChildren().add(subord);
			}
		}
	}

	/**
	 * Преобразует массив элементов в наблюдаемый список
	 *
	 * @param elements массив элементов
	 * @return наблюдаемый список
	 */
	public ObservableList<SubordinationElement> getObservableList(
			SubordinationElement... elements) {
		return FXCollections.observableArrayList(elements);

	}

	/**
	 * Возвращает список подчиненных заданного элемента
	 *
	 * @param aSubElSuid идентификатор заданного элемента подчиненности
	 * @return список подчиненных заданного элемента
	 */
	public ObservableList<SubordinationElement> getSubordiniers(Long aSubElSuid) {
		ObservableList<SubordinationElement> result = null;
		for (SubordinationElement element : root.getChildren()) {
			result = findSubordiniers(element, aSubElSuid);
			if (null != result) {
				break;
			}
		}
		return result;
	}

	/**
	 * Рекурсивная функция поиска подчиненных
	 *
	 * @param aSubEl проверяемый элемент подчиненности
	 * @param aSubElSuid идентификатор заданного элемента подчиненности
	 * @return список подчиненных заданного элемента
	 */
	private ObservableList<SubordinationElement> findSubordiniers(SubordinationElement aSubEl,
			Long aSubElSuid) {
		ObservableList<SubordinationElement> result = null;
		if (null != aSubEl) {
			if (aSubEl.getSuid().equals(aSubElSuid)) {
				result = aSubEl.getChildren();
			} else {
				for (SubordinationElement element : aSubEl.getChildren()) {
					if (element.getSuid().equals(aSubElSuid)) {
						result = element.getChildren();
						break;
					} else {
						result = findSubordiniers(element, aSubElSuid);
					}
				}
			}
		}
		return result;
	}

	@Override
	public Class<ISubordinationElementService> getTypeService() {
		return ISubordinationElementService.class;
	}

	/**
	 * Возвращает {@link#subordiniersRoot}
	 *
	 * @return the {@link#subordiniersRoot}
	 */
	public SubordinationElement getSubordiniersRoot() {
		return subordiniersRoot;
	}

}
