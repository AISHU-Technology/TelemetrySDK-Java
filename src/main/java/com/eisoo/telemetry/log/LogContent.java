package com.eisoo.telemetry.log;


import com.eisoo.telemetry.log.constant.KeyConstant;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.SerializedName;


public class LogContent {
    @SerializedName("Link")
    private Link link = new Link();

    @SerializedName("Timestamp")
    private String timestamp;

    @SerializedName("SeverityText")
    private String severityText = Level.INFO.toString();

    @SerializedName("Body")
    private Map<String, Object> body = new HashMap<>();

    @SerializedName("Attributes")
    private Map<String, Object> attributes = new HashMap<>();

    @SerializedName("Resource")
    private Map<String, Object> resource = new TreeMap<>();


    public LogContent() {
        timestamp = DateUtil.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").replace("+",  String.format("%04d", (System.nanoTime() % 1000000L) / 100) + "+");
        body.put(KeyConstant.MESSAGE.toString(), "");
        resource.put("host", new Host());
        resource.put("os", new Os());
        resource.put("service", new Service());
        resource.put("telemetry", new Telemetry());
    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public void setResource(Map<String, Object> resource) {
        this.resource = resource;
    }

    public Map<String, Object> getResource() {
        return resource;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
