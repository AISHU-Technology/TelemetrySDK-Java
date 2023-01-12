package com.eisoo.telemetry.log.config;

import com.eisoo.telemetry.log.Level;
import com.eisoo.telemetry.log.constant.KeyConstant;
import com.eisoo.telemetry.log.output.Destination;
import com.eisoo.telemetry.log.output.HttpOut;
import com.eisoo.telemetry.log.output.HttpsOut;
import com.eisoo.telemetry.log.output.Stdout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SamplerLogConfig {
    private SamplerLogConfig() {}

    private static Destination destination = genDestination(KeyConstant.CONFIG_FILE.toString());

    private static Level level = Level.INFO;
    private static final Object lockObj = new Object();

    private static Destination genDestination(String filename){
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (resourceAsStream != null) {
            try {
                properties.load(resourceAsStream);
                String httpUrl = properties.getProperty("http.url");
                if (httpUrl != null) {
                    return new HttpOut(httpUrl);
                }
                String httpsUrl = properties.getProperty("https.url");
                if (httpsUrl != null) {
                    return new HttpsOut(httpsUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Stdout();
    }


    public static Destination getDestination() {
        return destination;
    }

    public static void setConfigFileDestination(String filename) {
        synchronized (lockObj) {
            SamplerLogConfig.destination = genDestination(filename);
        }
    }

    public static void setDestination(Destination destination) {
        synchronized (lockObj) {
            SamplerLogConfig.destination = destination;
        }
    }

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        SamplerLogConfig.level = level;
    }
}

