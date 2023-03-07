package cn.aishu.exporter.ar_metric;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.SenderGen;
import cn.aishu.exporter.common.output.Retry;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

public final class MetricsExporter implements MetricExporter {
    public final Log log;
    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private final AggregationTemporality aggregationTemporality;
    private final Sender output;

    /**
     * Returns a new {@link HttpMetricsExporter} with an aggregation temporality
     * of {@link
     * AggregationTemporality#CUMULATIVE}.
     */

    /**
     * Returns a new {@link HttpMetricsExporter} with the given
     * {@code aggregationTemporality}.
     */

    public static MetricsExporterBuilder builder() {
        return new MetricsExporterBuilder();
    }

    public MetricsExporter(String addr, Retry retry, boolean isGzip) {
        this.aggregationTemporality = AggregationTemporality.CUMULATIVE;
        this.log = LogFactory.getLog(MetricsExporter.class);
        this.output = SenderGen.getSender(addr, retry, isGzip);
    }

    public MetricsExporter(String addr, Retry retry, boolean isGzip, Log log) {
        this.aggregationTemporality = AggregationTemporality.CUMULATIVE;
        this.log = log;
        this.output = SenderGen.getSender(addr, retry, isGzip);
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
            this.output.send(anyrobotScopeResource);
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
