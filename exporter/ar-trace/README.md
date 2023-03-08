# 开发指南

本项目生成包含所有依赖包的命令： mvn clean install assembly:assembly -DskipTests


## 导入TelemetrySDK-Trace(Java)
**第1步** 检查版本兼容性
Trace
- 查看SDK[兼容列表](../../../docs/compatibility.md)，检查待埋点业务代码的Java版本是否符合要求。
- 在pom.xml中引入相应版本的sdk包

## 导入Trace Exporter
- 在pom.xml中引入包
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-trace</artifactId>
    <version>1.0.0</version>
    <classifier>jar-with-dependencies</classifier>
</dependency>
```
- 对于没有maven本地仓库的情况，把两个jar包（opentelemetry-exporter-ar-trace-1.0.0.jar
  与 opentelemetry-exporter-ar-trace-1.0.0-jar-with-dependencies.jar）
  放在与项目src同级目录，用以下方法引用。使用导入本地jar文件的方式引入包，这样可以在离线环境下使用
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-trace</artifactId>
    <version>1.0.0</version>
    <type>jar</type>
    <scope>system</scope>
    <systemPath>${project.basedir}/opentelemetry-exporter-ar-trace-1.0.0-jar-with-dependencies.jar</systemPath>
</dependency>
```

## 使用TelemetrySDK-Trace(Java)
以实际使用为准。
```
import cn.aishu.exporter.ar_trace.ArExporter;
import cn.aishu.exporter.common.output.HttpSender;
import cn.aishu.exporter.common.output.HttpsSender;
import cn.aishu.exporter.common.output.Retry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class TraceExporterTest {
    public static void main(String[] args) {
        Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example2"));

        // Set to process the spans by the Jaeger Exporter
        SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
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
    }
}
```

