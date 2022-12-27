### sdk使用方式(拉取 2.4.0 版本)：
##### 1. 命令行：
    1.1 $ git clone ssh://devops.aishu.cn:22/AISHUDevOps/ONE-Architecture/_git/TelemetrySDK-Java -b 2.4.0
    
    1.2 $ mvn clean install

##### 2. 在pom.xml里添加：
    <dependency>
        <groupId>com.eisoo</groupId>
        <artifactId>SamplerEvent</artifactId>
        <version>2.4.0</version>
    </dependency>

##### 3. 使用代码
    //1.字符串日志：
    public void testMapStdout() {
        Event event = EventFactory.getEvent(this.getClass());   //生成日志实例
        HashMap<String, String> dataContent =new HashMap<>();
        dataContent.put("data","test123str");
        event.info(dataContent);
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

    //2.1 用http或者https方式发送event，可以设置一些自定义配置
     public void testObjectHttpSend() throws InterruptedException {

        //若要使用http发送event，需要以下设置，默认打印到标准输出
         EventConfig.setDestination(new HttpOut("http://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events"));

        //若要使用https发送event，需要以下设置
        // EventConfig.setDestination(new HttpsOut("https://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events"));

        Event event = EventFactory.getEvent(this.getClass());   //生成日志实例

        //创建data：自定义类型：Animal
        final Animal animal = new Animal("little cat4", 2);  //

        //可创建自定义的：EventType
        EventType eventType = new EventType("myEventType");

        //可创建自定义的：service
        Service service = new Service();
        service.setName("myServiceName");
        service.setInstance("myServiceInstance");
        service.setVersion("myServiceVersion2.4");

        //可创建自定义的：Subject
        Subject subject = new Subject("v1.1.3");

        //可创建自定义的：service
        Link link = new Link();
        link.setTraceId("a64dfb055e90ccab9bbce30ab31040df");
        link.setSpanId("217400e1dbf690f9");

        //把刚刚自定义的各个配置添加到event：
        event.warn(animal,service,subject,link,eventType);      //生成warn级别的event
    }

    //2.2可设置event级别
    public void testAllLevel() throws InterruptedException {
        final Event event = EventFactory.getEvent("this.getClass()");  //生成日志实例

        EventConfig.setLevel(Level.ERROR);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        EventConfig.setLevel(Level.WARN);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        EventConfig.setLevel(Level.INFO);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        final Animal animal = new Animal("little cat5", 2);         //创建event自定义的data内容

        event.info(animal);                                        //生成info级别的字符串日志：test
        event.warn(animal);
        event.error(animal);
    }





 
