package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import cn.aishu.exporter.common.utils.TimeUtil;
import io.opentelemetry.sdk.trace.data.EventData;

import java.util.List;

public class Event {

    private String name;

    private List<KeyValue> attributes;

    private long epochNanos;

    private String time;

    private int totalAttributeCount;

    private int droppedAttributesCount;

    public Event(EventData eventData) {
        this.name = eventData.getName();
        this.attributes = KeyValue.extractFromAttributes(eventData.getAttributes());
        this.epochNanos = eventData.getEpochNanos();
        this.time = TimeUtil.epochNanoToTime(eventData.getEpochNanos());
        this.totalAttributeCount = eventData.getTotalAttributeCount();
        this.droppedAttributesCount = eventData.getDroppedAttributesCount();
    }
}
