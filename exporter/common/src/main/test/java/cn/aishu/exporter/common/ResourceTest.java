package cn.aishu.exporter.common;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributeType;
import io.opentelemetry.api.internal.InternalAttributeKeyImpl;
import io.opentelemetry.sdk.internal.AttributesMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class ResourceTest {

    @Test
    public void getResource() {
        AttributesMap attributesMap =AttributesMap.create(10, 10);
        List<KeyValue> resource = Resource.getResource(attributesMap);

        Assert.assertFalse(resource.isEmpty());
    }

    @Test
    public void extractFromAttributes() {
        AttributesMap attributesMap =AttributesMap.create(10, 10);
        AttributeKey<Object> key = InternalAttributeKeyImpl.create("STRING", AttributeType.STRING);
        attributesMap.put(key,"str");
        List<KeyValue> keyValues = KeyValue.extractFromAttributes(attributesMap);
//        System.out.println(keyValues.size());
//        System.out.println(keyValues.get(0));
//        System.out.println(attributesMap);

        AttributeKey<Object> keyBoolean = InternalAttributeKeyImpl.create("BOOLEAN", AttributeType.BOOLEAN);
        attributesMap.put(keyBoolean, true);

        AttributeKey<Object> keyLong = InternalAttributeKeyImpl.create("LONG", AttributeType.LONG);
        attributesMap.put(keyLong, 123l);

        AttributeKey<Object> keyDouble = InternalAttributeKeyImpl.create("DOUBLE", AttributeType.DOUBLE);
        attributesMap.put(keyDouble, 12.3);

        AttributeKey<Object> keyStringArray = InternalAttributeKeyImpl.create("STRING_ARRAY", AttributeType.STRING_ARRAY);
        String[] strs = {"ac","cc"};
        attributesMap.put(keyStringArray, strs);

        AttributeKey<Object> keyBooleanArray = InternalAttributeKeyImpl.create("BOOLEAN_ARRAY", AttributeType.BOOLEAN_ARRAY);
        Boolean[] bools = {true, false};
        attributesMap.put(keyBooleanArray,bools);

        AttributeKey<Object> keyLongArray = InternalAttributeKeyImpl.create("LONG_ARRAY", AttributeType.LONG_ARRAY);
        long[] longs = {12l, 13l};
        attributesMap.put(keyLongArray,longs);

        AttributeKey<Object> keyDoubleArray = InternalAttributeKeyImpl.create("DOUBLE_ARRAY", AttributeType.DOUBLE_ARRAY);
        Double[] doubles = {12.3, 22.3};
        attributesMap.put(keyDoubleArray, doubles);

        List<KeyValue> keyValues2 = KeyValue.extractFromAttributes(attributesMap);
        System.out.println(keyValues2.size());

//        System.out.println(attributesMap);

    }
}