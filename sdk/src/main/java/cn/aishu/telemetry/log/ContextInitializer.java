package cn.aishu.telemetry.log;

public class ContextInitializer {
    private ContextInitializer() {
    }

    private static final LoggerContext DEFAULT_LOGGER_CONTEXT = new LoggerContext();

    public static LoggerContext getDefaultLoggerContext() {
        return DEFAULT_LOGGER_CONTEXT;
    }

}
