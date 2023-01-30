### sdk使用方式(拉取 2.2.0 版本)：
##### 1. 命令行：
    1.1 $ git clone ssh://devops.aishu.cn:22/AISHUDevOps/ONE-Architecture/_git/TelemetrySDK-Java
    1.2 $ git checkout -b 2.2.0 origin/2.2.0
    1.3 $ mvn clean install

##### 2. 在pom.xml里添加：
    <dependency>
        <groupId>com.eisoo</groupId>
        <artifactId>TelemetrySDK-Logger</artifactId>
        <version>2.2.0</version>
    </dependency>

##### 3. 使用代码
    //1.字符串日志：
    public void testString(){
        Logger logger = LoggerFactory.getLogger("test");  //给个字符类型的日志名称，生成日志实例
        SamplerLogConfig.setLevel(Level.TRACE);          //（可选）配置系统日志等级，默认是INFO
        logger.trace("hello world");
        logger.debug("test");
        logger.info("test");                             //生成info级别的字符串日志：test
        logger.warn("test");
        logger.error("test");
        logger.fatal("test");
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
        final Logger logger = LoggerFactory.getLogger("testBody");  //生成日志实例
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

        Map<String, Object> attr =new HashMap<>();
        attr.put("animal1", animal);
        Attributes attributes = new Attributes(attr);

        logger.info("bodyAbc", attributes);
    }

    //3.1 用http或者https方式发送event，可以设置一些自定义配置
       public void testObjectHttpsSend() throws InterruptedException {
        //若要使用http发送log，需要以下设置，记得修改url地址
        SamplerLogConfig.setDestination(new HttpOut("http://10.4.130.68/api/feed_ingester/v1/jobs/job-090a252e12fc6960/events"));

        //若要使用https发送log，需要以下设置，记得修改url地址
        //SamplerLogConfig.setDestination(new HttpsOut("https://10.4.130.68/api/feed_ingester/v1/jobs/job-090a252e12fc6960/events"));

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
    }

    //4.1可选择在resources添加配置文件slog.properties设置service和http.url或https.url,如：
        service.instance.id=myServiceInstanceId
        service.name=myServiceName
        service.version=myServiceVersion

        //http或者https，填一种方式就好，默认是打印在标准输出
        http.url=http://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events
        //或
        https.url=https://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events

    //5.1当使用trace时，应当把SpanContext中的TraceId和SpanId传入log，以便log能获得相应的TraceId和SpanId
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
        SpanContext spanContext = span.getSpanContext();
        Link link = new Link(spanContext.getTraceId(), spanContext.getSpanId());
        logger.info(message, link);
    
        span.end();

    }
