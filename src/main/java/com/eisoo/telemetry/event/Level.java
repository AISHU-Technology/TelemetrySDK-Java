package com.eisoo.telemetry.event;

//日志等级
public enum Level {
    ERROR(40, "ERROR"),
    WARN(30, "WARN"),
    INFO(20, "INFO");

    private int levelInt;
    private String levelStr;

    private Level(int i, String s) {
        this.levelInt = i;
        this.levelStr = s;
    }

    public int toInt() {
        return this.levelInt;
    }

    @Override
    public String toString() {
        return this.levelStr;
    }
}
