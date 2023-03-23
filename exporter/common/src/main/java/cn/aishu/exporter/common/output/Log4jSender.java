package cn.aishu.exporter.common.output;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jSender implements Sender {
    Logger logger = LogManager.getLogger(Log4jSender.class);

    @Override
    public void send(Serializer logContent) {
        logger.info(logContent);
    }

    @Override
    public void shutDown() {
        // 对于标准输出不需要实现
    }
}
