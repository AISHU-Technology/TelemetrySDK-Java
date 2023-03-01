package com.eisoo.telemetry.ar_metric;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eisoo.telemetry.common.Destination;
import com.eisoo.telemetry.output.StdOut;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

public final class MetricsExporter implements MetricExporter {
    public final Log log;
    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private final AggregationTemporality aggregationTemporality;
    private final Destination destination;

    /**
     * Returns a new {@link HttpMetricsExporter} with an aggregation temporality
     * of {@link
     * AggregationTemporality#CUMULATIVE}.
     */
    public static MetricsExporter create() {
        return create(AggregationTemporality.CUMULATIVE);
    }

    /**
     * Returns a new {@link HttpMetricsExporter} with the given
     * {@code aggregationTemporality}.
     */

    public static MetricsExporter create(AggregationTemporality aggregationTemporality) {
        return create(aggregationTemporality, Destination.getDefaultDestination());
    }

    public static MetricsExporter create(AggregationTemporality aggregationTemporality, Destination destination) {
        Log defaultLog = LogFactory.getLog(MetricsExporter.class);
        return new MetricsExporter(aggregationTemporality, destination, defaultLog);
    }

    public static MetricsExporter create(AggregationTemporality aggregationTemporality, Destination destination,
            Log log) {
        return new MetricsExporter(aggregationTemporality, destination, log);
    }

    private MetricsExporter(AggregationTemporality aggregationTemporality, Destination destination, Log log) {
        this.aggregationTemporality = aggregationTemporality;
        this.destination = destination;
        this.log = log;
        destination.init(log);
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
                this.destination.write(anyrobotScopeResource);
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
            destination.flush();
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
