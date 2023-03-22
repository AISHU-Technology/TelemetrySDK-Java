package cn.aishu.exporter.ar_trace.content;


import cn.aishu.exporter.common.utils.JsonUtil;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.Assert;
import org.junit.Test;


public class StatusTest {

    @Test
    public void testStatus() {
        StatusData statusUnset = StatusData.create(StatusCode.UNSET, "this is description");
        Assert.assertEquals("{\"Code\":0,\"Description\":\"this is description\"}", JsonUtil.toJson(new Status(statusUnset)));
        StatusData statusError = StatusData.create(StatusCode.ERROR, "this is description");
        Assert.assertEquals("{\"Code\":1,\"Description\":\"this is description\"}", JsonUtil.toJson(new Status(statusError)));
        StatusData statusOK = StatusData.create(StatusCode.OK, "this is description");
        Assert.assertEquals("{\"Code\":2,\"Description\":\"this is description\"}", JsonUtil.toJson(new Status(statusOK)));
    }

    @Test
    public void testConstruct() {
        Status status = new Status(null);
        Assert.assertNotNull(status);
    }
}