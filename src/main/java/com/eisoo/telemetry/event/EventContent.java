package com.eisoo.telemetry.event;


import cn.hutool.core.date.DateUtil;
import com.eisoo.telemetry.event.utils.IdGenerator;
import com.github.f4b6a3.ulid.UlidCreator;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EventContent {

    @SerializedName("EventID")
    private  String eventID = UlidCreator.getUlid().toString();

    @SerializedName("EventType")
    private  String eventType = "Default.EventType";

    @SerializedName("Time")
    private  String time = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

    @SerializedName("Level")
    private String level = Level.INFO.toString();

    @SerializedName("Resource")
    private Map<String, Object> resource = new HashMap<>();

    @SerializedName("Subject")
    private  String subject = "";

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SerializedName("Link")
    private Link link = new Link(IdGenerator.random().generateTraceId(),IdGenerator.random().generateSpanId());


    @SerializedName("Data")
    private Object data = "";


    public EventContent() {
        resource.put("host", new Host());
        resource.put("os", new Os());
        resource.put("service", new Service());
        resource.put("telemetry", new Telemetry());

    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setData(Object o){
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
