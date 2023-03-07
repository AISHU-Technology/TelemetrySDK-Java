package cn.aishu.exporter.ar_metric;

import org.junit.Test;

import static org.mockito.Mockito.*;

import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import cn.aishu.exporter.common.output.HttpOut;

import cn.aishu.exporter.common.utils.TimeUtil;

public class MetricsExporterTest {

        //
        @Test
        public void testMetrics() throws InterruptedException {
                // AttributesBuilder attributesBuilder = Attributes.builder();
                // attributesBuilder.put("test", 1, 2).put("test2", 1);
                // Attributes attribute = attributesBuilder.build();

                String instrumentScopeName = "Opentelemetry-Java";
                Resource resource = Resource.getDefault();
                // Resource resource = Resource.create(attribute);
                System.out.println(resource.toString());
                // build the meter provider
                PeriodicMetricReader reader = PeriodicMetricReader
                                .builder(MetricsExporter().create())
                                .build();
                SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                                .registerMetricReader(reader)
                                .setResource(resource)
                                .build();
                // build meter with instrument scope name
                Meter meter = sdkMeterProvider.meterBuilder(instrumentScopeName).build();
                Attributes attributes = Attributes.of(AttributeKey.stringKey("key.1.2"), "SomeWork",
                                AttributeKey.stringKey("key.1.3"), "SomeWork");
                // build counter metric, only record positive values, such as numbers of request
                // count
                LongCounter counter = meter.counterBuilder("http_request_count")
                                .setDescription("测试")
                                .setUnit("1")
                                .build();
                counter.add(10, attributes);
                // build upDownCounter metric, record posivite or negative values, such as the
                // queue size
                LongUpDownCounter upDownCounter = meter.upDownCounterBuilder("queue_size")
                                .setDescription("queue size")
                                .setUnit("1")
                                .build();
                upDownCounter.add(10, attributes);
                upDownCounter.add(-5, attributes);
                // build gauge metrics, record instantaneous values, such as cpu utilization
                // yuo can replace the record value with golabel variable or func
                meter.gaugeBuilder("cup_utilization").buildWithCallback(measurement -> {
                        measurement.record(21.0, attributes);
                });
                DoubleHistogram testHistogram = meter.histogramBuilder("histogram_test")
                                .setDescription("测试histogram")
                                .setUnit("test")
                                .build();
                testHistogram.record(20);
                testHistogram.record(500);
                io.opentelemetry.sdk.common.CompletableResultCode res = reader.shutdown();
                System.out.println(res.isDone());

                TimeUtil.sleepSecond(10);
        }
}
