package com.eisoo.telemetry.utils;

import java.util.Random;

public class KeyUtil {

    private KeyUtil() {}

    //生成纳秒timestamp
    public  static synchronized Long getNanoSecond() {
        return System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L;
    }

    //生成32位的traceId
    public  static synchronized String getTraceId() {
        return getRandomValue(32);
    }

    //生成16位的spanId
    public  static synchronized String getSpanId() {
        return getRandomValue(16);
    }

    //生成numSize位的16进制字符串
    private static String getRandomValue(int numSize) {
        StringBuilder result = new StringBuilder(numSize);
        for (int i = 0; i < numSize; i++) {
            result.append(Integer.toHexString(new Random().nextInt(16)));
        }
        return result.toString();
    }
}
