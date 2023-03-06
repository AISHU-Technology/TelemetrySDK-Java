package cn.aishu.exporter.common;

public class TypeValue {
    private  String Type;

    private  String Value;

    public TypeValue() {
    }

    public TypeValue(String type, String value) {
        Type = type;
        Value = value;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setValue(String value) {
        Value = value;
    }
}
