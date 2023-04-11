package cn.aishu.exporter.ar_metric;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cn.aishu.exporter.common.utils.TimeUtil;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeUtil.class)
public class AnyrobotHistogramDatapointTest {

    @Test
    public void testAnyrobotHistogramDatapoint() {
        HistogramPointData mockDataPoint = mock(HistogramPointData.class);
        AttributesBuilder attributesBuilder = Attributes.builder();
        attributesBuilder.put("test", 1, 2).put("test2", 1);
        Attributes attribute = attributesBuilder.build();

        List<Long> testCounts = new ArrayList<Long>();
        testCounts.add(1L);
        testCounts.add(2L);

        List<Double> testBoundaries = new ArrayList<Double>();
        testBoundaries.add(1.0);
        testBoundaries.add(2.0);

        when(mockDataPoint.getAttributes()).thenReturn(attribute);
        when(mockDataPoint.getCount()).thenReturn(1L);
        when(mockDataPoint.getSum()).thenReturn(2.0);
        when(mockDataPoint.getCounts()).thenReturn(testCounts);
        when(mockDataPoint.getBoundaries()).thenReturn(testBoundaries);

        PowerMockito.mockStatic(TimeUtil.class);
        when(TimeUtil.epochNanoToTime(any())).thenReturn("2", "1");

        AnyrobotHistogramDatapoint testDatapoint = new AnyrobotHistogramDatapoint(mockDataPoint);

        Assert.assertEquals((Long) 1L, testDatapoint.count);
        Assert.assertEquals((Double) 2.0, testDatapoint.sum);
        Assert.assertEquals("1", testDatapoint.startTime);
        Assert.assertEquals("2", testDatapoint.time);
        Assert.assertEquals(testBoundaries, testDatapoint.boundaries);
        Assert.assertEquals(testCounts, testDatapoint.bucketCounts);
        Assert.assertNotNull(testDatapoint.getAttributes());
        Assert.assertNotNull(testDatapoint.getSum());
        Assert.assertNotNull(testDatapoint.getBoundaries());
        Assert.assertNotNull(testDatapoint.getCount());
        Assert.assertNotNull(testDatapoint.getBucketCounts());
        Assert.assertNotNull(testDatapoint.getStartTime());
        Assert.assertNotNull(testDatapoint.getTime());
    }
}
