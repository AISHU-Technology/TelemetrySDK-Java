package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.ar_trace.common.KeyValue;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;

import java.util.List;

public class InstrumentationScope {

    private String name;

    private List<KeyValue> attributes;

    private String version;

    private String schemaUrl;

    public InstrumentationScope(InstrumentationScopeInfo info) {
        this.name = info.getName();
        this.attributes = KeyValue.extractFromAttributes(info.getAttributes());
        this.version = info.getVersion();
        this.schemaUrl = info.getSchemaUrl();
    }
}
