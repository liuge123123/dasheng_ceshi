package com.tcc.modules.event;

public interface EventHandler {

    <T> void handler(Event<T> event);

}
