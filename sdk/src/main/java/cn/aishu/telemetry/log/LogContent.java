package cn.aishu.telemetry.log;


import cn.aishu.exporter.common.Host;
import cn.aishu.exporter.common.Os;
import cn.aishu.exporter.common.Telemetry;
import cn.aishu.exporter.common.output.Serializer;
import cn.aishu.exporter.common.utils.JsonUtil;
import cn.aishu.telemetry.log.constant.KeyConstant;

import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.SerializedName;


public class LogContent implements Serializer {
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

    private DateNano dateNano;

    public LogContent() {
        dateNano = new DateNano(new Date(), System.nanoTime());
        body.put(KeyConstant.MESSAGE.toString(), "");
        resource.put("host", Host.getHost());
        resource.put("os", Os.getOs());
        resource.put("service", Service.getService());
        resource.put("telemetry", Telemetry.getTelemetry());
    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }

    public String getSeverityText() {
        return severityText;
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

    class DateNano {
        private Date date;

        private long nano;

        public DateNano(Date date, long nano) {
            this.date = date;
            this.nano = nano;
        }
    }

    public String toJson() {
        timestamp = DateUtil.format(dateNano.date, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX").replace("+", String.format("%04d", (dateNano.nano % 1000000L) / 100) + "+");
        dateNano = null;
        return JsonUtil.toJsonSimple(this);
    }
}
