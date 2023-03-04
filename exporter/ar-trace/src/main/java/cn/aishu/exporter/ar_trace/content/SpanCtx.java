package cn.aishu.exporter.ar_trace.content;

import com.google.gson.annotations.SerializedName;
import io.opentelemetry.api.trace.SpanContext;

import java.util.Map;

public class SpanCtx {

    public SpanCtx(SpanContext sc) {
        this.traceID = sc.getTraceId();
        this.spanID = sc.getSpanId();
        this.traceFlags = sc.getTraceFlags().asHex();
        Map<String, String> traceStateMap = sc.getTraceState().asMap();
        if(traceStateMap.isEmpty()) {
            //为了适配anyRobot接收器的格式
            this.traceState = "";
        }else {
            this.traceState = traceStateMap.toString();
        }
        this.remote = sc.isRemote();
    }


    @SerializedName("TraceID")
    private String traceID;

    @SerializedName("SpanID")
    private String spanID;

    @SerializedName("TraceFlags")
    private String traceFlags;

    @SerializedName("TraceState")
    private String traceState;

    @SerializedName("Remote")
    private boolean remote;

}
