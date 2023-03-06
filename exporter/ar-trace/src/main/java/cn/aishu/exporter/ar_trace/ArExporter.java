package cn.aishu.exporter.ar_trace;


import cn.aishu.exporter.common.output.SenderGen;
import cn.aishu.exporter.ar_trace.content.SpanContent;
import cn.aishu.exporter.common.output.Retry;
import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.utils.TimeUtil;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.*;


public final class ArExporter implements SpanExporter {
    private Sender sender;

    public static ArExporter create(String addr) {
        Retry retry = new Retry();
        return new ArExporter(addr, retry, true);
    }

    public static ArExporter create() {
        return new ArExporter();
    }

    public ArExporter() {
        sender = SenderGen.getSender();
    }

    public ArExporter(String addr, Retry retry, boolean isGzip) {
        sender = SenderGen.getSender(addr, retry, isGzip);
    }

    public static ArExporterBuilder builder(){
        return new ArExporterBuilder();
    }

    public static ArExporterBuilder builder(Retry retry){
        return new ArExporterBuilder(retry);
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


