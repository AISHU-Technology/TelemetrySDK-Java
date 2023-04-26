package cn.aishu.telemetry.log;


import java.util.Map;

public class Attributes {
    private Map<String, Object> attributeMap;

    public Attributes() {
    }

    public Attributes(Map<String, Object> attr) {
        this.attributeMap = attr;
    }

    public void setAttributes(Map<String, Object> attr) {
        this.attributeMap = attr;
    }

    public Map<String, Object> getAttributes() {
        return attributeMap;
    }
}
