package cn.aishu.exporter.common;

public class TypeValue {
    private String type;

    private Object value;

    public String getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public TypeValue() {
    }

    public TypeValue(Object value) {
        this.value = value;
        type = "STRING";
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
