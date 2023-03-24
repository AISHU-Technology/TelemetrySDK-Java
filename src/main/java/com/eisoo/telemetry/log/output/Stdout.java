package com.eisoo.telemetry.log.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stdout implements Destination{
    private static final Logger logger = LoggerFactory.getLogger("LogbackSender");

    @Override
    public void write(String logContent) {
        logger.info(logContent);
    }
}
