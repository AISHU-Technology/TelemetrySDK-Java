package cn.aishu.telemetry.log;

public interface Logger{
    void trace(Object... o);

    void info(Object... o);

    void debug(Object... o);

    void warn(Object... o);

    void error(Object... o);

    void fatal(Object... o);

    String getName();
}

