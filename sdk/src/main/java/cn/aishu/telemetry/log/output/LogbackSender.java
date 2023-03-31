package cn.aishu.telemetry.log.output;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.Serializer;
import cn.aishu.telemetry.log.LogContent;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;


public class LogbackSender implements Sender {
    private static final Logger logger = getLogger("LogbackSender");

    public static Sender create(){
        return new LogbackSender();
    }

    public static Logger getLogger(String name) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        logger.setLevel(Level.TRACE);

        return logger;
    }

    @Override
    public void send(Serializer logContent) {
        String level = "Info";
        if (logContent instanceof LogContent) {
            level = ((LogContent) logContent).getSeverityText();
        }

        String msg = logContent.toJson();

        switch (level) {
            case "Fatal":
            case "Error":
                logger.error(msg);
                break;
            case "Warn":
                logger.warn(msg);
                break;
            case "Debug":
                logger.debug(msg);
                break;
            case "Trace":
                logger.trace(msg);
                break;
            default:
                logger.info(msg);
        }
    }

    @Override
    public void shutDown() {
        //这里不需要实现
    }


}