package com.eisoo.telemetry.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个全局的上下文对象
 */
public class EventContext {

    /**
     * event缓存，存放通过程序手动创建的event对象
     */
    private Map<String, Event> eventCache = new HashMap<>();

    public void addEvent(Event event){
        eventCache.put(event.getName(), event);
    }

    public Map<String, Event> getEventCache() {
        return eventCache;
    }

}
