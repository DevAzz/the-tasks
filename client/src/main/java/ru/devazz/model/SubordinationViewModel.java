package ru.devazz.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.subel.SubordinatioElementServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.SubordinationElementEntity;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.utils.EntityConverter;

/**
 * Модель представления дерева подчиненности
 */
public class SubordinationViewModel
		extends PresentationModel<SubordinatioElementServiceRemote, SubordinationElementEntity> {

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

	/**
	 * Возвращает {@link#rootElement}
	 *
	 * @return the {@link#rootElement}
	 */
	public SubordinationElement getRootElement() {
		return rootElement;
	}

	/**
	 * Возвращает {@link#selectedElement}
	 *
	 * @return the {@link#selectedElement}
	 */
	public SubordinationElement getSelectedElement() {
		return selectedElement;
	}

	/**
	 * Устанавливает значение полю {@link#selectedElement}
	 *
	 * @param selectedElement значение поля
	 */
	public void setSelectedElement(SubordinationElement selectedElement) {
		this.selectedElement = selectedElement;
	}

	/**
	 * Загружает дерево элементов поддчиненности
	 *
	 * @return корневой элемент дерева
	 */
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

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<SubordinatioElementServiceRemote> getTypeService() {
		return SubordinatioElementServiceRemote.class;
	}

	/**
	 * Возвращает {@link#root}
	 *
	 * @return the {@link#root}
	 */
	public SubordinationElement getRoot() {
		return root;
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
