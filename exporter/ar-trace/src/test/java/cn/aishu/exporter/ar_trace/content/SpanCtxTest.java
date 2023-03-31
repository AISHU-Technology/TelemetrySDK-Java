package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.utils.JsonUtil;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import org.junit.Assert;
import org.junit.Test;

public class SpanCtxTest {

    @Test
    public void testConstruct(){

        SpanContext sc = SpanContext.create("123", "112",
                TraceFlags.getDefault(), TraceState.builder().put("abc", "def").build());
        SpanCtx spanCtx = new SpanCtx(sc);
        Assert.assertNotNull(spanCtx);

        SpanCtx ctx = new SpanCtx(null);
        Assert.assertNotNull(ctx);
    }

}