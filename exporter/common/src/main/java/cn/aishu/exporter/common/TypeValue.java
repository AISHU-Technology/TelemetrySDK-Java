package cn.aishu.exporter.common;

import com.google.gson.annotations.SerializedName;

public class TypeValue {
    @SerializedName("Type")
    private  String type;

    @SerializedName("Value")
    private  String value;

    public TypeValue() {
    }

    public TypeValue(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
