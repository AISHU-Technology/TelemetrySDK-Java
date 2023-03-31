package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.EventData;

import java.util.List;

public class Event {

    @SerializedName("Name")
    private String name;

    @SerializedName("Attributes")
    private List<KeyValue> attributes;

    @SerializedName("Time")
    private long time;

    @SerializedName("TotalAttributeCount")
    private int totalAttributeCount;

    @SerializedName("DroppedAttributesCount")
    private int droppedAttributesCount;

    public Event(EventData eventData) {
        if (eventData == null){
            return;
        }
        this.name = eventData.getName();
        this.attributes = KeyValue.extractFromAttributes(eventData.getAttributes());
        this.time = eventData.getEpochNanos();
        this.totalAttributeCount = eventData.getTotalAttributeCount();
        this.droppedAttributesCount = eventData.getDroppedAttributesCount();
    }
}
