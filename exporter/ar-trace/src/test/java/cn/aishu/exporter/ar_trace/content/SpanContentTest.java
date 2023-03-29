package cn.aishu.exporter.ar_trace.content;


import org.junit.Assert;
import org.junit.Test;

public class SpanContentTest {

    @Test
    public void testToJson() {
        String expected = "{\"SpanKind\":0,\"StartTime\":0,\"EndTime\":0,\"DroppedAttributes\":0,\"DroppedEvents\":0,\"DroppedLinks\":0,\"ChildSpanCount\":0}";
        Assert.assertEquals(expected, new SpanContent(null).toJson());
    }
}