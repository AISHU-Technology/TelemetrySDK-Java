package com.eisoo.telemetry.constant;

public enum KeyConstant {
    MESSAGE("Message"),
    TYPE("Type");

    private String key;
    private KeyConstant(String k){
        this.key = k;
    }

    @Override
    public String toString() {
        return this.key;
    }

}
