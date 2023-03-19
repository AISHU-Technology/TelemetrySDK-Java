package cn.aishu.telemetry.log;


public class LoggerFactory {
    public static final String GLOBAL_LOGGER_NAME = "global";

    private LoggerFactory() {}

    private static ILoggerFactory staticLoggerFactory = new StaticLoggerFactory();

    public static ILoggerFactory getLoggerFactory(){
        return staticLoggerFactory;
    }

    public static Logger getLogger(Class<?> clazz){
        return getLoggerFactory().getLogger(clazz);
    }

    public static Logger getLogger(String name){
        return getLoggerFactory().getLogger(name);
    }

    public static final Logger getGlobal() {
        return getLogger(GLOBAL_LOGGER_NAME);
    }


}