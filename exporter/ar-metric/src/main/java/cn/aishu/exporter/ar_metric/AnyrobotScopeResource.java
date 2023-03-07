package cn.aishu.exporter.ar_metric;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;

import cn.aishu.exporter.common.KeyValue;

import cn.aishu.exporter.common.utils.JsonUtil;
import cn.aishu.exporter.common.Resource;
import cn.aishu.exporter.common.output.Serializer;

import io.opentelemetry.sdk.metrics.data.MetricData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotScopeResource implements Serializer {

    @SerializedName("Resource")
    public List<KeyValue> resource;
    public transient String schemaURL;
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
