package ru.devazz.server;

import ru.devazz.server.api.event.ObjectEvent;

public interface IMessageListener {

    void onEvent(ObjectEvent event);

}
