package cn.aishu.exporter.ar_trace.content;

import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.StatusData;

public class Status {
    @SerializedName("Code")
    private String code;

    @SerializedName("Description")
    private String description;

    public Status(StatusData status) {
        this.code = status.getStatusCode().toString();
        this.description = status.getDescription();
    }

}
