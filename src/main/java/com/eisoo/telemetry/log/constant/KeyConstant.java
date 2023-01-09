package com.eisoo.telemetry.log.constant;

public enum KeyConstant {
    MESSAGE("Message"),
    TYPE("Type"),
    CONFIGFILE("slog.properties");

    private String key;
    KeyConstant(String k){
        this.key = k;
    }

    @Override
    public String toString() {
        return this.key;
    }

}
