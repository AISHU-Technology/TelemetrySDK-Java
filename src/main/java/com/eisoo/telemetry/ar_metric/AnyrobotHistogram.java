package com.eisoo.telemetry.ar_metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.apache.commons.logging.Log;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.HistogramData;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotHistogram {
    @SerializedName("DataPoints")
    List<AnyrobotHistogramDatapoint> dataPoints;

    @SerializedName("Temporality")
    String temporality;

    public List<AnyrobotHistogramDatapoint> getDataPoints() {
        return this.dataPoints;
    }

    public String getTemporality() {
        return this.temporality;
    }

    public AnyrobotHistogram(MetricData metricData, Log log) {
        HistogramData histogramData = metricData.getHistogramData();
        switch (histogramData.getAggregationTemporality()) {
            case DELTA:
                this.temporality = "DeltaTemporality";
                break;
            case CUMULATIVE:
                this.temporality = "CumulativeTemporality";
                break;
        }
        histogramData.getPoints();
        this.dataPoints = new ArrayList<AnyrobotHistogramDatapoint>();
        Collection<HistogramPointData> tempPoints = histogramData.getPoints();
        for (HistogramPointData tempDataPoint : tempPoints) {
            dataPoints.add(new AnyrobotHistogramDatapoint(tempDataPoint, log));
        }
    }
}
