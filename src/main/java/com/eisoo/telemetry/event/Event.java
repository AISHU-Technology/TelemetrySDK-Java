package com.eisoo.telemetry.event;

public interface Event {

    void info(Object... o);

    void warn(Object... o);

    void error(Object... o);

    String getName();
}

