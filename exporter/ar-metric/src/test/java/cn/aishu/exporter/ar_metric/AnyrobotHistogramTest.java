package cn.aishu.exporter.ar_metric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.HistogramData;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AnyrobotHistogram.class })
public class AnyrobotHistogramTest {

    @Test
    public void testAnyrobotLongGauge() {
        AnyrobotHistogramDatapoint mockAnyrobotHistogramDatapoint = mock(AnyrobotHistogramDatapoint.class);

        try {
            PowerMockito.whenNew(AnyrobotHistogramDatapoint.class).withAnyArguments()
                    .thenReturn(mockAnyrobotHistogramDatapoint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HistogramPointData mockHistogramPointData = mock(HistogramPointData.class);
        ArrayList<HistogramPointData> mockList = new ArrayList<HistogramPointData>();
        mockList.add(mockHistogramPointData);

        HistogramData mockHistogramData = mock(HistogramData.class);
        when(mockHistogramData.getPoints()).thenReturn(mockList);
        when(mockHistogramData.getAggregationTemporality()).thenReturn(AggregationTemporality.DELTA);

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getHistogramData()).thenReturn(mockHistogramData);

        AnyrobotHistogram testHistogram = new AnyrobotHistogram(mockMetricData);
        Assert.assertEquals(1, testHistogram.dataPoints.size());
    }
}
