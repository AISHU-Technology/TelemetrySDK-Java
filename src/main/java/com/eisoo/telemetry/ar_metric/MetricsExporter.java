package com.eisoo.telemetry.ar_metric;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eisoo.telemetry.common.Output;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

public final class MetricsExporter implements MetricExporter {
    public final Log log;
    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private final AggregationTemporality aggregationTemporality;
    private final Output output;

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
        return create(Output.getDefaultDestination());
    }

    public static MetricsExporter create(Output output) {
        Log defaultLog = LogFactory.getLog(MetricsExporter.class);
        return new MetricsExporter(output, defaultLog);
    }

    public static MetricsExporter create(Output output, Log log) {
        return new MetricsExporter(output, log);
    }

    private MetricsExporter(Output output, Log log) {
        this.aggregationTemporality = AggregationTemporality.CUMULATIVE;
        this.output = output;
        this.log = log;
        output.init(log);
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
            try {
                this.output.write(anyrobotScopeResource);
            } catch (Exception e) {
                return CompletableResultCode.ofFailure();
            }
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
        try {
            output.flush();
        } catch (Exception e) {
            return resultCode.fail();
        }
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

    public static class Builder {

    }
}
