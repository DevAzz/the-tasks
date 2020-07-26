package ru.devazz.server.api.event;

import ru.devazz.server.api.model.TaskModel;

/**
 * Реализация объекта события для сервиса задач
 */
public class TaskEvent extends ObjectEvent<TaskModel> {

    public TaskEvent() {
    }
}
