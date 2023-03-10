package cn.aishu.exporter.ar_metric;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.opentelemetry.sdk.metrics.data.GaugeData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AnyrobotGauge.class })
public class AnyrobotGaugeTest {
    @Test
    public void testAnyrobotLongGauge() {
        AnyrobotDatapoint mockAnyrobotDatapoint = mock(AnyrobotDatapoint.class);

        LongPointData mockDataPoint = mock(LongPointData.class);
        ArrayList<LongPointData> mockList = new ArrayList<LongPointData>();
        mockList.add(mockDataPoint);

        GaugeData<LongPointData> mockLongGaugeData = (GaugeData<LongPointData>) mock(GaugeData.class);
        when(mockLongGaugeData.getPoints()).thenReturn(mockList);

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getType()).thenReturn(MetricDataType.LONG_GAUGE);
        when(mockMetricData.getLongGaugeData()).thenReturn(null);
        when(mockMetricData.getLongGaugeData()).thenReturn(mockLongGaugeData);

        Log mockLog = mock(Log.class);

        try {
            PowerMockito.whenNew(AnyrobotDatapoint.class).withAnyArguments().thenReturn(mockAnyrobotDatapoint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnyrobotGauge testGauge = new AnyrobotGauge(mockMetricData, mockLog);
        Assert.assertEquals(1, testGauge.dataPoints.size());
    }

    @Test
    public void testAnyrobotUnknownGauge() {
        LongPointData mockDataPoint = mock(LongPointData.class);
        ArrayList<LongPointData> mockList = new ArrayList<LongPointData>();
        mockList.add(mockDataPoint);

        GaugeData<LongPointData> mockLongGaugeData = (GaugeData<LongPointData>) mock(GaugeData.class);
        when(mockLongGaugeData.getPoints()).thenReturn(mockList);

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getType()).thenReturn(MetricDataType.SUMMARY);
        when(mockMetricData.getLongGaugeData()).thenReturn(null);
        when(mockMetricData.getLongGaugeData()).thenReturn(mockLongGaugeData);

        Log mockLog = mock(Log.class);

        new AnyrobotGauge(mockMetricData, mockLog);
        verify(mockLog).error(any());
    }
}
