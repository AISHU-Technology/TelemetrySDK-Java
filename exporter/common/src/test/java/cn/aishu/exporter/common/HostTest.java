package cn.aishu.exporter.common;

import org.junit.Test;
import org.junit.Assert;

import java.util.Map;


public class HostTest {

    @Test
    public void getMap() {
        Map<String, String> map = Host.getMap();
        Assert.assertEquals(3, map.size());
        Assert.assertNotNull(Host.getHost());
    }
}