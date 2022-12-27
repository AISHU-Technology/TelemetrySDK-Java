package com.eisoo.telemetry.event;


import com.google.gson.annotations.SerializedName;

public class Link {
    @SerializedName("TraceId")
    private  String traceId = "";

    @SerializedName("SpanId")
    private  String spanId = "";

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
