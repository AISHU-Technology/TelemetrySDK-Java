package com.eisoo.telemetry.event;

public class StaticEventFactory implements IEventFactory {

    private EventContext eventContext;

    public StaticEventFactory() {
        eventContext = ContextInitializer.getDefaultEventContext();
    }

    @Override
    public Event getEvent(Class<?> clazz) {
        return getEvent(clazz.getName());
    }

    @Override
    public Event getEvent(String name) {
        Event event = eventContext.getEventCache().get(name);
        if(event == null){
            event = newEvent(name);
        }
        return event;
    }

    @Override
    public Event newEvent(String name) {
        SamplerEvent event = new SamplerEvent();
        event.setName(name);
        eventContext.addEvent(event);

        return event;
    }

}
