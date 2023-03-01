package com.eisoo.telemetry.ar_metric;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;

import com.eisoo.telemetry.common.KeyValue;
import com.eisoo.telemetry.common.SerializeToString;
import com.eisoo.telemetry.utils.JsonUtil;

import io.opentelemetry.sdk.metrics.data.MetricData;

import com.google.gson.annotations.SerializedName;

public class AnyrobotScopeResource implements SerializeToString {

    @SerializedName("Resource")
    public List<KeyValue> resource;
    private transient String schemaURL;
    @SerializedName("ScopeMetrics")
    public List<AnyrobotScopeMetrics> scopeMetrics;

    public AnyrobotScopeResource(MetricData metricData, Log log) {
        this.resource = KeyValue.extractFromAttributes(metricData.getResource().getAttributes(), log);
        this.schemaURL = metricData.getResource().getSchemaUrl();
        this.scopeMetrics = new ArrayList<>();
        this.scopeMetrics.add(new AnyrobotScopeMetrics(metricData, log));
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }
}
