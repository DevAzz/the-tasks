package ru.devazz.model.dialogmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.model.PresentationModel;
import ru.devazz.server.api.ISubordinationElementService;
import ru.devazz.server.api.model.SubordinationElementModel;
import ru.devazz.utils.EntityConverter;
import ru.devazz.utils.Utils;

import java.util.ArrayList;

/**
 * Модель представления диалога выбора подразделений фильтра журнала событий по должностям
 */
public class SubFilterDialogModel
		extends PresentationModel<ISubordinationElementService, SubordinationElementModel> {

	/** Список, содержащий подразделения, подлежащие выбору */
	private ObservableList<SubordinationElement> allSubList;

	/** Список, содержащий выбранные подразделения */
	private ObservableList<SubordinationElement> selectedSubList;

	/** Флаг использования диалога в форме поиска */
	private Boolean searchFlag = false;

	@Override
	protected void initModel() {
		allSubList = FXCollections.observableArrayList();
		selectedSubList = FXCollections.observableArrayList();
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	@Override
	public void loadEntities() {
		if (!searchFlag) {
			SubordinationElement root = EntityConverter.getInstatnce()
					.convertSubElEntityToClientWrap(new ArrayList<>(
							service.getAll(Utils.getInstance().getCurrentUser().getSuid())).get(0));
			addAllSubEls(root);
		} else {
			for (SubordinationElementModel entity : service.getTotalSubElList()) {
				allSubList
						.add(EntityConverter.getInstatnce().convertSubElEntityToClientWrap(entity));
			}

		}
		allSubList.sort((o1, o2) -> {
			int result = 0;
			if (o1.getRoleSuid() < o2.getRoleSuid()) {
				result = -1;
			} else {
				result = 1;
			}
			return result;
		});
	}

	/**
	 * Рекурсивная функция добавления элементов подчиненности в список
	 *
	 * @param aElement элемент подчиненности
	 */
	private void addAllSubEls(SubordinationElement aElement) {
		if (!allSubList.contains(aElement)) {
			allSubList.add(aElement);
		}
		for (SubordinationElement value : aElement.getChildren()) {
			addAllSubEls(value);
		}
	}

	/**
	 * Проверяет, содержится элемент с заданным именем aName в списке выделенных
	 * элементов подчиненности
	 * 
	 * @param aName наименование элемента подчиненности
	 * @return {@code true} если элемент содержится
	 */
	public boolean selectedSubsContains(String aName) {
		boolean result = false;
		for (SubordinationElement element : selectedSubList) {
			if (element.getName().equals(aName)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public ObservableList<SubordinationElement> getSelectedSubList() {
		return selectedSubList;
	}

	public ObservableList<SubordinationElement> getAllSubList() {
		return allSubList;
	}

	public void setSearchFlag(Boolean searchFlag) {
		this.searchFlag = searchFlag;
	}

	@Override
	public Class<ISubordinationElementService> getTypeService() {
		return ISubordinationElementService.class;
	}

}
