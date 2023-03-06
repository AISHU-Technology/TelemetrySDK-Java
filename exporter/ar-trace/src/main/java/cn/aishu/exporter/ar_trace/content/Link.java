package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.LinkData;

import java.util.List;

public class Link {
    @SerializedName("SpanContext")
    private SpanCtx spanContext;

    @SerializedName("Attributes")
    private List<KeyValue> attributes;

    @SerializedName("TotalAttributeCount")
    private int totalAttributeCount;

    public SpanCtx getSpanContext() {
        return spanContext;
    }

    public void setSpanContext(SpanCtx spanContext) {
        this.spanContext = spanContext;
    }

    public List<KeyValue> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<KeyValue> attributes) {
        this.attributes = attributes;
    }

    public int getTotalAttributeCount() {
        return totalAttributeCount;
    }

    public void setTotalAttributeCount(int totalAttributeCount) {
        this.totalAttributeCount = totalAttributeCount;
    }

    public Link(LinkData linkData) {
        this.spanContext = new SpanCtx(linkData.getSpanContext());
        this.attributes = KeyValue.extractFromAttributes(linkData.getAttributes());
        this.totalAttributeCount = linkData.getTotalAttributeCount();
    }
}
