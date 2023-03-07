package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import cn.aishu.exporter.common.output.Serializer;
import cn.aishu.exporter.common.Resource;
import cn.aishu.exporter.common.utils.JsonUtil;
import cn.aishu.exporter.common.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.SpanData;

import java.util.*;
import java.util.stream.Collectors;

public class SpanContent implements Serializer {
    @SerializedName("Name")
    private String name;

    @SerializedName("SpanContext")
    private SpanCtx spanContext;

    @SerializedName("Parent")
    private SpanCtx parentSpanContext;

    @SerializedName("SpanKind")
    private int spanKind;

    @SerializedName("StartTime")
    private String startTime;

    @SerializedName("EndTime")
    private String endTime;

    @SerializedName("Attributes")
    private List<KeyValue> attributes;

    @SerializedName("Links")
    private List<Link> links ;

    @SerializedName("Events")
    private List<Event> events ;

    @SerializedName("Status")
    private Status status;

    @SerializedName("InstrumentationScope")
    private InstrumentationScope instrumentationScope;

    @SerializedName("Resource")
    private List<KeyValue> resource;

    @SerializedName("DroppedAttributes")
    private int droppedAttributes;

    @SerializedName("DroppedEvents")
    private int droppedEvents;

    @SerializedName("DroppedLinks")
    private int droppedLinks;

    @SerializedName("ChildSpanCount")
    private int childSpanCount;

    public SpanContent(SpanData span) {
        this.name = span.getName();
        this.spanContext = new SpanCtx(span.getSpanContext());
        this.parentSpanContext = new SpanCtx(span.getParentSpanContext());
        this.spanKind = span.getKind().ordinal() + 1;
        startTime = TimeUtil.epochNanoToTime(span.getStartEpochNanos());
        endTime = TimeUtil.epochNanoToTime(span.getEndEpochNanos());
        this.attributes = KeyValue.extractFromAttributes(span.getAttributes());
        this.links = span.getLinks().stream().map(e -> new Link(e)).collect(Collectors.toList());
        this.events = span.getEvents().stream().map(e -> new Event(e)).collect(Collectors.toList());
        this.status = new Status(span.getStatus());
        this.instrumentationScope = new InstrumentationScope(span.getInstrumentationScopeInfo());
        this.resource = Resource.getResource(span.getResource().getAttributes());
        this.droppedAttributes = span.getTotalAttributeCount() - span.getAttributes().size();
        this.droppedEvents = span.getTotalRecordedEvents() - span.getEvents().size();
        this.droppedLinks = span.getTotalRecordedLinks() - span.getLinks().size();
        // childSpanCount在java版本的spanData中未找到相应属性
        this.childSpanCount = 0;
    }


    public String toJson() {

        return JsonUtil.toJson(this);
    }

}
