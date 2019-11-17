/**
 *
 */
package ru.devazz.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import ru.devazz.server.api.ICommonService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.widgets.PageSettingsView;

import java.util.Map;

/**
 * Модель представления формы настроек паджинации
 */
public class PageSettingsModel extends PresentationModel<ICommonService, IEntity> {

	/** Свойство значения спиннера выбора количества записей на странице */
	private ObjectProperty<Integer> countPageEntriesProperty;

	/** Свойство значения выбора количества страниц в блоке */
	private ObjectProperty<Integer> countPagesProperty;

	@Override
	protected void initModel() {
		countPageEntriesProperty = new SimpleObjectProperty<>(this, "countPageEntriesProperty", 0);
		countPagesProperty = new SimpleObjectProperty<>(this, "countPagesProperty", 0);

	}

	/**
	 * Инициализирует свойства модели
	 * 
	 * @param aInitMap инициализационная карта
	 */
	public void initProperties(Map<String, Integer> aInitMap) {
		countPageEntriesProperty = new SimpleObjectProperty<>(this, "countPageEntriesProperty",
				aInitMap.get(PageSettingsView.DEFAULT_PAGES_ENTRIES_COUNT));
		countPagesProperty = new SimpleObjectProperty<>(this, "countPagesProperty",
				aInitMap.get(PageSettingsView.DEFAULT_PAGES_COUNT));
	}

	/**
	 * Возвращает {@link#countPageEntriesProperty}
	 *
	 * @return the {@link#countPageEntriesProperty}
	 */
	public ObjectProperty<Integer> getCountPageEntriesProperty() {
		return countPageEntriesProperty;
	}

	/**
	 * Возвращает {@link#countPagesProperty}
	 *
	 * @return the {@link#countPagesProperty}
	 */
	public ObjectProperty<Integer> getCountPagesProperty() {
		return countPagesProperty;
	}

	@Override
	public Class<ICommonService> getTypeService() {
		// TODO Auto-generated method stub
		return null;
	}

}
