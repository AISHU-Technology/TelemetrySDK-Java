package cn.aishu.exporter.ar_trace;


import cn.aishu.exporter.common.output.StdSender;
import cn.aishu.exporter.common.utils.TimeUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.Assert;
import org.junit.Test;

public class ArExporterTest {

    @Test
    public void testTraceExporterHttp(){
        Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example2"));

        // Set to process the spans by the Jaeger Exporter
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
//                        .addSpanProcessor(BatchSpanProcessor.builder(LoggingSpanExporter.create()).build())

                        //1. 导出到标准输出
                        .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))

                        //2. 导出到AnyRobot:注意切换到对应地址：
//                        .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create(
//                                HttpSender.create("http://10.4.15.62/api/feed_ingester/v1/jobs/job-0988e01371fd21c9/events"))))

                        //3. 导出到AnyRobot:（可配置发送相关参数）
//                        .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create(
//                                HttpSender.create("http://10.4.68.236:13048/api/feed_ingester/v1/jobs/job-4f1931764308121e/events",
//                                        Retry.create(true,5,15,30),
//                                        true, 4096))))
//

                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        // it's always a good idea to shut down the SDK cleanly at JVM exit.
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        final Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample2");

        Span span = tracer.spanBuilder("Start my wonderful use case2").startSpan();
        span.addEvent("Event 02");

        span.end();
        Assert.assertNotNull(tracer);
        TimeUtil.sleepSecond(5);
    }


    @Test
    public  void testChain () {
        final Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example2"));

        // Set to process the spans by the Jaeger Exporter
        final SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))
                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        final OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        // it's always a good idea to shut down the SDK cleanly at JVM exit.
//    Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample2");
        Span span = tracer.spanBuilder("main").startSpan();
        try (Scope scope = span.makeCurrent()) {
            doSomething(tracer);
        } finally {
            span.end();
        }
        Assert.assertNotNull(tracer);

        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        TimeUtil.sleepSecond(1);
    }

    private static void doSomething(Tracer tracer) {
        Span span = tracer.spanBuilder("doSomething").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Do some work here
            doSomethingElse(tracer);
        } finally {
            span.end();
        }
    }

    private static void doSomethingElse(Tracer tracer) {
        Span span = tracer.spanBuilder("doSomethingElse").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Do some more work here

        } finally {
            span.end();
        }
    }

    @Test
    public void testCreate(){
        ArExporter arExporter = ArExporter.create();
        Assert.assertNotNull(arExporter);
    }

    @Test
    public void testCreateWithSender(){
        ArExporter arExporter = ArExporter.create(new StdSender());
        Assert.assertNotNull(arExporter);
    }

    @Test
    public void testCreateWithNull(){
        ArExporter arExporter = ArExporter.create(null);
        Assert.assertNotNull(arExporter);
    }

}


