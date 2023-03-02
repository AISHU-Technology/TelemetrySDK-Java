package cn.aishu.telemetry.ar_metric;

import java.util.List;
import org.apache.commons.logging.Log;

import cn.aishu.telemetry.common.KeyValue;
import cn.aishu.telemetry.utils.TimeUtil;

import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotDatapoint {
    @SerializedName("Attributes")
    List<KeyValue> attributes;
    @SerializedName("StartTime")
    String startTime;
    @SerializedName("Time")
    String time;
    @SerializedName("Int")
    Long asInt;
    @SerializedName("Float")
    Double asDouble;

    public void setAttributes(List<KeyValue> attributes) {
        this.attributes = attributes;
    }

    public List<KeyValue> getAttributes() {
        return this.attributes;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getTime() {
        return this.startTime;
    }

    @SerializedName("Int")
    public Long getInt() {
        return this.asInt;
    }

    @SerializedName("Float")
    public Double getFloat() {
        return this.asDouble;
    }

    public AnyrobotDatapoint(DoublePointData dataPoint, Log log) {
        this.time = TimeUtil.epochNanoToTime(dataPoint.getEpochNanos());
        this.startTime = TimeUtil.epochNanoToTime(dataPoint.getStartEpochNanos());

        this.asDouble = dataPoint.getValue();
        this.attributes = KeyValue.extractFromAttributes(dataPoint.getAttributes(), log);
    }

    public AnyrobotDatapoint(LongPointData dataPoint, Log log) {
        this.time = TimeUtil.epochNanoToTime(dataPoint.getEpochNanos());
        this.startTime = TimeUtil.epochNanoToTime(dataPoint.getStartEpochNanos());

        this.asInt = dataPoint.getValue();
        this.attributes = KeyValue.extractFromAttributes(dataPoint.getAttributes(), log);
    }
}
