package com.eisoo.telemetry.common;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;

import io.opentelemetry.api.common.Attributes;

public class KeyValue {

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = new TypeValue(value);
    }

    public KeyValue() {
    }

    private String key;

    private TypeValue value;

    public String getKey() {
        return this.key;
    }

    public TypeValue getValue() {
        return this.value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(TypeValue value) {
        this.value = value;
    }

    public static List<KeyValue> extractFromAttributes(Attributes attributes, Log log) {
        List<KeyValue> keyValues = new ArrayList<>();
        attributes.forEach((k, v) -> {
            KeyValue kv = new KeyValue();
            kv.setKey(k.getKey());
            TypeValue tv = new TypeValue();
            switch (k.getType()) {
                case BOOLEAN:
                    tv.setType("BOOL");
                    break;
                case LONG:
                    tv.setType("INT");
                    break;
                case STRING:
                    tv.setType("STRING");
                    break;
                case DOUBLE:
                    tv.setType("FLOAT");
                    break;
                case BOOLEAN_ARRAY:
                    tv.setType("BOOLEANARRAY");
                    break;
                case LONG_ARRAY:
                    tv.setType("INTARRAY");
                    break;
                case DOUBLE_ARRAY:
                    tv.setType("FLOATARRAY");
                    break;
                case STRING_ARRAY:
                    tv.setType("STRINGARRAY");
                    break;
                default:
                    log.error("Unrecognized attribute type");
            }
            tv.setValue(v);
            kv.setValue(tv);
            keyValues.add(kv);
        });
        return keyValues;
    }

}
