package cn.aishu.exporter.ar_metric;

import org.junit.Assert;
import org.junit.Test;

import cn.aishu.exporter.common.utils.TimeUtil;

import static org.mockito.Mockito.*;

import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;

public class AnyrobotDatapointTest {
    @Test
    public void testAnyrobotDatapoint() {
        DoublePointData mockDataPoint = mock(DoublePointData.class);
        AttributesBuilder attributesBuilder = Attributes.builder();
        attributesBuilder.put("test", 1, 2).put("test2", 1);
        Attributes attribute = attributesBuilder.build();
        when(mockDataPoint.getAttributes()).thenReturn(attribute);
        when(mockDataPoint.getValue()).thenReturn(1.0);
        when(TimeUtil.epochNanoToTime(anyLong())).thenReturn("1", "2");
        AnyrobotDatapoint testDatapoint = new AnyrobotDatapoint(mockDataPoint);
        Assert.assertEquals((Double) 1.0, testDatapoint.asDouble);
        Assert.assertEquals("1", testDatapoint.startTime);
        Assert.assertEquals("2", testDatapoint.time);
    }
}
