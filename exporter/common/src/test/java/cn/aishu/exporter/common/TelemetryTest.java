package cn.aishu.exporter.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;


public class TelemetryTest {

    @Test
    public void getMap() {

        Map<String, String> map = Telemetry.getMap();
        Assert.assertEquals(3, map.size());
    }

    @Test
    public void getTelemetry() {
        Telemetry telemetry = Telemetry.getTelemetry();
        Assert.assertNotNull(telemetry);
    }
}