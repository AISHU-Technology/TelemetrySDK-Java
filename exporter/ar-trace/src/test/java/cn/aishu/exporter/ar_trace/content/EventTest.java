package cn.aishu.exporter.ar_trace.content;


import io.opentelemetry.api.common.Attributes;

import io.opentelemetry.sdk.trace.data.EventData;
import org.junit.Assert;
import org.junit.Test;

public class EventTest {
    @Test
    public void testConstruct(){
        Assert.assertNotNull(new Event(null));
        Assert.assertNotNull(new Event(EventData.create(1, "abc", Attributes.empty())));
    }

}