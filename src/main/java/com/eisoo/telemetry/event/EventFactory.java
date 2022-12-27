package com.eisoo.telemetry.event;


public class EventFactory {
    private EventFactory() {}

    private static IEventFactory staticEventFactory = new StaticEventFactory();

    public static IEventFactory getEventFactory(){
        return staticEventFactory;
    }

    public static Event getEvent(Class<?> clazz){
        return getEventFactory().getEvent(clazz);
    }

    public static Event getEvent(String name){
        return getEventFactory().getEvent(name);
    }

}