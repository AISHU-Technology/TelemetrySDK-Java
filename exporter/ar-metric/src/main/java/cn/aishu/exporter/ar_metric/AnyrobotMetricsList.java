package cn.aishu.exporter.ar_metric;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import cn.aishu.exporter.common.utils.JsonUtil;
import cn.aishu.exporter.common.output.Serializer;

import org.apache.commons.logging.Log;
import io.opentelemetry.sdk.metrics.data.MetricData;

public class AnyrobotMetricsList implements Serializer {
    List<AnyrobotScopeResource> list;

    public AnyrobotMetricsList(Collection<MetricData> metrics, Log log) {
        this.list = new ArrayList<>();
        for (MetricData metricData : metrics) {
            AnyrobotScopeResource anyrobotScopeResource = new AnyrobotScopeResource(metricData, log);
            list.add(anyrobotScopeResource);
        }
    }

    public String toJson() {
        return JsonUtil.toJson(this.list);
    }
}
