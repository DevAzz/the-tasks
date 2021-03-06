package ru.devazz.server.api;

import ru.devazz.server.api.model.Filter;
import ru.devazz.server.api.model.TaskHistoryModel;
import ru.devazz.server.api.model.enums.TaskHistoryType;

import java.util.List;

/**
 * Общий интерфейс работы с задачами
 */
public interface ITaskHistoryService extends IEntityService<TaskHistoryModel> {

    /**
     * Возвращает список исторических записей по идентификатору задачи
     *
     * @param aTaskSuid идентификатор задачи
     * @param aFilter   фильтр
     * @return список исторических записей
     */
    List<TaskHistoryModel> getTaskHistory(Long aTaskSuid, Filter aFilter);

    /**
     * Возвращает страницу исторических записей по идентификатору задачи
     *
     * @param aTaskSuid идентификатор задачи
     * @param aLimit    количество записей
     * @param aOffset   смещение
     * @return список исторических записей
     */
    List<TaskHistoryModel> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
                                                         int aOffset);

    /**
     * Возвращает страницу исторических записей по идентификатору задачи с учетом
     * фильтра
     *
     * @param aTaskSuid идентификатор задачи
     * @param aLimit    количество записей
     * @param aOffset   смещение
     * @param aFilter   фильтр
     * @return список исторических записей
     */
    List<TaskHistoryModel> getTaskHistoryWithPagination(Long aTaskSuid, int aLimit,
                                                         int aOffset, Filter aFilter);

    /**
     * Возвращает количество записей в таблице исторических записей
     *
     * @param aTaskSuid           идентификатор задачи
     * @param aFilter             фильтр
     * @param aCountEntriesOnPage количество записей на странице
     * @return колчиество страниц
     */
    int getCountPages(Long aTaskSuid, Integer aCountEntriesOnPage, Filter aFilter);

    /**
     * Возвращает номер страницы исторической записи
     *
     * @param aEntry                       историческая запись
     * @param aTaskSuid                    идентификатор задачи
     * @param aCountTaskHistoryEntryOnPage количество записей на странице
     * @param aFilter                      фильтр
     * @return номер страницы исторической записи
     */
    Integer getNumberPageByTask(TaskHistoryModel aEntry, Long aTaskSuid,
                                Integer aCountTaskHistoryEntryOnPage, Filter aFilter);

    /**
     * Возвращает все исторические записи по задаче с определенным типом
     *
     * @param aTaskSuid идентификатор задачи
     * @param aType     тип исторической записи
     * @return список исторических записей
     */
    List<TaskHistoryModel> getAllTaskHistoryEntriesByType(Long aTaskSuid,
                                                           TaskHistoryType aType);

    /**
     * Удаляет историю по идентификатору задачи
     *
     * @param aTaskSuid иденификатор задачи
     */
    void deleteHistory(Long aTaskSuid);

}
