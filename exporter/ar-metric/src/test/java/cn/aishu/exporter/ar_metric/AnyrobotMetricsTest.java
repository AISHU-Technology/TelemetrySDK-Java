package cn.aishu.exporter.ar_metric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.MetricDataType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AnyrobotMetrics.class })
public class AnyrobotMetricsTest {
    @Test
    public void testAnyrobotMetricsLongGauge() {
        AnyrobotGauge mockAnyrobotGauge = mock(AnyrobotGauge.class);

        Log mockLog = mock(Log.class);

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getName()).thenReturn("NameTest");
        when(mockMetricData.getDescription()).thenReturn("DescriptionTest");
        when(mockMetricData.getUnit()).thenReturn("UnitTest");
        when(mockMetricData.getType()).thenReturn(MetricDataType.LONG_GAUGE);

        try {
            PowerMockito.whenNew(AnyrobotGauge.class).withAnyArguments().thenReturn(mockAnyrobotGauge);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnyrobotMetrics testMetrics = new AnyrobotMetrics(mockMetricData, mockLog);
        Assert.assertNotNull(testMetrics.gauge);
    }
}
