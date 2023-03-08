package cn.aishu.exporter.ar_metric;

import java.util.List;
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
    List<AnyrobotDatapoint> dataPoints;

    public List<AnyrobotDatapoint> getDataPoints() {
        return this.dataPoints;
    }

    public AnyrobotGauge(MetricData metricData, Log log) {
        GaugeData<? extends PointData> gaugeData;
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
        Collection<? extends PointData> tempPoints = gaugeData.getPoints();
        for (PointData tempDataPoint : tempPoints) {
            if (tempDataPoint instanceof DoublePointData) {
                dataPoints.add(new AnyrobotDatapoint((DoublePointData) tempDataPoint));
            } else if (tempDataPoint instanceof LongPointData) {
                dataPoints.add(new AnyrobotDatapoint((LongPointData) tempDataPoint));
            } else {
                log.error("AnyrobotGauge:Unrecognized data point type");
            }
        }
    }
}
