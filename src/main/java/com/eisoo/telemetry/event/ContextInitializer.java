package com.eisoo.telemetry.event;


public class ContextInitializer {
    private ContextInitializer() {}

    private static final EventContext DEFAULT_EVENT_CONTEXT = new EventContext();

    public static EventContext getDefaultEventContext(){
        return DEFAULT_EVENT_CONTEXT;
    }

}
