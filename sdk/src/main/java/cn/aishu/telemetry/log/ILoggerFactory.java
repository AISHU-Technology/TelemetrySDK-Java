package cn.aishu.telemetry.log;

public interface ILoggerFactory {
    Logger getLogger(Class<?> clazz);

    Logger getLogger(String name);

    Logger newLogger(String name);
}