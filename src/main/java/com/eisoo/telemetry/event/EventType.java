package com.eisoo.telemetry.event;


public class EventType {
    private  String value = "";

    public EventType(String subject) {
        this.value = subject;
    }

    public String getValue() {
        return value;
    }
}
