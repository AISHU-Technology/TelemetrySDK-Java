package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.ar_trace.common.KeyValue;
import io.opentelemetry.sdk.trace.data.LinkData;

import java.util.List;

public class Link {
    private SpanCtx spanContext;

    private List<KeyValue> attributes;

    private int totalAttributeCount;

    public Link(LinkData linkData) {
        this.spanContext = new SpanCtx(linkData.getSpanContext());
        this.attributes = KeyValue.extractFromAttributes(linkData.getAttributes());
        this.totalAttributeCount = linkData.getTotalAttributeCount();
    }
}
