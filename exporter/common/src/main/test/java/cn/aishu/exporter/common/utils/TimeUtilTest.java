package cn.aishu.exporter.common.utils;


import org.junit.Assert;
import org.junit.Test;


public class TimeUtilTest {

    @Test
    public void sleepSecond() {
        final long startTime = System.currentTimeMillis();
        final long oneSecond = 1;
        TimeUtil.sleepSecond(oneSecond);
        final long endTime = System.currentTimeMillis();
        final long ms = endTime - startTime;
        Assert.assertTrue(ms >= oneSecond * 1000);
    }


    @Test
    public void epochNanoToTime() {
        String s = TimeUtil.epochNanoToTime(1678845584123456789l);
        Assert.assertEquals("2023-03-15T09:59:44.1234567+08:00", s);
    }
}