package cn.aishu.exporter.common;

import com.google.gson.annotations.SerializedName;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

import java.util.ArrayList;
import java.util.List;

public class KeyValue {
    @SerializedName("Key")
    private String key;

    @SerializedName("Value")
    private TypeValue value;

    public KeyValue() {
    }

    public KeyValue(String key, String type, String value) {
        this.key = key;
        this.value = new TypeValue(type, value);
    }

    public static KeyValue createWithStringType(String key, String value) {
        return new KeyValue(key, "STRING", value);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(TypeValue value) {
        this.value = value;
    }

    // 从Attributes获取keyValue
    public static List<KeyValue> extractFromAttributes(Attributes attributes) {
        List<KeyValue> keyValues = new ArrayList<>();
        attributes.forEach((k, v) -> {
            KeyValue kv = new KeyValue();
            kv.setKey(k.getKey());
            TypeValue tv = new TypeValue();
            tv.setType(changeTypeName(k));
            tv.setValue(v);
            kv.setValue(tv);
            keyValues.add(kv);
        });
        return keyValues;
    }

    // 为了适配AR接收器的统一命名
    private static String changeTypeName(AttributeKey<?> k) {
        switch (k.getType()) {
            case STRING:
                return "STRING";
            case BOOLEAN:
                return "BOOL";
            case LONG:
                return "INT";
            case DOUBLE:
                return "FLOAT";
            case STRING_ARRAY:
                return "STRINGARRAY";
            case BOOLEAN_ARRAY:
                return "BOOLEANARRAY";
            case LONG_ARRAY:
                return "INTARRAY";
            case DOUBLE_ARRAY:
                return "FLOATARRAY";
            default:
                return "Unrecognized attribute type";
        }
    }

}