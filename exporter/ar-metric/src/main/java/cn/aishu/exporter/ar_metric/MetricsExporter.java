package cn.aishu.exporter.ar_metric;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.Stdout;

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

    public static MetricsExporter create() {
        return create(new Stdout());
    }

    public static MetricsExporter create(Sender output) {
        Log defaultLog = LogFactory.getLog(MetricsExporter.class);
        return new MetricsExporter(output, defaultLog);
    }

    public static MetricsExporter create(Sender output, Log log) {
        return new MetricsExporter(output, log);
    }

    private MetricsExporter(Sender output, Log log) {
        this.aggregationTemporality = AggregationTemporality.CUMULATIVE;
        this.output = output;
        this.log = log;
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
        AnyrobotMetricsList anyrobotMetricsList = new AnyrobotMetricsList(metrics, this.log);
        this.output.send(anyrobotMetricsList);
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
