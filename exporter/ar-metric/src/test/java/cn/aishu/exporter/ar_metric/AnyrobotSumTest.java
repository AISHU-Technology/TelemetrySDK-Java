package cn.aishu.exporter.ar_metric;


import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.SumData;
import io.opentelemetry.sdk.metrics.internal.aggregator.EmptyMetricData;
import io.opentelemetry.sdk.metrics.internal.data.ImmutableMetricData;
import io.opentelemetry.sdk.metrics.internal.data.ImmutableSumData;
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

}