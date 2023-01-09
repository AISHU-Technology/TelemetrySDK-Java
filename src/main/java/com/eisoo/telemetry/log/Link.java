package com.eisoo.telemetry.log;


import com.google.gson.annotations.SerializedName;

public class Link {
    @SerializedName("TraceId")
    private  String traceId = "00000000000000000000000000000000";

    @SerializedName("SpanId")
    private  String spanId = "0000000000000000";

    public Link() {
    }

    public Link(String traceId, String spanId) {
        this.spanId = spanId;
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
