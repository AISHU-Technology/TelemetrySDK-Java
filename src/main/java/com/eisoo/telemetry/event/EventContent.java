package com.eisoo.telemetry.event;


import cn.hutool.core.date.DateUtil;
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
    private String time;

    @SerializedName("Level")
    private String level = Level.INFO.toString();

    @SerializedName("Attributes")
    private Map<String, Object> attributes = new HashMap<>();

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
    private Link link = null;

    @SerializedName("Data")
    private Object data = "";


    public EventContent() {
        time = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").replace("+",  String.format("%04d", (System.nanoTime() % 1000000L) / 100) + "+");

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

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
