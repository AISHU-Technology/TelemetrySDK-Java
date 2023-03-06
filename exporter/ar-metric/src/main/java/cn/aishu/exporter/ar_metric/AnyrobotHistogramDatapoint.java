package cn.aishu.exporter.ar_metric;

import java.util.List;

import cn.aishu.exporter.common.KeyValue;
import cn.aishu.exporter.common.utils.TimeUtil;

import io.opentelemetry.sdk.metrics.data.HistogramPointData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotHistogramDatapoint {
    @SerializedName("Attributes")
    List<KeyValue> attributes;
    @SerializedName("StartTime")
    String startTime;
    @SerializedName("Time")
    String time;
    @SerializedName("Count")
    long count;
    @SerializedName("Sum")
    double sum;
    @SerializedName("Bounds")
    List<Double> boundaries;
    @SerializedName("BucketCounts")
    List<Long> bucketCounts;

    public List<KeyValue> getAttributes() {
        return this.attributes;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getTime() {
        return this.time;
    }

    public long getCount() {
        return this.count;
    }

    public double getSum() {
        return this.sum;
    }

    public List<Double> getBoundaries() {
        return this.boundaries;
    }

    public List<Long> getBucketCounts() {
        return this.bucketCounts;
    }

    public AnyrobotHistogramDatapoint(HistogramPointData histogramPointData) {
        this.time = TimeUtil.epochNanoToTime(histogramPointData.getEpochNanos());
        this.startTime = TimeUtil.epochNanoToTime(histogramPointData.getStartEpochNanos());

        this.attributes = KeyValue.extractFromAttributes(histogramPointData.getAttributes());
        this.count = histogramPointData.getCount();
        this.sum = histogramPointData.getSum();
        this.boundaries = histogramPointData.getBoundaries();
        this.bucketCounts = histogramPointData.getCounts();
    }
}
