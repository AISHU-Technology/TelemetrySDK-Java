package cn.aishu.exporter.common.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

public class TimeUtil {

    private TimeUtil() {
    }

    public static void sleepSecond(long sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            LogFactory.getLog(TimeUtil.class).error(e);
            Thread.currentThread().interrupt();
        }
    }

    public static String epochNanoToTime(Long epochNano) {
        return DateUtil.format(new Date(epochNano / 1000000), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").replace("+",
                String.format("%04d", (epochNano % 1000000L) / 100) + "+");
    }
}
