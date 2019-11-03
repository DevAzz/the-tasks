package ru.devazz.utils;

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
