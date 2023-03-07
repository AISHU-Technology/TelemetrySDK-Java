package cn.aishu.exporter.ar_trace;

import cn.aishu.exporter.common.output.Sender;

public class ArExporterBuilder {
    private Sender sender;

    public ArExporterBuilder() {
    }

    public ArExporterBuilder(Sender sender) {
        this.sender = sender;
    }

    public ArExporterBuilder setSender(Sender sender){
        this.sender =sender;
        return this;
    }

    public ArExporter build(){
        return new ArExporter(sender);
    }
}
