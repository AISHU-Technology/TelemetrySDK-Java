package cn.aishu.exporter.ar_trace.content;

import com.google.gson.annotations.SerializedName;
import io.opentelemetry.sdk.trace.data.StatusData;

public class Status {
    @SerializedName("Code")
    private int code;

    @SerializedName("Description")
    private String description;

    public Status(StatusData status) {
        if (status == null){
            return;
        }
        //code的值与go版本的保持一致
        switch (status.getStatusCode()) {
            case UNSET:
                this.code = 0;
                break;
            case ERROR:
                this.code = 1;
                break;
            case OK:
                this.code = 2;
                break;
        }
        this.description = status.getDescription();
    }
}
