package cn.aishu.exporter.ar_metric;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.StdSender;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

public final class MetricsExporter implements MetricExporter {
    public final Log log;
    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private final AggregationTemporality aggregationTemporality;
    private final Sender sender;

    /**
     * Returns a new {@link HttpMetricsExporter} with an aggregation temporality
     * of {@link
     * AggregationTemporality#CUMULATIVE}.
     */

    /**
     * Returns a new {@link HttpMetricsExporter} with the given
     * {@code aggregationTemporality}.
     */

    public static MetricsExporter create() {
        Log log = LogFactory.getLog(MetricsExporter.class);
        Sender sender = new StdSender();
        return new MetricsExporter(sender, log);
    }

    public static MetricsExporter create(Sender sender) {
        Log log = LogFactory.getLog(MetricsExporter.class);
        return new MetricsExporter(sender, log);
    }

    public static MetricsExporter create(Sender sender, Log log) {
        return new MetricsExporter(sender, log);
    }

    private MetricsExporter(Sender sender, Log log) {
        this.aggregationTemporality = AggregationTemporality.CUMULATIVE;
        this.log = log;
        this.sender = sender;
    }

    /**
     * Return the aggregation temporality.
     *
     * @deprecated Use {@link #getAggregationTemporality(InstrumentType)}.
     */
    @Deprecated
    public AggregationTemporality getPreferredTemporality() {
        return aggregationTemporality;
    }

    @Override
    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return aggregationTemporality;
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> metrics) {
        if (isShutdown.get()) {
            return CompletableResultCode.ofFailure();
        }
        for (MetricData metricData : metrics) {
            AnyrobotScopeResource anyrobotScopeResource = new AnyrobotScopeResource(metricData, log);
            this.sender.send(anyrobotScopeResource);
        }
        return CompletableResultCode.ofSuccess();
    }

    /**
     * Flushes the data.
     *
     * @return the result of the operation
     */
    @Override
    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();
        return resultCode.succeed();
    }

    @Override
    public CompletableResultCode shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            log.info("Calling shutdown() multiple times.");
            return CompletableResultCode.ofSuccess();
        }
        return flush();
    }
}
