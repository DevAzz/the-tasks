package ru.devazz.server.api.model.enums;

public enum JmsQueueName {

    DEFAULT("task_queue");

    private String name;

    JmsQueueName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
