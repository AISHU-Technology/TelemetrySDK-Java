package cn.aishu.telemetry.log;

import cn.aishu.telemetry.log.constant.KeyConstant;
import cn.aishu.telemetry.log.utils.IdGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

public class LogContent {

    @SerializedName("Version")
    private String version = "v1.6.1";

    @SerializedName("TraceId")
    private String traceId = IdGenerator.random().generateTraceId();

    @SerializedName("SpanId")
    private String spanId = IdGenerator.random().generateSpanId();

    @SerializedName("Timestamp")
    private Long timestamp = System.currentTimeMillis() * 1000000L + System.nanoTime() % 1000000L;

    @SerializedName("SeverityText")
    private String severityText = Level.INFO.toString();

    @SerializedName("Body")
    private Map<String, Object> body = new HashMap<>();

    @SerializedName("Attributes")
    private Map<String, Object> attributes = new HashMap<>();

    @SerializedName("Resource")
    private Map<String, String> resource = new HashMap<>();

    public LogContent() {

        body.put(KeyConstant.MESSAGE.toString(), "");
        resource.put("Telemetry.SDK.Name", "Telemetry SDK");
        resource.put("Telemetry.SDK.Version", "2.0.0");
        resource.put("Telemetry.SDK.Language", "java");
        resource.put("HostName", "UnknownHost");
        try {
            resource.put("HostName", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            // 暂时吃掉该异常
        }

    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

}
