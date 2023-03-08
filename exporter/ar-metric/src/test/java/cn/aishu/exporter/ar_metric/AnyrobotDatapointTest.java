package cn.aishu.exporter.ar_metric;

import org.junit.Assert;
import org.junit.Test;

import cn.aishu.exporter.common.utils.TimeUtil;

import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;

import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeUtil.class)
public class AnyrobotDatapointTest {
    @Test
    public void testAnyrobotDoubleDatapoint() {
        DoublePointData mockDataPoint = mock(DoublePointData.class);
        AttributesBuilder attributesBuilder = Attributes.builder();
        attributesBuilder.put("test", 1, 2).put("test2", 1);
        Attributes attribute = attributesBuilder.build();

        when(mockDataPoint.getAttributes()).thenReturn(attribute);
        when(mockDataPoint.getValue()).thenReturn(1.0);

        PowerMockito.mockStatic(TimeUtil.class);
        when(TimeUtil.epochNanoToTime(any())).thenReturn("2", "1");

        AnyrobotDatapoint testDatapoint = new AnyrobotDatapoint(mockDataPoint);
        Assert.assertEquals((Double) 1.0, testDatapoint.asDouble);
        Assert.assertEquals("1", testDatapoint.startTime);
        Assert.assertEquals("2", testDatapoint.time);
    }

    @Test
    public void testAnyrobotLongDatapoint() {
        LongPointData mockDataPoint = mock(LongPointData.class);
        AttributesBuilder attributesBuilder = Attributes.builder();
        attributesBuilder.put("test", 1, 2).put("test2", 1);
        Attributes attribute = attributesBuilder.build();

        when(mockDataPoint.getAttributes()).thenReturn(attribute);
        when(mockDataPoint.getValue()).thenReturn((long) 1);

        PowerMockito.mockStatic(TimeUtil.class);
        when(TimeUtil.epochNanoToTime(any())).thenReturn("2", "1");

        AnyrobotDatapoint testDatapoint = new AnyrobotDatapoint(mockDataPoint);
        Assert.assertEquals((Long) 1L, testDatapoint.asInt);
        Assert.assertEquals("1", testDatapoint.startTime);
        Assert.assertEquals("2", testDatapoint.time);
    }

}
