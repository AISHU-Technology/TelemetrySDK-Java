# 开发指南

## 导入TelemetrySDK-Metric(Java)
**第1步** 检查版本兼容性
Metric
- java版本：1.8
- 在pom.xml中引入相应版本的sdk包
```
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk-metrics</artifactId>
    <version>1.23.1</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.23.1</version>
</dependency>
```
## 导入Metric Exporter
- 在pom.xml中引入包
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-metrics-exporter</artifactId>
    <version>1.0.0</version>
</dependency>
```
- 对于没有maven本地仓库的情况，将包含依赖的jar包（-jar-with-dependencies）放入项目目录，使用导入本地jar文件的方式引入包，这样可以在离线环境下使用
```
<dependency>
    <groupId>cn.aishu</groupId>
    <artifactId>opentelemetry-metrics-exporter</artifactId>
    <version>1.0.0</version>
    <type>jar</type>
    <scope>system</scope>
    <systemPath>${project.basedir}/opentelemetry-metrics-exporter-1.0.0-jar-with-dependencies.jar</systemPath>
</dependency>
```

## 使用TelemetrySDK-Metric(Java)进行代码埋点生产指标数据
**第1步** 新增依赖：以下为新增汇总，以实际使用为准。
```
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;

import cn.aishu.exporter.common.output.StdSender;
import cn.aishu.exporter.common.output.HttpSender;
```

**第2步** 修改业务代码
- 修改前
```
public static int add(int a,int b){
    return a+b;
}
```
- 修改后
```
//函数所在类初始化时获取作为全局量的meter，对需要记录的metric信息进行初始化
LongCounter counter = meter.counterBuilder("add")
.setDescription("记录add函数总和")
.setUnit("1")
.build();


//确定每次记录时所需的attributes标签，在初始化时写死或者记录时实时填写都可以
Attributes attributes = Attributes.of(AttributeKey.stringKey("key.1.2"), "SomeWork",AttributeKey.stringKey("key.1.3"), "SomeWork");


public static int add(int a,int b){
    int res=a+b;
    //传入需要记录的值和attributes标签
    counter.add(res, attributes);
    return res;
}
```
### 上报指标数据到AnyRobot
**第1步** 本地调试
```
public class App {
        public static void main(String[] args) throws InterruptedException {
                //自定义所需的attributes示例
                // AttributesBuilder attributesBuilder = Attributes.builder();
                // attributesBuilder.put("test", 1, 2).put("test2", 1);
                // Attributes attribute = attributesBuilder.build();

                String instrumentScopeName = "Opentelemetry-Java";
                Resource resource = Resource.getDefault();
                //根据自定义attributes初始化resource的示例，一般用getDefault就行了
                // Resource resource = Resource.create(attribute);
                System.out.println(resource.toString());
                // 创建meter provider
                PeriodicMetricReader reader = PeriodicMetricReader
                                .builder(MetricsExporter.create(
                                        new StdSender()                                         //导出到标准输出
//                                        HttpSender.create("http://localhost:8080/")           //导出到Ar接收器
                                ))
                                .setInterval(Duration.ofDays(1)).build();
                SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                                .registerMetricReader(reader)
                                .setResource(resource)
                                .build();
                // 通过instrumentScopeName创建meter
                List<String> testList = new ArrayList<String>();
                testList.add("test1");
                testList.add("test2");
                Meter meter = sdkMeterProvider.meterBuilder(instrumentScopeName).build();

                //创建通用attributes
                Attributes attributes = Attributes.of(AttributeKey.stringKey("key.1.2"), "SomeWork",
                                AttributeKey.stringKey("key.1.3"), "SomeWork",
                                AttributeKey.booleanKey("testBool"), false,
                                AttributeKey.stringArrayKey("testArray"), testList);
                // 在记录单调累加数据时，创建counter计数器
                LongCounter counter = meter.counterBuilder("http_request_count")
                                .setDescription("测试")
                                .setUnit("1")
                                .build();
                //进行记录
                counter.add(10, attributes);
                // 记录可能发生增减两种情况的累计值时，使用UpDownCounter
                LongUpDownCounter upDownCounter = meter.upDownCounterBuilder("queue_size")
                                .setDescription("queue size")
                                .setUnit("1")
                                .build();
                //进行增减两种记录
                upDownCounter.add(10, attributes);
                upDownCounter.add(-5, attributes);
                // 记录瞬时量时，使用gauge计数器
                // 可以使用回调函数的方式调用计数器记录方法
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
        }
}

```
运行后查看标准输出是否正常输出数据


**第2步** 获取上报地址
- 在AnyRobot管理端创建Metric采集任务并生成上报地址供数据源端使用，如`https://a.b.c.d/api/feed_ingester/v1/jobs/abcd4f634e80d530/events` 。



**第3步** 上报到AnyRobot
- 将获取的上报地址作为参数传入
```
//在初始化reader时使用httpSender或者httpsSender以切换http发送或者https发送
PeriodicMetricReader reader = PeriodicMetricReader.builder(MetricsExporter.create(HttpSender.create("发送地址",null, true, 10))).build();

//通过传入Retry对象的方式可改变默认的重试规则
PeriodicMetricReader reader = PeriodicMetricReader.builder(MetricsExporter.create(HttpSender.create("发送地址",Retry.create(true, 2, 10, 20), true, 10))).build();

```


