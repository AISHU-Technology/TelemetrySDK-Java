package cn.aishu.exporter.ar_trace;


import cn.aishu.exporter.ar_trace.content.SpanContent;
import cn.aishu.exporter.common.output.Retry;
import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.Stdout;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.*;


public final class ArExporter implements SpanExporter {
    private Sender sender;


    public ArExporter() {
        sender = new Stdout();
    }

    public ArExporter(Sender sender) {
        this.sender = sender;
    }

    public static ArExporterBuilder builder(){
        return new ArExporterBuilder();
    }


    public CompletableResultCode export(Collection<SpanData> spans) {
        for (SpanData spanData : spans) {
            SpanContent spanContent = new SpanContent(spanData);
            sender.send(spanContent);
        }

        return CompletableResultCode.ofSuccess();
    }


    public CompletableResultCode flush() {
        CompletableResultCode resultCode = new CompletableResultCode();

        return resultCode.succeed();
    }

    public CompletableResultCode shutdown() {
        sender.shutDown();
        return this.flush();
    }
}


