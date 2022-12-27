package com.eisoo.telemetry.event.config;

import com.eisoo.telemetry.event.Level;
import com.eisoo.telemetry.event.output.Destination;
import com.eisoo.telemetry.event.output.Stdout;

public class EventConfig {
    private EventConfig() {}

    private static Destination destination = new Stdout();

    private static Level level = Level.INFO;
    private static final Object lockObj = new Object();

    public static Destination getDestination() {
        return destination;
    }

    public static void setDestination(Destination destination) {
        synchronized (lockObj) {
            EventConfig.destination = destination;
        }
    }

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        EventConfig.level = level;
    }
}
