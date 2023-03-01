package com.eisoo.telemetry.ar_metric;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;

import com.google.gson.annotations.SerializedName;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.SumData;
import io.opentelemetry.sdk.metrics.data.PointData;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;

public class AnyrobotSum {
    @SerializedName("DataPoints")
    List<AnyrobotDatapoint> dataPoints;
    @SerializedName("Temporality")
    String temporality;
    @SerializedName("IsMonotonic")
    Boolean isMonotonic;

    public List<AnyrobotDatapoint> getDataPoints() {
        return this.dataPoints;
    }

    public String getTemporality() {
        return this.temporality;
    }

    public void setIsMonotonic(Boolean isMonotonic) {
        this.isMonotonic = isMonotonic;
    }

    public Boolean getIsMonotonic() {
        return this.isMonotonic;
    }

    public AnyrobotSum(MetricData metricData, Log log) {
        SumData sumData;
        switch (metricData.getType()) {
            case LONG_SUM:
                sumData = metricData.getLongSumData();
                break;
            case DOUBLE_SUM:
                sumData = metricData.getDoubleSumData();
                break;
            default:
                log.error("Unexpected Sum type");
                return;
        }
        this.isMonotonic = sumData.isMonotonic();
        switch (sumData.getAggregationTemporality()) {
            case DELTA:
                this.temporality = "DeltaTemporality";
                break;
            case CUMULATIVE:
                this.temporality = "CumulativeTemporality";
                break;
        }

        this.dataPoints = new ArrayList<AnyrobotDatapoint>();
        Collection<PointData> tempPoints = sumData.getPoints();
        for (PointData tempDataPoint : tempPoints) {
            if (tempDataPoint instanceof DoublePointData) {
                dataPoints.add(new AnyrobotDatapoint((DoublePointData) tempDataPoint, log));
            } else if (tempDataPoint instanceof LongPointData) {
                dataPoints.add(new AnyrobotDatapoint((LongPointData) tempDataPoint, log));
            }
        }
    }
}
