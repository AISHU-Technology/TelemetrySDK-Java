package cn.aishu.exporter.ar_trace.content;

import cn.aishu.exporter.common.KeyValue;
import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;

import java.util.List;

public class InstrumentationScope {
    @SerializedName("Name")
    private String name;

    @SerializedName("Attributes")
    private List<KeyValue> attributes;

    @SerializedName("Version")
    private String version;

    @SerializedName("SchemaUrl")
    private String schemaUrl;

    public InstrumentationScope(InstrumentationScopeInfo info) {
        this.name = info.getName();
        this.attributes = KeyValue.extractFromAttributes(info.getAttributes());
        this.version = info.getVersion();
        this.schemaUrl = info.getSchemaUrl();
    }
}
