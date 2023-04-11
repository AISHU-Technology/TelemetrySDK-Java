package cn.aishu.exporter.ar_metric;


import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.internal.data.*;
import io.opentelemetry.sdk.resources.Resource;
import org.apache.commons.logging.impl.SimpleLog;
import org.junit.Assert;
import org.junit.Test;

public class AnyrobotSumTest  {

    @Test
    public void testGetDataPoints() {
        MetricData metricData1 = ImmutableMetricData.createDoubleSum(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableSumData.empty());
        AnyrobotSum sum1 = new AnyrobotSum(metricData1, new SimpleLog("test"));
        Assert.assertNotNull(sum1);
        MetricData metricData = ImmutableMetricData.createLongSum(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableSumData.empty());
        AnyrobotSum sum = new AnyrobotSum(metricData, new SimpleLog("test"));
        Assert.assertNotNull(sum);
        Assert.assertNotNull(sum.getDataPoints());
        Assert.assertNotNull(sum.getIsMonotonic());
        Assert.assertNotNull(sum.getTemporality());
    }

    @Test
    public void testAnyRobotMetrics() {
        String nameTest = "NameTest";
        String descriptionTest = "DescriptionTest";
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
        MetricData metricData7 = ImmutableMetricData.createDoubleHistogram(Resource.getDefault(), InstrumentationScopeInfo.create("test"),"testName", "testDescription", "testUnit", ImmutableHistogramData.empty());
        AnyrobotMetrics testMetrics7 = new AnyrobotMetrics(metricData7, new SimpleLog("test"));
        Assert.assertNotNull(testMetrics7);
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