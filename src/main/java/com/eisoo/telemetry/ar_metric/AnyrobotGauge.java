package com.eisoo.telemetry.ar_metric;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.GaugeData;
import io.opentelemetry.sdk.metrics.data.PointData;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotGauge {
    @SerializedName("DataPoints")
    ArrayList<AnyrobotDatapoint> dataPoints;

    public ArrayList<AnyrobotDatapoint> getDataPoints() {
        return this.dataPoints;
    }

    public AnyrobotGauge(MetricData metricData, Log log) {
        GaugeData gaugeData;
        switch (metricData.getType()) {
            case LONG_GAUGE:
                gaugeData = metricData.getLongGaugeData();
                break;
            case DOUBLE_GAUGE:
                gaugeData = metricData.getDoubleGaugeData();
                break;
            default:
                log.error("Error Occured when initializing AnyrobotGauge");
                return;
        }
        this.dataPoints = new ArrayList<>();
        Collection<PointData> tempPoints = gaugeData.getPoints();
        for (PointData tempDataPoint : tempPoints) {
            if (tempDataPoint instanceof DoublePointData) {
                dataPoints.add(new AnyrobotDatapoint((DoublePointData) tempDataPoint, log));
            } else if (tempDataPoint instanceof LongPointData) {
                dataPoints.add(new AnyrobotDatapoint((LongPointData) tempDataPoint, log));
            } else {
                log.error("AnyrobotGauge:Unrecognized data point type");
            }
        }
    }
}
