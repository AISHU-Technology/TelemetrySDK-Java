package com.eisoo.telemetry.event;


import cn.hutool.core.date.DateUtil;
import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.output.HttpOut;
import com.eisoo.telemetry.event.output.HttpsOut;
import com.eisoo.telemetry.event.utils.IdGenerator;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class EventContent {

    @SerializedName("EventID")
    private String eventID = UlidCreator.getUlid().toString();

    @SerializedName("EventType")
    private String eventType = "Default.EventType";

    @SerializedName("Time")
    private String time = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

    @SerializedName("Level")
    private String level = Level.INFO.toString();

    @SerializedName("Resource")
    private Map<String, Object> resource = new HashMap<>();

    @SerializedName("Subject")
    private String subject = "";

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SerializedName("Link")
    private Link link = new Link(IdGenerator.random().generateTraceId(), IdGenerator.random().generateSpanId());


    @SerializedName("Data")
    private Object data = "";


    public EventContent() {
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("event.properties");
        if (resourceAsStream == null) {
            resource.put("service", new Service());
        } else {
            try {
                properties.load(resourceAsStream);
                Service service = new Service();
                String serviceInstance = properties.getProperty("service.instance");
                if (serviceInstance != null) {
                    service.setInstance(serviceInstance);
                }
                String serviceName = properties.getProperty("service.name");
                if (serviceName != null) {
                    service.setName(serviceName);
                }
                String serviceVersion = properties.getProperty("service.version");
                if (serviceVersion != null) {
                    service.setVersion(serviceVersion);
                }
                resource.put("service", service);

                String httpUrl = properties.getProperty("http.url");
                if (httpUrl != null) {
                    EventConfig.setDestination(new HttpOut(httpUrl));
                }
                String httpsUrl = properties.getProperty("https.url");
                if (httpsUrl != null) {
                    EventConfig.setDestination(new HttpsOut(httpsUrl));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        resource.put("host", new Host());
        resource.put("os", new Os());
        resource.put("telemetry", new Telemetry());

    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setData(Object o) {
        this.data = o;
    }

    public Map<String, Object> getResource() {
        return resource;
    }

    public void setResource(Map<String, Object> resource) {
        this.resource = resource;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
