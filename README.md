### 项目介绍
本项目是基于 Opentelemetry 官方定义的 Log 数据模型实现的 SDK，包括以下组件
sdk : 基于 Opentelemetry Log API 的具体实现，用于生成程序日志、业务日志
exporters: Log 数据导出的具体实现，支持标准输出、上报到数据接收器等导出方式
### 项目 layout 说明
```
├── exporter             数据导出器
│   ├── ar-metric        指标数据导出到 AnyRobot 数据接收器
│   │   ├── pom.xml
│   │   └── src
│   └── common           公共包，封装数据导出协议和编码
│       ├── pom.xml
│       └── src
├── README.md
└── sdk                  SDK
    ├── pom.xml
    └── src
        ├── main
        └── test
```

### sdk使用方式(拉取 2.0.0 版本)：
##### 1. 命令行：
    1.1 $ git clone ssh://devops.aishu.cn:22/AISHUDevOps/ONE-Architecture/_git/TelemetrySDK-Java -b 2.1.0

    1.2 $ mvn clean install

##### 2. 在pom.xml里添加：
    <dependency>
        <groupId>cn.aishu</groupId>
        <artifactId>SamplerLogger</artifactId>
        <version>2.1.0</version>
    </dependency>

##### 3. 使用代码
    //1.字符串日志：
    public void testString(){
        Logger logger = LoggerFactory.getLogger("test");  //生成日志实例
        SamplerLogConfig.setLevel(Level.TRACE);                 //（可选）配置系统日志等级，默认是DEBUG
        logger.trace("hello world");
    }
    //可以看见打印信息

    //2.在body添加自定义类日志：
    //测试用的自定义类:Animal
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

    //2.1测试给Body添加Animal类的实例
    public void testBody() {
        final Logger logger = LoggerFactory.getLogger("test");  //生成日志实例
        //Body: Animal实例
        final Animal animal = new Animal();
        animal.setName("little cat");
        animal.setAge(2);

        final Body body = new Body();
        body.setType("animal");
        body.setField(animal);

        logger.info(body);
    }


    //2.2测试给Attributes添加Animal类的实例
    public void testAttributes() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());  //生成日志实例

        //Attributes: Animal实例
        final Animal animal = new Animal("little cat", 2);

        final Attributes attributes = new Attributes();
        attributes.setType("animalType");
        attributes.setField(animal);

        logger.info("bodyAbc", attributes);
    }


    //3.当使用trace时，应当把SpanContext传入log，以便log能获得相应的TraceId和SpanId
    public void testTraceIdAndSpanId() {
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

    }
