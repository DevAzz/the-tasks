package ru.devazz.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.devazz.entities.ExtSearchRes;
import ru.devazz.entities.SubordinationElement;
import ru.devazz.server.api.ISearchService;
import ru.devazz.server.api.model.IEntity;
import ru.devazz.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель представления вкладки расширенного поиска
 */
public class ExtendedSearchViewModel extends PresentationModel<ISearchService, IEntity> {

	/** Список результатов поиска по задачам */
	private ObservableList<ExtSearchRes> tasksData;

	/** Список результатов поиска по ДЛ */
	private ObservableList<ExtSearchRes> personsData;

	/** Свойство текста поля ввода Должности */
	private SimpleStringProperty authorProperty;

	/** Свойство текста поля ввода Наименования задачи */
	private SimpleStringProperty taskNameProperty;

	/**
	 * Свойство текста поля ввода наименования должности, к которому привязана
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

	/** Свойство текста поля ввода наименования должности */
	private SimpleStringProperty subElNameProperty;

	/** Выделенный результат */
	private ExtSearchRes selectedResult = null;

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

		authorProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				taskNameProperty.set("");
				executorProperty.set("");
			}
		});
		taskNameProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				authorProperty.set("");
				executorProperty.set("");
			}
		});
		executorProperty.addListener((observable, oldValue, newValue) -> {
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
				.addListener((observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) {
						positionSubElProperty.set("");
						subElNameProperty.set("");
					}
				});
		positionSubElProperty
				.addListener((observable, oldValue, newValue) -> {
					if (!newValue.isEmpty()) {
						personNameProperty.set("");
						subElNameProperty.set("");
					}
				});
		subElNameProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue.isEmpty()) {
				personNameProperty.set("");
				positionSubElProperty.set("");
			}
		});
	}

	@Override
	protected String getQueueName() {
		return null;
	}

	public ObservableList<ExtSearchRes> getTasksData() {
		return tasksData;
	}

	public ObservableList<ExtSearchRes> getPersonsData() {
		return personsData;
	}

	@Override
	public Class<ISearchService> getTypeService() {
		return ISearchService.class;
	}

	public SimpleStringProperty getAuthorProperty() {
		return authorProperty;
	}

	private void setAuthorProperty(String positionString) {
		this.authorProperty.set(positionString);
	}

	public SimpleStringProperty getTaskNameProperty() {
		return taskNameProperty;
	}

	public void setTaskNameProperty(String taskNameValue) {
		this.taskNameProperty.set(taskNameValue);
	}

	public SimpleStringProperty getExecutorProperty() {
		return executorProperty;
	}

	private void setExecutorProperty(String subelValue) {
		this.executorProperty.set(subelValue);
	}

	public ExtSearchRes getSelectedResult() {
		return selectedResult;
	}

	public void setSelectedResult(ExtSearchRes selectedResult) {
		this.selectedResult = selectedResult;
	}

	public SimpleStringProperty getPersonNameProperty() {
		return personNameProperty;
	}

	public SimpleStringProperty getPositionSubElProperty() {
		return positionSubElProperty;
	}

	public SimpleStringProperty getSubElNameProperty() {
		return subElNameProperty;
	}

	public SubordinationElement getAuthor() {
		return author;
	}

	public void setAuthor(SubordinationElement author) {
		this.author = author;
		if (null != author) {
			setAuthorProperty(author.getName());
		} else {
			setAuthorProperty("");
		}
	}

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

		boolean userCredentials = !personNameProperty.get().isEmpty();
		boolean position = !positionSubElProperty.get().isEmpty();
		boolean subElName = !subElNameProperty.get().isEmpty();

		if (userCredentials) {
			listEntity.addAll(getService().searchUsersByName(personNameProperty.get(),
															 Utils.getInstance().getCurrentUser().getSuid()));
		} else if (position) {
			listEntity.addAll(getService().searchUsersByPosition(positionSubElProperty.get(),
					Utils.getInstance().getCurrentUser().getSuid()));
		} else if (subElName) {
			listEntity.addAll(getService().searchSubElsByName(subElNameProperty.get(),
					Utils.getInstance().getCurrentUser().getSuid()));
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

		boolean authorExist = !authorProperty.get().isEmpty();
		boolean executorExist = !executorProperty.get().isEmpty();
		boolean taskNameExist = !taskNameProperty.get().isEmpty();

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
