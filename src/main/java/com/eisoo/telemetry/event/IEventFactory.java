package com.eisoo.telemetry.event;

public interface IEventFactory {
    Event getEvent(Class<?> clazz);

    Event getEvent(String name);

    Event newEvent(String name);
}