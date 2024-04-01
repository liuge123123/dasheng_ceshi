package com.tcc.modules.event;

import com.tcc.common.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListener implements ApplicationListener<Event> {

    @Override
    public void onApplicationEvent(Event event) {
        String eventHandlerName = event.getEventType().name() + "Handler";
        EventHandler eventHandler = (EventHandler) SpringContextUtils.getBean(eventHandlerName);
        eventHandler.handler(event);
    }

}
