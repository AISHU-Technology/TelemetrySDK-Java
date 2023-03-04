package cn.aishu.telemetry.ar_metric;

import com.google.gson.annotations.SerializedName;

public class AnyrobotScope {
    @SerializedName("Name")
    String name;
    @SerializedName("Version")
    String version;
    @SerializedName("schemaURL")
    String schemaURL;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setSchemaURL(String schemaURL) {
        this.schemaURL = schemaURL;
    }

    public String getSchemaURL() {
        return this.schemaURL;
    }
}
