package cn.aishu.exporter.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;


public class ServiceTest {

    @Test
    public void getMap() {

        Map<String, String> map = Service.getMap();
        Assert.assertEquals(3, map.size());
    }
}