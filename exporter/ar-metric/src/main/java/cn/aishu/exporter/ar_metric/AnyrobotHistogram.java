package cn.aishu.exporter.ar_metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
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

    public AnyrobotHistogram(MetricData metricData) {
        HistogramData histogramData = metricData.getHistogramData();
        if (histogramData.getAggregationTemporality() == AggregationTemporality.DELTA) {
            this.temporality = "DeltaTemporality";
        } else {
            this.temporality = "CumulativeTemporality";
        }
        histogramData.getPoints();
        this.dataPoints = new ArrayList<>();
        Collection<HistogramPointData> tempPoints = histogramData.getPoints();
        for (HistogramPointData tempDataPoint : tempPoints) {
            dataPoints.add(new AnyrobotHistogramDatapoint(tempDataPoint));
        }
    }
}
