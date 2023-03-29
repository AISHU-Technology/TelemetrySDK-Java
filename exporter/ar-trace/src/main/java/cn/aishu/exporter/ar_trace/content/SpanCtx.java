package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.utils.JsonUtil;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.api.trace.SpanContext;

import java.util.Map;

public class SpanCtx {

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


    public SpanCtx(SpanContext sc) {
        if (sc == null){
            return;
        }
        this.traceID = sc.getTraceId();
        this.spanID = sc.getSpanId();
        this.traceFlags = sc.getTraceFlags().asHex();
        Map<String, String> traceStateMap = sc.getTraceState().asMap();
        //为了适配anyRobot接收器的格式,把map改成string格式
        if(traceStateMap.isEmpty()) {
            this.traceState = "";
        }else {
            this.traceState = JsonUtil.toJson(sc.getTraceState());
        }
        this.remote = sc.isRemote();
    }


}
