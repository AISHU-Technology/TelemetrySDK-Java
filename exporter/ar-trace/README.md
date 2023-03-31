# 开发指南

## 导入TelemetrySDK-Trace(Java)
**第1步** 检查版本兼容性
Trace
- 此SDK使用的是java1.8， 检查待埋点业务代码的Java版本是否符合要求，。
- 在pom.xml中引入相应版本的sdk包

``` 
本项目生成包含所有依赖包的命令： 
由于本项目依赖：opentelemetry-exporter-common，需要到common项目里运行命令：mvn clean install assembly:assembly -DskipTests
生成两个jar包： opentelemetry-exporter-common-2.4.0.jar 和 opentelemetry-exporter-common-2.4.0-jar-with-dependencies.jar
再回到本项目ar-trace,运行命令：mvn clean install assembly:assembly -DskipTests
生成两个jar包: opentelemetry-exporter-ar-trace-2.4.0.jar和 opentelemetry-exporter-ar-trace-2.4.0-jar-with-dependencies.jar
``` 

-  opentelemetry-exporter-ar-trace组件 需要依赖 opentelemetry-exporter-common组件
-  我们有四个包：
```
  opentelemetry-exporter-common-2.4.0.jar //小包，需要有maven仓库下载其它第三方包，如io.opentelemetry
  opentelemetry-exporter-common-2.4.0-jar-with-dependencies.jar //大包，包含了所有依赖包
  opentelemetry-exporter-ar-trace-2.4.0.jar //小包，需要有maven仓库下载其它第三方包，如io.opentelemetry
  opentelemetry-exporter-ar-trace-2.4.0-jar-with-dependencies.jar   //大包，包含了所有依赖包，包括opentelemetry-exporter-common
```

### 导包方法（根据部署环境选择以下三种方法的一种即可）
#### 1.1 最佳实践：【离线环境下可以使用】
###### 1.1.1 把trace大包（opentelemetry-exporter-ar-trace-2.4.0-jar-with-dependencies.jar）用以下命令安装到maven仓库：【注意：-Dfile指定jar包的地址填写正确。】
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-ar-trace-2.4.0-jar-with-dependencies.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-ar-trace -Dversion=2.4.0 -Dpackaging=jar
###### 1.1.2 在pom.xml中引用：
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-trace</artifactId>
    <version>2.4.0</version>
</dependency>
```

#### 1.2 对于需要同时使用【ar-trace, ar-metrics, ar-log】且不可以连外网拉第三方库的情况：【优点: 离线环境下可以使用，比同时使用三个大包体积小】
###### 1.2.1 把common大包安装到maven仓库：【注意：-Dfile指定jar包的地址填写正确。】
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-common-2.4.0-jar-with-dependencies.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-common -Dversion=2.4.0 -Dpackaging=jar
###### 1.2.2 把ar-trace小包安装到maven仓库：
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-ar-trace-2.4.0.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-ar-trace -Dversion=2.4.0 -Dpackaging=jar
###### 1.2.3 把ar-metrics小包安装到maven仓库：
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-ar-metrics-2.4.0.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-ar-metrics -Dversion=2.4.0 -Dpackaging=jar
###### 1.2.4 把ar-log小包安装到maven仓库：
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-ar-log-2.4.0.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-ar-log -Dversion=2.4.0 -Dpackaging=jar
###### 1.2.5 在pom.xml中引用：
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-common</artifactId>
    <version>2.4.0</version>
</dependency>

<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-trace</artifactId>
    <version>2.4.0</version>
</dependency>

<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-metrics</artifactId>
    <version>2.4.0</version>
</dependency>

<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-log</artifactId>
    <version>2.4.0</version>
</dependency>
```

#### 1.3  对于可以连外网拉第三方库的情况：【可以安装common小包和trace小包，优点：体积小】
###### 1.3.1 把common大包安装到maven仓库：【注意：-Dfile指定jar包的地址填写正确。】
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-common-2.4.0.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-common -Dversion=2.4.0 -Dpackaging=jar
###### 1.3.2 把ar-trace小包安装到maven仓库：
- mvn install:install-file -Dfile=D:/jar/opentelemetry-exporter-ar-trace-2.4.0.jar -DgroupId=cn.aishu -DartifactId=opentelemetry-exporter-ar-trace -Dversion=2.4.0 -Dpackaging=jar
###### 1.3.3 在pom.xml中引用：

```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-common</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-exporter-ar-trace</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.14</version>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-core</artifactId>
    <version>5.8.14</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>1.23.1</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.2.3</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
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


//跨函数调用：
 public  void testChain () {
        final Resource serviceNameResource =
                Resource.create(io.opentelemetry.api.common.Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example2"));

        // Set to process the spans by the Jaeger Exporter
        final SdkTracerProvider tracerProvider =
                SdkTracerProvider.builder()
                        .addSpanProcessor(SimpleSpanProcessor.create(new ArExporter()))

                        //发送到anyRobot：
//                    .addSpanProcessor(SimpleSpanProcessor.create(ArExporter.create(
//                                HttpSender.create("http://10.4.15.62/api/feed_ingester/v1/jobs/job-0988e01371fd21c9/events",
//                                        Retry.create(true,5,15,30),
//                                        true, 4096))))

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
```

