package cn.aishu.exporter.ar_trace;

import cn.aishu.exporter.common.output.Retry;

public class ArExporterBuilder {
    private String addr;
    private Retry retry;
    private boolean isGzip = true;

    public ArExporterBuilder() {
    }

    public ArExporterBuilder(Retry retry) {
        this.retry = retry;
    }

    public ArExporterBuilder setSendAddr(String addr){
        this.addr = addr;
        return this;
    }

    public ArExporterBuilder setRetry(Retry retry){
        this.retry = retry;
        return this;
    }

    public ArExporterBuilder setGzip(boolean isGzip){
        this.isGzip = isGzip;
        return this;
    }

    public ArExporter build(){
        return new ArExporter(addr, retry, isGzip);
    }
}
