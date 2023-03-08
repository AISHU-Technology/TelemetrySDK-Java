package cn.aishu.exporter.ar_trace;

//import cn.aishu.exporter.ar_trace.common.KeyValue;
import cn.aishu.exporter.common.output.HttpSender;
import cn.aishu.exporter.common.output.Retry;
import cn.aishu.exporter.common.utils.TimeUtil;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.internal.AttributesMap;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
//import io.opentelemetry.api.common.ArrayBackedAttributes;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArExporterTest {
<<<<<<< HEAD

    @Test
    public void testTraceExporterHttp(){
        Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example2"));
=======
        public static void main(String[] args) {
                Retry.builder().setInitialInterval(1).setMaxInterval(2).setMaxElapsedTime(3).build();
        }

        public static void testBuilder() {
                Retry.builder().setInitialInterval(1).setMaxInterval(2).setMaxElapsedTime(3).build();
        }
>>>>>>> 9a305b1db49f723d94fa2c2f4be12617ca061981

        // Set to process the spans by the Jaeger Exporter
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
//                        .addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build())
                        .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))
//                        .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create(
//                                HttpSender.create("http://10.4.68.236:13048/api/feed_ingester/v1/jobs/job-4f1931764308121e/events",
//                                        Retry.create(true,5,15,30),
//                                        true, 4096)))
//
//                        )
                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

                // Set to process the spans by the Jaeger Exporter
                SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                                // .addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build())
                                // .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))
                                .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.builder()
                                                .setGzip(false)
                                                // .setRetry(new Retry(true, 1,2,3))
                                                // .setRetry(Retry.builder().setInitialInterval(1).setMaxInterval(2).setMaxElapsedTime(3).build())
                                                // .setSendAddr("http://localhost:8089")
                                                .setSendAddr("http://localhost:8080")
                                                // .setSendAddr("http://10.4.68.236:13048/api/feed_ingester/v1/jobs/job-4f1931764308121e/events")
                                                .build()))
                                // .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create("http://localhost:8089")))
                                // .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create("http://localhost:8080")))
                                .setResource(Resource.getDefault().merge(serviceNameResource))
                                .build();
                OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

                // Set to process the spans by the Jaeger Exporter
                SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                                // .addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build())
                                // .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))
                                // .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create()))
                                .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.builder()
                                                .setGzip(true)
                                                // .setRetry(new Retry(true, 1,2,3))
                                                .setRetry(Retry.builder().setInitialInterval(1).setMaxInterval(2)
                                                                .setMaxElapsedTime(3).build())
                                                // .setSendAddr("http://localhost:8089")
                                                .build()))
                                // .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create("http://localhost:8089")))
                                // .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create("http://localhost:8080")))
                                .setResource(Resource.getDefault().merge(serviceNameResource))
                                .build();
                OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        span.end();
        TimeUtil.sleepSecond(5);
    }

                span.end();
                TimeUtil.sleepSecond(15);
}

Span span = tracer.spanBuilder("Start my wonderful use case2").startSpan();
