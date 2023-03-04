package cn.aishu.telemetry.log;

public class StaticLoggerFactory implements ILoggerFactory {

    private LoggerContext loggerContext;

    public StaticLoggerFactory() {
        loggerContext = ContextInitializer.getDefaultLoggerContext();
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    @Override
    public Logger getLogger(String name) {
        Logger logger = loggerContext.getLoggerCache().get(name);
        if (logger == null) {
            logger = newLogger(name);
        }
        return logger;
    }

    @Override
    public Logger newLogger(String name) {
        SamplerLogger logger = new SamplerLogger();
        logger.setName(name);
        loggerContext.addLogger(logger);

        return logger;
    }

}
