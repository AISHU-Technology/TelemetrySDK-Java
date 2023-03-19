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

import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.metrics.data.MetricData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AnyrobotScopeMetrics.class })
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

        Log mockLog = mock(Log.class);

        InstrumentationScopeInfo mockInstrumentationScopeInfo = mock(InstrumentationScopeInfo.class);
        when(mockInstrumentationScopeInfo.getName()).thenReturn("testName");
        when(mockInstrumentationScopeInfo.getSchemaUrl()).thenReturn("testSchemaUrl");
        when(mockInstrumentationScopeInfo.getVersion()).thenReturn("testVersion");

        when(mockMetricData.getInstrumentationScopeInfo()).thenReturn(mockInstrumentationScopeInfo);

        AnyrobotScopeMetrics testScopeMetrics = new AnyrobotScopeMetrics(mockMetricData, mockLog);
        Assert.assertEquals(1, testScopeMetrics.metrics.size());
    }

}
