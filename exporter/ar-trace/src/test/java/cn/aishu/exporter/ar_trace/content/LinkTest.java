package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import cn.aishu.exporter.common.utils.JsonUtil;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.sdk.internal.AttributesMap;
import io.opentelemetry.sdk.trace.data.LinkData;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class LinkTest{

    @Test
    public void testGetSpanContext() {
        SpanContext sct = SpanContext.create("123", "112",
                TraceFlags.getDefault(), TraceState.getDefault());
        AttributesMap attributesMap = AttributesMap.create(6, 6);
        LinkData linkData = LinkData.create(sct, attributesMap);

        Link link = new Link(linkData);

        SpanCtx spanCtx = new SpanCtx(linkData.getSpanContext());
        link.setSpanContext(spanCtx);
        SpanCtx spanContext = link.getSpanContext();
        Assert.assertEquals(spanCtx, spanContext);

        link.setAttributes(KeyValue.extractFromAttributes(linkData.getAttributes()));
        List<KeyValue> attributes = link.getAttributes();
        Assert.assertEquals(KeyValue.extractFromAttributes(linkData.getAttributes()), attributes);

        link.setTotalAttributeCount(linkData.getTotalAttributeCount());
        int totalAttributeCount = link.getTotalAttributeCount();
        Assert.assertEquals(linkData.getTotalAttributeCount(), totalAttributeCount);
    }

    @Test
    public void testConstruct() {
        Link link = new Link(null);
        Assert.assertNotNull(link);
    }
    
}