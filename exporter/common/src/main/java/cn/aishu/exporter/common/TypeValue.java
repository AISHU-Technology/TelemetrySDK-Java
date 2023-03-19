package cn.aishu.exporter.common;

import com.google.gson.annotations.SerializedName;

public class TypeValue {
    @SerializedName("Type")
    private String type;

    @SerializedName("Value")
    private Object value;

    public TypeValue() {
    }

    public TypeValue(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
