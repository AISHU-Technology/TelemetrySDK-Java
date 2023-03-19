package cn.aishu.telemetry.log;


import java.util.Map;

public class Attributes {
    private Map<String, Object> attributes;

    public Attributes() {
    }

    public Attributes(Map<String, Object> attr) {
        this.attributes = attr;
    }

    public void setAttributes(Map<String, Object> attr) {
        this.attributes = attr;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
