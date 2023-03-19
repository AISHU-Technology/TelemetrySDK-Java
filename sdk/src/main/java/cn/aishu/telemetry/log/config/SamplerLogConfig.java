package cn.aishu.telemetry.log.config;

import cn.aishu.exporter.common.output.HttpSender;
import cn.aishu.exporter.common.output.HttpsSender;
import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.StdSender;
import cn.aishu.telemetry.log.Level;
import cn.aishu.telemetry.log.constant.KeyConstant;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class SamplerLogConfig {
    private SamplerLogConfig() {}

    private static Sender sender = genSender(KeyConstant.CONFIG_FILE.toString());

    private static Level level = Level.INFO;
    private static final Object lockObj = new Object();

    private static Sender genSender(String filename){
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (resourceAsStream != null) {
            try {
                properties.load(resourceAsStream);
                String httpUrl = properties.getProperty("http.url");
                if (httpUrl != null) {
                    return HttpSender.create(httpUrl);
                }
                String httpsUrl = properties.getProperty("https.url");
                if (httpsUrl != null) {
                    return HttpsSender.create(httpsUrl);
                }
            } catch (IOException e) {
                Logger.getGlobal().warning(e.toString());
            }
        }
        return new StdSender();
    }


    public static Sender getSender() {
        return sender;
    }

    public static void setConfigFileSender(String filename) {
        synchronized (lockObj) {
            SamplerLogConfig.sender = genSender(filename);
        }
    }

    public static void setSender(Sender sender) {
        synchronized (lockObj) {
            if (SamplerLogConfig.sender != null){
                SamplerLogConfig.sender.shutDown();
            }
            SamplerLogConfig.sender = sender;
        }
    }

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        SamplerLogConfig.level = level;
    }
}

