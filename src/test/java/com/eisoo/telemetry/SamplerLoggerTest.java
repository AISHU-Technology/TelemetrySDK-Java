package com.eisoo.telemetry;


import com.eisoo.telemetry.log.*;
import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.output.BufferOut;
import com.eisoo.telemetry.log.output.HttpOut;
import com.eisoo.telemetry.log.output.HttpsOut;
import com.eisoo.telemetry.log.output.Stdout;
import com.eisoo.telemetry.log.utils.SleepUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class SamplerLoggerTest {

    //测试消息体Body是字符串时的情况
    @Test
    public void testString() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例

        logger.trace("test");
        SamplerLogConfig.setLevel(Level.TRACE);                           //（可选）配置系统日志等级，默认是INFO级别
        logger.trace("test");
        logger.debug("test");
        logger.info("test");                                        //生成info级别的字符串日志：test
        logger.warn("test");
        logger.error("test");
        logger.fatal("test");

        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{[^\\}]*\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";

        for (int i = 0; i < 6; i++) {
            assertAndPrint(buffer, regLog);
        }
    }

    private BlockingQueue<String> setAndGetBufferOutput() {
        BufferOut bufferOut = new BufferOut();
        SamplerLogConfig.setDestination(bufferOut);
        return  bufferOut.getBuffer();
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
    }

    @Test
    public void testObjectHttpsSend() throws InterruptedException {
        //设置本地缓存用于正则对比测试
//        BlockingQueue<String> buffer = setAndGetBufferOutput();

        //若要使用https发送log，需要以下设置，记得修改url地址
        SamplerLogConfig.setDestination(new HttpsOut("https://10.4.130.68/api/feed_ingester/v1/jobs/job-090a252e12fc6960/events"));

        final Logger logger = LoggerFactory.getLogger(this.getClass()); //生成日志实例

        //创建service
        Service service = new Service();
        service.setName("myServiceName");
        service.setInstanceId("myServiceInstanceId");
        service.setVersion("myServiceVersion2.4");

        //创建Attributes
        Map<String, Object> attr =new HashMap<>();
        attr.put("attr","test123str");
        Attributes attributes = new Attributes(attr);

        //创建link
        Link link = new Link();
        link.setTraceId("a64dfb055e90ccab9bbce30ab31040df");
        link.setSpanId("217400e1dbf690f9");


        //把刚刚自定义的各个配置添加到log：
        logger.info("str123testms", attributes, link, service);
//        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{[^\\}]*\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";
//        assertAndPrint(buffer, regLog);

        //若要测试http或者https网络发生，需要睡眠等一等
        SleepUtil.second(5);

    }

    @Test
    public void testObjectHttpSend() throws InterruptedException {
        //设置本地缓存用于正则对比测试
//        BlockingQueue<String> buffer = setAndGetBufferOutput();

        //若要使用http发送log，需要以下设置，记得修改url地址
        SamplerLogConfig.setDestination(new HttpOut("http://10.4.130.68/api/feed_ingester/v1/jobs/job-090a252e12fc6960/events"));

        final Logger logger = LoggerFactory.getLogger(this.getClass()); //生成日志实例

        //创建service
        Service service = new Service();
        service.setName("myServiceName");
        service.setInstanceId("myServiceInstanceId");
        service.setVersion("myServiceVersion2.4");

        //创建Attributes
        Map<String, Object> attr =new HashMap<>();
        attr.put("attr","test123str");
        Attributes attributes = new Attributes(attr);

        //创建link
        Link link = new Link();
        link.setTraceId("a64dfb055e90ccab9bbce30ab31040df");
        link.setSpanId("217400e1dbf690f9");

        //把刚刚自定义的各个配置添加到event：
//        event.warn(animal,service,subject,link,eventType,attributes);                           //生成warn级别的event
        logger.info("str123testms", attributes, link, service);
//        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{[^\\}]*\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";
//        assertAndPrint(buffer, regLog);

        //若要测试http或者https网络发生，需要睡眠等一等
       SleepUtil.second(5);
    }

    //测试给Body添加Animal类的实例
    @Test
    public void testBody() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger("this.getClass()");  //生成日志实例

        //Body: Animal实例
        final Animal animal = new Animal("little cat", 2);
        animal.setName("little cat");
        animal.setAge(2);

        final Body body = new Body();
        body.setType("animal");
        body.setField(animal);

        logger.info(body);
        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Type\":\"animal\",\"animal\":\\{\"name\":\"little cat\",\"age\":2\\}\\},\"Attributes\":\\{[^\\}]*\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";

        assertAndPrint(buffer, regLog);
    }

    //测试给Attributes添加Animal类的实例
    @Test
    public void testAttributes() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例


        //Attributes: Animal实例
        final Animal animal = new Animal("little cat", 2);

        Map<String, Object> stringObjectMap =new HashMap<>();
        stringObjectMap.put("animal1", animal);
        Attributes attributes = new Attributes();
        attributes.setAttributes(stringObjectMap);

        logger.trace("bodyAbc", attributes);
        logger.warn("bodyAbc", attributes);

        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{\"animal1\":\\{\"name\":\"little cat\",\"age\":2\\}\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";

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
        SpanContext spanContext = span.getSpanContext();
//        Link link = new Link(spanContext.getTraceId(), spanContext.getSpanId());
//        logger.info(message, link);
        logger.info(message, spanContext);
        span.end();

        String regLog = "^\\{\"Link\":\\{\"TraceId\":\"[0-9a-z]{32}\",\"SpanId\":\"[0-9a-z]{16}\"\\},\"Timestamp\":\"[^\"]+\",\"SeverityText\":\"[^\"]+\",\"Body\":\\{\"Message\":\"[^\"]+\"\\},\"Attributes\":\\{[^\\}]*\\},\"Resource\":\\{\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\\{\"id\":\"[^\"]+\"\\},\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\}\\}$";

        assertAndPrint(buffer, regLog);
    }

    @Test
    public void testStdout() {
        final Stdout stdout = new Stdout();
        stdout.write("test");
        Assert.assertNotNull(stdout);
    }

    //测试配置文件
    @Test
    public void testHttpProperties() throws InterruptedException {
        SamplerLogConfig.setConfigFileDestination("testHttpUrl.properties");
        final Logger logger = LoggerFactory.getLogger("testConfig");  //生成日志实例
        logger.info("test");

        //若要测试http或者https网络发生，需要睡眠等一等
        SleepUtil.second(5);
    }

    //测试配置文件
    @Test
    public void testHttpsProperties() throws InterruptedException {
        SamplerLogConfig.setConfigFileDestination("testHttpUrl.properties");
        final Logger logger = LoggerFactory.getLogger("testConfig");  //生成日志实例
        logger.info("test");

        //若要测试http或者https网络发生，需要睡眠等一等
        SleepUtil.second(5);
    }

    //测试配置文件
    @Test
    public void testServiceProperties() throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger("testConfig");  //生成日志实例
        Service service = new Service("testService.properties");
        logger.info("test", service);

        //若要测试标准输出，需要睡眠等一等
        SleepUtil.second(1);
    }

}