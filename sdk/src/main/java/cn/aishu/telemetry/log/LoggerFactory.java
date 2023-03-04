package cn.aishu.telemetry.log;

public class LoggerFactory {
    private LoggerFactory() {
    }

    private static ILoggerFactory staticLoggerFactory = new StaticLoggerFactory();

    public static ILoggerFactory getLoggerFactory() {
        return staticLoggerFactory;
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLoggerFactory().getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return getLoggerFactory().getLogger(name);
    }

}