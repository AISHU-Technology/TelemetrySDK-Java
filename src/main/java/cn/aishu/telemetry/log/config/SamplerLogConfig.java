package cn.aishu.telemetry.log.config;

import cn.aishu.telemetry.log.Level;
import cn.aishu.telemetry.log.output.Destination;
import cn.aishu.telemetry.log.output.Stdout;

public class SamplerLogConfig {
    private SamplerLogConfig() {
    }

    private static Destination destination = new Stdout();

    private static Level level = Level.DEBUG;
    private static final Object lockObj = new Object();

    public static Destination getDestination() {
        return destination;
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
