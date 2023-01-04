package com.eisoo.telemetry.event.config;

import com.eisoo.telemetry.event.Level;
import com.eisoo.telemetry.event.output.Destination;
import com.eisoo.telemetry.event.output.HttpOut;
import com.eisoo.telemetry.event.output.HttpsOut;
import com.eisoo.telemetry.event.output.Stdout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EventConfig {
    private EventConfig() {}

    private static Destination destination = genDestination();

    private static Level level = Level.INFO;
    private static final Object lockObj = new Object();

    private static Destination genDestination(){
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("event.properties");
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

    public static void setDefaultDestination() {
        synchronized (lockObj) {
            EventConfig.destination = genDestination();
        }
    }

    public static void setDestination(Destination destination) {
        synchronized (lockObj) {
            EventConfig.destination = destination;
        }
    }

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        EventConfig.level = level;
    }
}
