package com.eisoo.telemetry;

import com.eisoo.telemetry.log.Attributes;
import com.eisoo.telemetry.log.Body;
import com.eisoo.telemetry.log.Level;
import com.eisoo.telemetry.log.SamplerLogger;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.Assert;
import org.junit.Test;


public class SamplerLoggerTest {

    //测试消息体Body是字符串时的情况
    @Test
    public void testString() {
        final SamplerLogger logger = new SamplerLogger();       //生成日志实例
        logger.setLevel(Level.TRACE);                           //（可选）配置系统日志等级，默认是info
        logger.info("test hello world");                   //生成info级别的字符串日志：test hello world
        logger.trace(logger.getLevel().toString());
        logger.debug(logger.getLevel().toString());
        logger.info(logger.getLevel().toString());
        logger.warn(logger.getLevel().toString());
        logger.error(logger.getLevel().toString());
        logger.fatal();
        logger.fatal(logger.getLevel().toString());

        String regLog = "\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":(.*),\"Body\":(.*)\"Message\":(.*),\"Attributes\":(.*),\"Resource\":(.*)\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"(.*)";
        Assert.assertTrue(logger.getResult().matches(regLog));
    }

    //测试用的自定义类
    public class Animal {
        private String name;
        private Integer age;

        public Animal() {
        }

        public Animal(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    //测试给Body添加Animal类的实例
    @Test
    public void testBody() {
        final SamplerLogger logger = new SamplerLogger();
        //Body: Animal实例
        final Animal animal = new Animal();
        animal.setName("little cat");
        animal.setAge(2);

        final Body body = new Body();
        body.setType("animal");
        body.setField(animal);

        logger.info(body);
        String regLog = "\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":(.*),\"Body\":\\{\"Type\":\"animal\",\"animal\":\\{\"name\":\"little cat\",\"age\":2\\}\\},\"Attributes\":(.*),\"Resource\":(.*)\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"(.*)";
        Assert.assertTrue(logger.getResult().matches(regLog));
    }

    //测试给Attributes添加Animal类的实例
    @Test
    public void testAttributes() {
        final SamplerLogger logger = new SamplerLogger();

        //Attributes: Animal实例
        final Animal animal = new Animal("little cat", 2);

        final Attributes attributes = new Attributes();
        attributes.setType("animalType");
        attributes.setField(animal);

        logger.trace("bodyAbc", attributes);
        logger.warn("bodyAbc", attributes);

        String regLog = "\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":(.*),\"Body\":(.*)\"Message\":(.*),\"Attributes\":\\{\"Type\":\"animalType\",\"animalType\":\\{\"name\":\"little cat\",\"age\":2\\}\\}(.*),\"Resource\":\\{\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"(.*)\\}\\}";
        Assert.assertTrue(logger.getResult().matches(regLog));
    }

    @Test
    public void testTraceIdAndSpanId() {
        final SamplerLogger logger = new SamplerLogger();

        Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example"));

        // Set to process the spans by the Jaeger Exporter
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter()))
                        .setResource(Resource.getDefault().merge(serviceNameResource))
                        .build();
        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        // it's always a good idea to shut down the SDK cleanly at JVM exit.
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        final Tracer tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample");
        Span span = tracer.spanBuilder("Start my wonderful use case").startSpan();
        span.addEvent("Event 0");
        final String message = "using existed traceId and spanId";
        System.out.println("traceId:" + span.getSpanContext().getTraceId());
        System.out.println("spanId:" + span.getSpanContext().getSpanId());
        logger.info(message, span.getSpanContext());
        span.end();

        String regLog = "\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":(.*),\"Body\":(.*)\"Message\":(.*)" + message + "(.*),\"Attributes\":(.*),\"Resource\":(.*)\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"(.*)";
        Assert.assertTrue(logger.getResult().matches(regLog));
    }

}