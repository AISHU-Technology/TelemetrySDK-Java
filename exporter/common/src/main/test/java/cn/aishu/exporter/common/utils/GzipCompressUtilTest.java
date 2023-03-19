package cn.aishu.exporter.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class GzipCompressUtilTest {

    @Test
    public void compressData() {
        byte[] bytes = GzipCompressUtil.compressData("abc", StandardCharsets.UTF_8.toString());
        Assert.assertEquals(23, bytes.length);
    }

    @Test
    public void compressDataNull() {
        byte[] bytes = GzipCompressUtil.compressData(null, StandardCharsets.UTF_8.toString());
        Assert.assertEquals(0, bytes.length);
    }

    @Test
    public void compressDataError() {
        byte[] bytes = GzipCompressUtil.compressData("abc", "xxx");
        Assert.assertNotEquals(-1, bytes.length);
    }
}