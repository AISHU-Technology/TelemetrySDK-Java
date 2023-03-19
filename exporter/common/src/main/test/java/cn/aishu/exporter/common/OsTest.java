package cn.aishu.exporter.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;


public class OsTest {

    @Test
    public void getMap() {
        Map<String, String> map = Os.getMap();
        Assert.assertEquals(3, map.size());
        Assert.assertNotNull(Os.getOs());
    }
}