package com.eisoo.telemetry.log.utils;

public class SleepUtil {
    private SleepUtil() {}

    public static void second(long sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
