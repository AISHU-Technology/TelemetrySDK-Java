package com.eisoo.telemetry.log;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个全局的上下文对象
 */
public class LoggerContext {

    /**
     * logger缓存，存放通过程序手动创建的logger对象
     */
    private final Map<String,Logger> loggerCache = new HashMap<>();

    public void addLogger(Logger logger){
        loggerCache.put(logger.getName(),logger);
    }

    public Map<String, Logger> getLoggerCache() {
        return loggerCache;
    }

}
