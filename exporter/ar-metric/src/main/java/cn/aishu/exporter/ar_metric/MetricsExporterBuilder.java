package cn.aishu.exporter.ar_metric;

import cn.aishu.exporter.common.output.Retry;

import org.apache.commons.logging.Log;

public class MetricsExporterBuilder {
    private String addr;
    private Retry retry;
    private boolean isGzip = true;
    private Log log;

    public MetricsExporterBuilder() {
    }

    public MetricsExporterBuilder(Retry retry) {
        this.retry = retry;
    }

    public MetricsExporterBuilder setSendAddr(String addr) {
        this.addr = addr;
        return this;
    }

    public MetricsExporterBuilder setRetry(Retry retry) {
        this.retry = retry;
        return this;
    }

    public MetricsExporterBuilder setGzip(boolean isGzip) {
        this.isGzip = isGzip;
        return this;
    }

    public MetricsExporterBuilder setLog(Log log) {
        this.log = log;
        return this;
    }

    public MetricsExporter build() {
        if (log != null) {
            return new MetricsExporter(addr, retry, isGzip, log);
        } else {
            return new MetricsExporter(addr, retry, isGzip);
        }
    }
}
