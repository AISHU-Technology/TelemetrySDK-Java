package com.eisoo.telemetry.event.constant;

public enum KeyConstant {
    SERVICE("service");

    private String key;
    KeyConstant(String k){
        this.key = k;
    }

    @Override
    public String toString() {
        return this.key;
    }

}
