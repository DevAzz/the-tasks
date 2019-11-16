package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.sciencesquad.hqtasks.server.bean.search.SearchServiceRemote;
import ru.sciencesquad.hqtasks.server.datamodel.IEntity;
import ru.siencesquad.hqtasks.ui.entities.ExtSearchRes;
import ru.siencesquad.hqtasks.ui.entities.SubordinationElement;
import ru.siencesquad.hqtasks.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель представления вкладки расширенного поиска
 */
public class ExtendedSearchViewModel extends PresentationModel<SearchServiceRemote, IEntity> {

	/** Список результатов поиска по задачам */
	private ObservableList<ExtSearchRes> tasksData;

	/** Список результатов поиска по ДЛ */
	private ObservableList<ExtSearchRes> personsData;

	/** Свойство текста поля ввода Должности */
	private SimpleStringProperty authorProperty;

	/** Свойство текста поля ввода Наименования задачи */
	private SimpleStringProperty taskNameProperty;

	/**
	 * Свойство текста поля ввода наименования боевого поста, к которому привязана
	 * задача
	 */
	private SimpleStringProperty executorProperty;

	/** Выбранный автор */
	private SubordinationElement author;

	/** Выбранный исполнитель */
	private SubordinationElement executor;

	/** Свойство текста поля ввода ФИО ДЛ */
	private SimpleStringProperty personNameProperty;

	/** Свойство текста поля ввода наименования должности ДЛ */
	private SimpleStringProperty positionSubElProperty;

	/** Свойство текста поля ввода наименования боевого поста */
	private SimpleStringProperty subElNameProperty;

	/** Выделенный результат */
	private ExtSearchRes selectedResult = null;

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#initModel()
	 */
	@Override
	protected void initModel() {
		tasksData = FXCollections.observableArrayList();
		personsData = FXCollections.observableArrayList();

		// Поля ввода для вкладки поиска задач
		authorProperty = new SimpleStringProperty(this, "positionProperty", "");
		taskNameProperty = new SimpleStringProperty(this, "taskNameProperty", "");
		executorProperty = new SimpleStringProperty(this, "subelProperty", "");

		// Очистка полей поиска в зависимости от выбранного. Поиск возможен только по
		// одному из параметров

		authorProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				taskNameProperty.set("");
				executorProperty.set("");
			}
		});
		taskNameProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				authorProperty.set("");
				executorProperty.set("");
			}
		});
		executorProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				authorProperty.set("");
				taskNameProperty.set("");
			}
		});

		// Поля ввода для вкладки поиска ДЛ
		personNameProperty = new SimpleStringProperty(this, "personNameProperty", "");
		positionSubElProperty = new SimpleStringProperty(this, "positionSubElProperty", "");
		subElNameProperty = new SimpleStringProperty(this, "subElNameProperty", "");

		personNameProperty
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) {
						positionSubElProperty.set("");
						subElNameProperty.set("");
					}
				});
		positionSubElProperty
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) {
						personNameProperty.set("");
						subElNameProperty.set("");
					}
				});
		subElNameProperty.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				personNameProperty.set("");
				positionSubElProperty.set("");
			}
		});
	}

	/**
	 * Возвращает {@link#data}
	 *
	 * @return the {@link#data}
	 */
	public ObservableList<ExtSearchRes> getTasksData() {
		return tasksData;
	}

	/**
	 * Устанавливает значение полю {@link#data}
	 *
	 * @param data значение поля
	 */
	public void setData(ObservableList<ExtSearchRes> data) {
		this.tasksData = data;
	}

	/**
	 * Возвращает {@link#personsData}
	 *
	 * @return the {@link#personsData}
	 */
	public ObservableList<ExtSearchRes> getPersonsData() {
		return personsData;
	}

	/**
	 * Устанавливает значение полю {@link#personsData}
	 *
	 * @param personsData значение поля
	 */
	public void setPersonsData(ObservableList<ExtSearchRes> personsData) {
		this.personsData = personsData;
	}

	/**
	 * @see ru.siencesquad.hqtasks.ui.model.PresentationModel#getTypeService()
	 */
	@Override
	public Class<SearchServiceRemote> getTypeService() {
		return SearchServiceRemote.class;
	}

	/**
	 * Возвращает {@link#positionProperty}
	 *
	 * @return the {@link#positionProperty}
	 */
	public SimpleStringProperty getAuthorProperty() {
		return authorProperty;
	}

	/**
	 * Устанавливает значение полю {@link#positionProperty}
	 *
	 * @param positionString значение поля
	 */
	public void setAuthorProperty(String positionString) {
		this.authorProperty.set(positionString);
	}

	/**
	 * Возвращает {@link#taskNameProperty}
	 *
	 * @return the {@link#taskNameProperty}
	 */
	public SimpleStringProperty getTaskNameProperty() {
		return taskNameProperty;
	}

	/**
	 * Устанавливает значение полю {@link#taskNameProperty}
	 *
	 * @param taskNameValue значение поля
	 */
	public void setTaskNameProperty(String taskNameValue) {
		this.taskNameProperty.set(taskNameValue);
	}

	/**
	 * Возвращает {@link#subelProperty}
	 *
	 * @return the {@link#subelProperty}
	 */
	public SimpleStringProperty getExecutorProperty() {
		return executorProperty;
	}

	/**
	 * Устанавливает значение полю {@link#subelProperty}
	 *
	 * @param subelValue значение поля
	 */
	public void setExecutorProperty(String subelValue) {
		this.executorProperty.set(subelValue);
	}

	/**
	 * Возвращает {@link#selectedResult}
	 *
	 * @return the {@link#selectedResult}
	 */
	public ExtSearchRes getSelectedResult() {
		return selectedResult;
	}

	/**
	 * Устанавливает значение полю {@link#selectedResult}
	 *
	 * @param selectedResult значение поля
	 */
	public void setSelectedResult(ExtSearchRes selectedResult) {
		this.selectedResult = selectedResult;
	}

	/**
	 * Возвращает {@link#personNameProperty}
	 *
	 * @return the {@link#personNameProperty}
	 */
	public SimpleStringProperty getPersonNameProperty() {
		return personNameProperty;
	}

	/**
	 * Устанавливает значение полю {@link#personNameProperty}
	 *
	 * @param personNameValue значение поля
	 */
	public void setPersonNamePropertyValue(String personNameValue) {
		this.personNameProperty.set(personNameValue);
	}

	/**
	 * Возвращает {@link#positionSubElProperty}
	 *
	 * @return the {@link#positionSubElProperty}
	 */
	public SimpleStringProperty getPositionSubElProperty() {
		return positionSubElProperty;
	}

	/**
	 * Устанавливает значение полю {@link#positionSubElProperty}
	 *
	 * @param positionSubElValue значение поля
	 */
	public void setPositionSubElPropertyValue(String positionSubElValue) {
		this.positionSubElProperty.set(positionSubElValue);
	}

	/**
	 * Возвращает {@link#subElNameProperty}
	 *
	 * @return the {@link#subElNameProperty}
	 */
	public SimpleStringProperty getSubElNameProperty() {
		return subElNameProperty;
	}

	/**
	 * Устанавливает значение полю {@link#subElNameProperty}
	 *
	 * @param subElNameValue значение поля
	 */
	public void setSubElNameValue(String subElNameValue) {
		this.subElNameProperty.set(subElNameValue);
	}

	/**
	 * Возвращает {@link#author}
	 *
	 * @return the {@link#author}
	 */
	public SubordinationElement getAuthor() {
		return author;
	}

	/**
	 * Устанавливает значение полю {@link#author}
	 *
	 * @param author значение поля
	 */
	public void setAuthor(SubordinationElement author) {
		this.author = author;
		if (null != author) {
			setAuthorProperty(author.getName());
		} else {
			setAuthorProperty("");
		}
	}

	/**
	 * Возвращает {@link#executor}
	 *
	 * @return the {@link#executor}
	 */
	public SubordinationElement getExecutor() {
		return executor;
	}

	/**
	 * Устанавливает значение полю {@link#executor}
	 *
	 * @param executor значение поля
	 */
	public void setExecutor(SubordinationElement executor) {
		this.executor = executor;
		if (null != executor) {
			setExecutorProperty(executor.getName());
		} else {
			setExecutorProperty("");
		}
	}

	/**
	 * Загрузка результатов поиска ДЛ
	 */
	public void searchPersons() {
		personsData.clear();
		List<IEntity> listEntity = new ArrayList<>();

		Boolean userCredentials = !personNameProperty.get().isEmpty();
		Boolean position = !positionSubElProperty.get().isEmpty();
		Boolean subElName = !subElNameProperty.get().isEmpty();

		if (userCredentials) {
			listEntity.addAll(getService().searchUsersByName(personNameProperty.get(),
					Utils.getInstance().getCurrentUser().getIduser()));
		} else if (position) {
			listEntity.addAll(getService().searchUsersByPosition(positionSubElProperty.get(),
					Utils.getInstance().getCurrentUser().getIduser()));
		} else if (subElName) {
			listEntity.addAll(getService().searchSubElsByName(subElNameProperty.get(),
					Utils.getInstance().getCurrentUser().getIduser()));
		}

		for (IEntity entity : listEntity) {
			String decodeName = Utils.getInstance().fromBase64(entity.getName());
			ExtSearchRes result = new ExtSearchRes(decodeName, entity);
			personsData.add(result);
		}
	}

	/**
	 * Загрузка результатов поиска задач
	 */
	public void searchTasks() {
		tasksData.clear();
		List<IEntity> listEntity = new ArrayList<>();

		Boolean authorExist = !authorProperty.get().isEmpty();
		Boolean executorExist = !executorProperty.get().isEmpty();
		Boolean taskNameExist = !taskNameProperty.get().isEmpty();

		if (authorExist) {
			listEntity.addAll(getService().searchTasksByAuthor(author.getSuid()));
		} else if (taskNameExist) {
			listEntity.addAll(getService().searchTasksByName(taskNameProperty.get(),
					Utils.getInstance().getCurrentUser().getPositionSuid()));
		} else if (executorExist) {
			listEntity.addAll(getService().searchTasksByExecutor(executor.getSuid()));
		}

		for (IEntity entity : listEntity) {
			String decodeName = Utils.getInstance().fromBase64(entity.getName());
			ExtSearchRes result = new ExtSearchRes(decodeName, entity);
			tasksData.add(result);
		}
	}

	/**
	 * Возвращает результат по идентификатору
	 *
	 * @param aSuid идентификатор сущности
	 * @return результат
	 */
	public ExtSearchRes getResPersonsBySuid(String aSuid) {
		ExtSearchRes res = null;
		for (ExtSearchRes value : personsData) {
			if (value.getEntity().getSuid().equals(Long.valueOf(aSuid))) {
				res = value;
				break;
			}
		}
		return res;
	}

	/**
	 * Возвращает результат по идентификатору
	 *
	 * @param aSuid идентификатор сущности
	 * @return результат
	 */
	public ExtSearchRes getResTasksBySuid(String aSuid) {
		ExtSearchRes res = null;
		for (ExtSearchRes value : tasksData) {
			if (value.getEntity().getSuid().equals(Long.valueOf(aSuid))) {
				res = value;
				break;
			}
		}
		return res;
	}

}
