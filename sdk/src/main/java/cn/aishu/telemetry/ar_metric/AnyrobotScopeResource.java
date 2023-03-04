package cn.aishu.telemetry.ar_metric;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;

import cn.aishu.telemetry.common.KeyValue;
import cn.aishu.telemetry.common.SerializeToString;
import cn.aishu.telemetry.utils.JsonUtil;

import io.opentelemetry.sdk.metrics.data.MetricData;
import cn.aishu.telemetry.resource.Resource;

import com.google.gson.annotations.SerializedName;

public class AnyrobotScopeResource implements SerializeToString {

    @SerializedName("Resource")
    public List<KeyValue> resource;
    private transient String schemaURL;
    @SerializedName("ScopeMetrics")
    public List<AnyrobotScopeMetrics> scopeMetrics;

    public AnyrobotScopeResource(MetricData metricData, Log log) {
        this.resource = Resource.getResource(metricData.getResource().getAttributes());
        this.schemaURL = metricData.getResource().getSchemaUrl();
        this.scopeMetrics = new ArrayList<>();
        this.scopeMetrics.add(new AnyrobotScopeMetrics(metricData, log));
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }
}
