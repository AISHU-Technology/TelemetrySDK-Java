package com.eisoo.telemetry.event;



import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.output.BufferOut;
import com.eisoo.telemetry.event.output.HttpOut;
import com.eisoo.telemetry.event.output.HttpsOut;
import com.eisoo.telemetry.event.output.Stdout;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class EventEventTest {

    @Test
    public void testMapStdout() {
        Event event = EventFactory.getEvent(this.getClass());   //生成日志实例
        HashMap<String, String> dataContent =new HashMap<>();
        dataContent.put("data","test123str");
        event.info(dataContent);

        //等待打印info级别的event
        try {
            Thread.sleep(1000*1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Animal {
        private String name;
        private Integer age;

        public Animal(String name, Integer age) {
            this.name = name;
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


    @Test
    public void testObjectHttpSend() throws InterruptedException {
        //设置本地缓存用于正则对比测试
        BlockingQueue<String> buffer = setAndGetBufferOutput();

        //若要使用http发送event，需要以下设置
//         EventConfig.setDestination(new HttpOut("http://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events"));

        //若要使用https发送event，需要以下设置
        // EventConfig.setDestination(new HttpsOut("https://10.4.15.62/api/feed_ingester/v1/jobs/job-0e87b9ed98e52c30/events"));

        Event event = EventFactory.getEvent(this.getClass());   //生成日志实例

        //创建data：自定义类型：Animal
        final Animal animal = new Animal("little cat4", 2);  //

        //创建EventType
        EventType eventType = new EventType("myEventType");

        //创建service
        Service service = new Service();
        service.setName("myServiceName");
        service.setInstance("myServiceInstance");
        service.setVersion("myServiceVersion2.4");

        //创建Subject
        Subject subject = new Subject("v1.1.3");

        //创建service
        Link link = new Link();
        link.setTraceId("a64dfb055e90ccab9bbce30ab31040df");
        link.setSpanId("217400e1dbf690f9");

        //把刚刚自定义的各个配置添加到event：
        event.warn(animal,service,subject,link,eventType);                           //生成warn级别的event

        String regEvent = "^\\{\"EventID\":\"[0-9A-Z]{26}\",\"EventType\":\"[^\"]+\",\"Time\":\"[^\"]+\",\"Level\":\"\\w+\",\"Resource\":\\{\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"\\w+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"\\w+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\},\"Subject\":\".*\",\"Link\":\\{\"TraceId\":\"\\w+\",\"SpanId\":\"\\w+\"\\},\"Data\":\\{.*\\}$";
        assertAndPrint(buffer, regEvent);

        //若要测试http或者https网络发生，需要睡眠等一等
//        try {
//            Thread.sleep(1000*5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    //测试消息体Body是字符串时的情况
    @Test
    public void testAllLevel() throws InterruptedException {
        BlockingQueue<String> buffer = setAndGetBufferOutput();
        final Event event = EventFactory.getEvent("this.getClass()");  //生成日志实例

        EventConfig.setLevel(Level.ERROR);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        EventConfig.setLevel(Level.WARN);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        EventConfig.setLevel(Level.INFO);                           //（可选）配置事件等级，默认是INFO，后面的等级设置会覆盖前面的设置
        final Animal animal = new Animal("little cat5", 2);  //

        event.info(animal);                                        //生成info级别的字符串日志：test
        event.warn(animal);
        event.error(animal);

        String regEvent = "^\\{\"EventID\":\"[0-9A-Z]{26}\",\"EventType\":\"[^\"]+\",\"Time\":\"[^\"]+\",\"Level\":\"\\w+\",\"Resource\":\\{\"os\":\\{\"description\":\"[^\"]+\",\"type\":\"\\w+\",\"version\":\"[^\"]+\"\\},\"service\":\\{\"instance\":\"[^\"]+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\},\"host\":\\{\"arch\":\"[^\"]+\",\"ip\":\"[^\"]+\",\"name\":\"[^\"]+\"\\},\"telemetry\":\\{\"sdk\":\\{\"language\":\"\\w+\",\"name\":\"[^\"]+\",\"version\":\"[^\"]+\"\\}\\}\\},\"Subject\":\".*\",\"Link\":\\{\"TraceId\":\"\\w+\",\"SpanId\":\"\\w+\"\\},\"Data\":\\{.*\\}$";

        for (int i = 0; i < 3; i++) {
            assertAndPrint(buffer, regEvent);
        }
    }

    private BlockingQueue<String> setAndGetBufferOutput() {
        EventConfig.setDestination(new BufferOut());
        return BufferOut.getBuffer();
    }

    private void assertAndPrint(BlockingQueue<String> buffer, String s) throws InterruptedException {
        final String logMsg = buffer.take();
        Assert.assertTrue(logMsg.matches(s));
        new Stdout().write(logMsg);
    }

    @Test
    public void testStdout() {
        final Stdout stdout = new Stdout();
        stdout.write("test");
        Assert.assertNotNull(stdout);
    }
}