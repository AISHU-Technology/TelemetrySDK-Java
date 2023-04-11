package cn.aishu.exporter.ar_metric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.opentelemetry.sdk.metrics.internal.aggregator.EmptyMetricData;
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
        String nameTest = "NameTest";
        String descriptionTest = "DescriptionTest";
        String unitTest = "UnitTest";
        when(mockMetricData.getName()).thenReturn(nameTest);
        when(mockMetricData.getDescription()).thenReturn(descriptionTest);
        when(mockMetricData.getUnit()).thenReturn(unitTest);
        when(mockMetricData.getType()).thenReturn(MetricDataType.LONG_GAUGE);

        try {
            PowerMockito.whenNew(AnyrobotGauge.class).withAnyArguments().thenReturn(mockAnyrobotGauge);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AnyrobotMetrics testMetrics = new AnyrobotMetrics(mockMetricData, mockLog);
        Assert.assertNotNull(testMetrics.gauge);

        testMetrics.setName(nameTest);
        testMetrics.setDescription(descriptionTest);
        Assert.assertEquals(nameTest, testMetrics.getName());
        Assert.assertEquals(descriptionTest, testMetrics.getDescription());
        Assert.assertNotNull(testMetrics.getGauge());
        Assert.assertNotNull(testMetrics.getUnit());
        Assert.assertNull(testMetrics.getHistogram());
        Assert.assertNull(testMetrics.getSum());
    }


}
