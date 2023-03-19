package cn.aishu.exporter.ar_metric;

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
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;

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
        SumData<? extends PointData> sumData;
        switch (metricData.getType()) {
            case LONG_SUM:
                sumData = metricData.getLongSumData();
                break;
            case DOUBLE_SUM:
                sumData = metricData.getDoubleSumData();
                break;
            default:
                log.error("AnyrobotSum:Unexpected Sum type");
                return;
        }
        this.isMonotonic = sumData.isMonotonic();
        if (sumData.getAggregationTemporality() == AggregationTemporality.DELTA) {
            this.temporality = "DeltaTemporality";
        } else {
            this.temporality = "CumulativeTemporality";
        }

        this.dataPoints = new ArrayList<>();
        Collection<? extends PointData> tempPoints = sumData.getPoints();
        for (PointData tempDataPoint : tempPoints) {
            if (tempDataPoint instanceof DoublePointData) {
                dataPoints.add(new AnyrobotDatapoint((DoublePointData) tempDataPoint));
            } else if (tempDataPoint instanceof LongPointData) {
                dataPoints.add(new AnyrobotDatapoint((LongPointData) tempDataPoint));
            }
        }
    }
}
