package cn.aishu.exporter.ar_metric;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cn.aishu.exporter.common.KeyValue;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.resources.Resource;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AnyrobotScopeResource.class, cn.aishu.exporter.common.Resource.class })
public class AnyrobotScopeResourceTest {
    @Test
    public void testAnyrobotScopeResource() {
        AnyrobotScopeMetrics mockAnyrobotScopeMetrics = mock(AnyrobotScopeMetrics.class);

        try {
            PowerMockito.whenNew(AnyrobotScopeMetrics.class).withAnyArguments()
                    .thenReturn(mockAnyrobotScopeMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyValue mockKeyValue = mock(KeyValue.class);
        List<KeyValue> mockAnyResources = new ArrayList<KeyValue>();
        mockAnyResources.add(mockKeyValue);

        PowerMockito.mockStatic(cn.aishu.exporter.common.Resource.class);
        PowerMockito.when(cn.aishu.exporter.common.Resource.getResource(any())).thenReturn(mockAnyResources);

        Resource mockResource = mock(Resource.class);

        MetricData mockMetricData = mock(MetricData.class);
        when(mockMetricData.getResource()).thenReturn(mockResource);

        Log mockLog = mock(Log.class);

        AnyrobotScopeResource testScopeResource = new AnyrobotScopeResource(mockMetricData, mockLog);

        Assert.assertEquals(1, testScopeResource.scopeMetrics.size());
    }
}
