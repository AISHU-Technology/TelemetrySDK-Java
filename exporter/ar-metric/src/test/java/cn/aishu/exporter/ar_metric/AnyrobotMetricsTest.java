package cn.aishu.exporter.ar_metric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.metrics.internal.aggregator.EmptyMetricData;
import io.opentelemetry.sdk.metrics.internal.data.*;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;
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

    String nameTest = "NameTest";
    String descriptionTest = "DescriptionTest";
    String unitTest = "UnitTest";

    @Test
    public void testAnyrobotMetricsLongGauge() {
        AnyrobotGauge mockAnyrobotGauge = mock(AnyrobotGauge.class);

        Log mockLog = mock(Log.class);

        MetricData mockMetricData = mock(MetricData.class);

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

    @Test
    public void testAnyRobotMetrics() {
        MetricData metricData1 = ImmutableMetricData.createDoubleSum(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableSumData.empty());
        AnyrobotMetrics testMetrics = new AnyrobotMetrics(metricData1, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics);
        MetricData metricData2 = ImmutableMetricData.createDoubleGauge(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit",  ImmutableGaugeData.empty());
        AnyrobotMetrics testMetrics2 = new AnyrobotMetrics(metricData2, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics2);
        MetricData metricData3 = ImmutableMetricData.createLongSum(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableSumData.empty());
        AnyrobotMetrics testMetrics3 = new AnyrobotMetrics(metricData3, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics3);
        MetricData metricData4 = ImmutableMetricData.createLongGauge(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit",  ImmutableGaugeData.empty());
        AnyrobotMetrics testMetrics4 = new AnyrobotMetrics(metricData4, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics4);
        MetricData metricData5 = ImmutableMetricData.createDoubleSummary(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableSummaryData.empty());
        AnyrobotMetrics testMetrics5 = new AnyrobotMetrics(metricData5, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics5);
        MetricData metricData6 = ImmutableMetricData.createExponentialHistogram(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableExponentialHistogramData.empty());
        AnyrobotMetrics testMetrics6 = new AnyrobotMetrics(metricData6, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics6);
        testMetrics.setName(nameTest);
        testMetrics.setDescription(descriptionTest);
        Assert.assertEquals(nameTest, testMetrics.getName());
        Assert.assertEquals(descriptionTest, testMetrics.getDescription());
        Assert.assertNull(testMetrics.getGauge());
        Assert.assertNotNull(testMetrics.getUnit());
        Assert.assertNull(testMetrics.getHistogram());
        Assert.assertNotNull(testMetrics.getSum());
    }


}
