package com.eisoo.telemetry;


import com.eisoo.telemetry.log.*;
import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.output.BufferOut;
import com.eisoo.telemetry.log.output.Stdout;
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

import java.util.concurrent.BlockingQueue;

public class SamplerLoggerTest {

    //测试消息体Body是字符串时的情况
    @Test
    public void testString() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例

        logger.trace("test");
        SamplerLogConfig.setLevel(Level.TRACE);                           //（可选）配置系统日志等级，默认是DEBUG
        logger.trace("test");
        logger.debug("test");
        logger.info("test");                                        //生成info级别的字符串日志：test
        logger.warn("test");
        logger.error("test");
        logger.fatal("test");

        String regLog = "^\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":\"\\w+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{\\},\"Resource\":\\{\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"[^\"]+\"\\}\\}$";

        for (int i = 0; i < 6; i++) {
            assertAndPrint(buffer, regLog);
        }
    }

    private BlockingQueue<String> setAndGetBufferOutput() {
        SamplerLogConfig.setDestination(new BufferOut());
        return BufferOut.getBuffer();
    }

    private void assertAndPrint(BlockingQueue<String> buffer, String s) throws InterruptedException {
        final String logMsg = buffer.take();
        Assert.assertTrue(logMsg.matches(s));
        new Stdout().write(logMsg);
    }

    //测试用的自定义类
    private class Animal {
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

        @Override
        public String toString() {
            return "Animal{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    //测试给Body添加Animal类的实例
    @Test
    public void testBody() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger("this.getClass()");  //生成日志实例

        //Body: Animal实例
        final Animal animal = new Animal();
        animal.setName("little cat");
        animal.setAge(2);

        final Body body = new Body();
        body.setType("animal");
        body.setField(animal);

        logger.info(body);

        String regLog = "\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":\"\\w+\",\"Body\":\\{\"Type\":\"animal\",\"animal\":\\{\"name\":\"little cat\",\"age\":2\\}\\},\"Attributes\":\\{\\},\"Resource\":\\{\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"[^\"]+\"\\}\\}$";
        assertAndPrint(buffer, regLog);

    }

    //测试给Attributes添加Animal类的实例
    @Test
    public void testAttributes() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例


        //Attributes: Animal实例
        final Animal animal = new Animal("little cat", 2);

        final Attributes attributes = new Attributes();
        attributes.setType("animalType");
        attributes.setField(animal);

        logger.trace("bodyAbc", attributes);
        logger.warn("bodyAbc", attributes);

        String regLog = "^\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":\"\\w+\",\"Body\":\\{\"Message\":\"\\w+\"\\},\"Attributes\":\\{\"Type\":\"animalType\",\"animalType\":\\{\"name\":\"little cat\",\"age\":2\\}\\},\"Resource\":\\{\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"(.*)\\}\\}$";

        assertAndPrint(buffer, regLog);

    }

    @Test
    public void testTraceIdAndSpanId() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();

        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例


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
        logger.info(message, span.getSpanContext());
        span.end();

        String regLog = "^\\{\"Version\":\"v1.6.1\",\"TraceId\":\"[0-9a-f]{32}\",\"SpanId\":\"[0-9a-f]{16}\",\"Timestamp\":[0-9]{19},\"SeverityText\":\"\\w+\",\"Body\":\\{\"Message\":\"" + message + "\"\\},\"Attributes\":\\{\\},\"Resource\":\\{\"Telemetry.SDK.Version\":\"2.0.0\",\"Telemetry.SDK.Name\":\"Telemetry SDK\",\"Telemetry.SDK.Language\":\"java\",\"HostName\":\"[^\"]+\"\\}\\}$";

        assertAndPrint(buffer, regLog);
    }

    @Test
    public void testStdout() {
        final Stdout stdout = new Stdout();
        stdout.write("test");
        Assert.assertNotNull(stdout);
    }


}