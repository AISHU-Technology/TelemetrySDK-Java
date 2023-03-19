package cn.aishu.exporter.common.utils;

import cn.aishu.exporter.common.output.MyX509TrustManager;
import cn.aishu.exporter.common.output.Retry;
import org.junit.Assert;
import org.junit.Test;


public class JsonUtilTest {

    @Test
    public void toJson() {
        Retry retry = new Retry();
        String s = JsonUtil.toJson(retry);
        Assert.assertEquals("{\"Enabled\":true,\"InitialInterval\":5,\"MaxInterval\":15,\"MaxElapsedTime\":30}", s);
        String ss = JsonUtil.toJsonSimple(retry);
        Assert.assertEquals("{\"enabled\":true,\"initialInterval\":5,\"maxInterval\":15,\"maxElapsedTime\":30}", ss);
    }
}