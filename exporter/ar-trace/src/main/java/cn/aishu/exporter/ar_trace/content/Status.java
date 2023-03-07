package cn.aishu.exporter.ar_trace.content;

import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.StatusData;

public class Status {
    @SerializedName("Code")
    private String Code;

    @SerializedName("Description")
    private String Description;

    public Status(StatusData status) {
        Code = status.getStatusCode().toString();
        Description = status.getDescription();
    }

}
