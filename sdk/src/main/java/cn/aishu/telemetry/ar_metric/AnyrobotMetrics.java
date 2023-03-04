package cn.aishu.telemetry.ar_metric;

import org.apache.commons.logging.Log;

import com.google.gson.annotations.SerializedName;

import io.opentelemetry.sdk.metrics.data.*;

public class AnyrobotMetrics {

    @SerializedName("Name")
    String name;
    @SerializedName("Description")
    String description;
    @SerializedName("Unit")
    String unit;
    @SerializedName("Sum")
    AnyrobotSum sum;
    @SerializedName("Gauge")
    AnyrobotGauge gauge;
    @SerializedName("Histogram")
    AnyrobotHistogram histogram;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUnit() {
        return this.unit;
    }

    public AnyrobotSum getSum() {
        return this.sum;
    }

    public AnyrobotGauge getGauge() {
        return this.gauge;
    }

    public AnyrobotHistogram getHistogram() {
        return this.histogram;
    }

    public AnyrobotMetrics(MetricData metricData, Log log) {
        this.name = metricData.getName();
        this.description = metricData.getDescription();
        this.unit = metricData.getUnit();
        switch (metricData.getType()) {
            case LONG_GAUGE:
            case DOUBLE_GAUGE:
                this.gauge = new AnyrobotGauge(metricData, log);
                break;
            case LONG_SUM:
            case DOUBLE_SUM:
                this.sum = new AnyrobotSum(metricData, log);
                break;
            case HISTOGRAM:
                this.histogram = new AnyrobotHistogram(metricData, log);
                break;
            case SUMMARY:
            case EXPONENTIAL_HISTOGRAM:
                log.error("unsupported metric types");
        }
    }
}
