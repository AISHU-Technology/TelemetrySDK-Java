package cn.aishu.exporter.ar_trace;


import cn.aishu.exporter.ar_trace.content.SpanContent;
import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.StdSender;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.*;


public final class ArExporter implements SpanExporter {
    private Sender sender = new StdSender();

    public ArExporter() {
    }

    public static ArExporter create() {
        return new ArExporter();
    }

    public static ArExporter create(Sender sender) {
        return new ArExporter(sender);
    }

    public ArExporter(Sender sender) {
        if (sender == null){
            return;
        }
        this.sender = sender;
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


