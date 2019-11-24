package ru.devazz.server.api.model.enums;

public enum JmsQueueName {

    USERS("usersQueue"),
    TASKS("tasksQueue"),
    EVENTS("eventsQueue"),
    SUB_ELS("subElQueue"),
    HISTORY_TASKS("taskHistoryQueue");

    private String name;

    JmsQueueName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
