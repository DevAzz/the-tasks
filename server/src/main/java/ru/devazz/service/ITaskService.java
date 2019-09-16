package ru.devazz.service;

import ru.devazz.entity.DefaultTaskEntity;
import ru.devazz.entity.TaskEntity;
import ru.devazz.utils.Filter;
import ru.devazz.utils.TasksViewType;

import java.util.List;

/**
 * Общий интерфейс работы с задачами
 */
public interface ITaskService extends IEntityService<TaskEntity> {

	/**
	 * Возвращает список задач по идентификатору должности
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	List<TaskEntity> getTasksByAuthor(Long aPositionSuid);

	/**
	 * Возвращает список задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	List<TaskEntity> getTasksByExecutor(Long aPositionSuid);

	/**
	 * Получение типовых задач по SUID поевого поста
	 *
	 * @param aPositionSuid боевой пост
	 * @return список типовых задач
	 */
	List<DefaultTaskEntity> getDefaultTaskBySub(Long aPositionSuid);

	/**
	 * Возвращает все задачи, которые относятся к пользователю
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @return список задач
	 */
	List<TaskEntity> getAllUserTasks(Long aPositionSuid);

	/**
	 * Получение типовой задачи
	 *
	 * @param aPositionSuid SUID задачи
	 * @return сущьность типовой задачи
	 */
	DefaultTaskEntity getDefaultTaskBySUID(Long aPositionSuid);

	/**
	 * Возвращает типовые задачи по идентификатору автора
	 *
	 * @param aPositionSuid идентификаторт автора
	 * @return список типовых задач
	 */
	List<DefaultTaskEntity> getDefaultTasksByAuthor(Long aPositionSuid);

	/**
	 * Получение всех типовых задач
	 *
	 * @return список типовых задач
	 */
	List<DefaultTaskEntity> getDefaultTaskAll();

	/**
	 * Воззвращает список закрытых архивных задач
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @return список задач
	 */
	List<TaskEntity> getClosedTasks(Long aPositionSuid);

	/**
	 * Возвращает список входящих задач пользователя
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @return список задач
	 */
	List<TaskEntity> getInTasks(Long aPositionSuid);

	/**
	 * Возвращает список исходящих задач пользователя
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @return список задач
	 */
	List<TaskEntity> getOutTasks(Long aPositionSuid);

	/**
	 * Возвращает список повседневных типовых задач
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @return список задач
	 */
	List<TaskEntity> getDefaultTasks(Long aPositionSuid);

	/**
	 * Возвращает список задач по идентификатору должности автора задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	List<TaskEntity> getAllTasksByAuthor(Long aPositionSuid);

	/**
	 * Возвращает список задач по идентификатору должности и задачи
	 *
	 * @param aPositionSuid идентификатор должности
	 * @return список задач
	 */
	List<TaskEntity> getAllTasksByExecutor(Long aPositionSuid);

	/**
	 * Возвращает страницу записей
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	List<TaskEntity> getSubElPageTasks(Long aPositionSuid, int aLimit, int aOffset);

	/**
	 * Возвращает страницу записей архивных задач
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	List<TaskEntity> getCloseTasksWithPagination(Long aPositionSuid, int aLimit,
                                                        int aOffset);

	/**
	 * Возвращает страницу записей входящих задач
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	List<TaskEntity> getInTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset);

	/**
	 * Возвращает страницу записей исходящих задач
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	List<TaskEntity> getOutTasksWithPagination(Long aPositionSuid, int aLimit, int aOffset);

	/**
	 * Возвращает страницу записей типовых задач
	 *
	 * @param aPositionSuid идентификатор должности
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @return страница записей в виде списка
	 */
	List<TaskEntity> getDefaultTasksWithPagination(Long aPositionSuid, int aLimit,
                                                          int aOffset);

	/**
	 * Возвращает количество страниц задач
	 *
	 * @param aType тип панели
	 * @param aPositionSuid идентификатор должности
	 * @param aFilter фильтр
	 * @return количество страниц задач
	 */
	Long getCountTasks(TasksViewType aType, Long aPositionSuid, Filter aFilter);

	/**
	 * Возвращает номер страницы по идентификатору задачи
	 *
	 * @param aCountTaskOnPage количество задач на странице
	 * @param aPositionSuid идентификатор должности ДЛ
	 * @param aTaskSuid идентификатор задачи
	 * @param aTypeView тип представления
	 * @param aFilter фильтр
	 * @return номер страницы
	 */
	Integer getPageNumberByTask(Long aPositionSuid, Long aTaskSuid, Integer aCountTaskOnPage,
                                       TasksViewType aTypeView, Filter aFilter);

	/**
	 * Возвращает список идентификатор авторов типовых задач без повторений
	 *
	 * @return список идентификаров
	 */
	List<Long> getDefaultTasksAuthorsSuids();

	/**
	 * Возвращает список задач по типу представления
	 *
	 * @param aPositionSuid идентификатор элемента подчиненности
	 * @param aViewType тип представления
	 * @param aLimit количество записей
	 * @param aOffset смещение
	 * @param aFilter фильтр
	 * @return список задач
	 */
	List<TaskEntity> getTasksByViewTypeWithPagination(Long aPositionSuid,
                                                             TasksViewType aViewType, int aLimit,
                                                             int aOffset, Filter aFilter);

	/**
	 * Возвращает список циклических задач
	 *
	 * @return список циклических задач
	 */
	List<TaskEntity> getCycleTasks();

	/**
	 * Возвращает список всех задач боевого поста по фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	List<TaskEntity> getAllUserTasksWithFilter(Long aPositionSuid, Filter aFilter);

	/**
	 * Возвращает список задач в которых боевой пост числится автором по фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	List<TaskEntity> getAllUserTasksAuthorWithFilter(Long aPositionSuid, Filter aFilter);

	/**
	 * Возвращает список задач в которых боевой пост числится исполонителем по
	 * фильтру
	 *
	 * @param aPositionSuid идентификатор боевого поста
	 * @param aFilter фильтр
	 * @return список задач
	 */
	List<TaskEntity> getAllUserTasksExecutorWithFilter(Long aPositionSuid, Filter aFilter);

	/**
	 * Возвращает список всех задач
	 * 
	 * @return список всех задач
	 */
	List<TaskEntity> getAll();

}
