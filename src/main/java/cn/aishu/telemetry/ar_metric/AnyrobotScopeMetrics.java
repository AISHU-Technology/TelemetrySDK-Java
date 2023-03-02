package cn.aishu.telemetry.ar_metric;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;

import com.google.gson.annotations.SerializedName;

import io.opentelemetry.sdk.metrics.data.*;

public class AnyrobotScopeMetrics {
    @SerializedName("Scope")
    AnyrobotScope scope;
    @SerializedName("Metrics")
    List<AnyrobotMetrics> metrics;

    public void setScope(AnyrobotScope scope) {
        this.scope = scope;
    }

    public AnyrobotScope getScope() {
        return this.scope;
    }

    public List<AnyrobotMetrics> getMetrics() {
        return this.metrics;
    }

    public AnyrobotScopeMetrics(MetricData metricData, Log log) {
        this.metrics = new ArrayList<AnyrobotMetrics>();
        this.scope = new AnyrobotScope();
        this.scope.name = metricData.getInstrumentationScopeInfo().getName();
        this.scope.schemaURL = metricData.getInstrumentationScopeInfo().getSchemaUrl();
        this.scope.version = metricData.getInstrumentationScopeInfo().getVersion();
        AnyrobotMetrics tempMetrics = new AnyrobotMetrics(metricData, log);
        this.metrics.add(tempMetrics);
    }
}
