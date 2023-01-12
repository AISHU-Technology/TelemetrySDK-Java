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


    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }


    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
