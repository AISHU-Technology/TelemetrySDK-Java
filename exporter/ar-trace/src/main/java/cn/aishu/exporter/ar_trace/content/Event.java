package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import cn.aishu.exporter.common.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.EventData;

import java.util.List;

public class Event {

    @SerializedName("Name")
    private String name;

    @SerializedName("Attributes")
    private List<KeyValue> attributes;

    @SerializedName("EpochNanos")
    private long epochNanos;

    @SerializedName("Time")
    private String time;

    @SerializedName("TotalAttributeCount")
    private int totalAttributeCount;

    @SerializedName("DroppedAttributesCount")
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
