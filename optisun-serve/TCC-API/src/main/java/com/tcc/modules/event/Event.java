package com.tcc.modules.event;

import org.springframework.context.ApplicationEvent;

public class Event<T> extends ApplicationEvent {

    private EventType eventType;

    private T data;

    public Event() {
        super("");
    }

    public Event(Object source) {
        super(source);
    }

    public Event(Object source, EventType eventType, T data){
        super(source);
        this.eventType = eventType;
        this.data = data;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
