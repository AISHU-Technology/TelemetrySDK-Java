package cn.aishu.exporter.ar_metric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import io.opentelemetry.sdk.metrics.data.MetricData;

public class AnyrobotScopeMetricsTest {
    @Test
    public void testAnyrobotScopeMetrics() {
        AnyrobotMetrics mockAnyrobotMetrics = mock(AnyrobotMetrics.class);

        try {
            PowerMockito.whenNew(AnyrobotMetrics.class).withAnyArguments()
                    .thenReturn(mockAnyrobotMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getInstrumentationScopeInfo().getName()).thenReturn("testName");

    }
}
