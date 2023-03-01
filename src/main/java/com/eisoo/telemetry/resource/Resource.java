package com.eisoo.telemetry.resource;

import com.eisoo.telemetry.common.KeyValue;
import com.eisoo.telemetry.common.TypeValue;
import io.opentelemetry.api.common.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Resource {
    private static List<KeyValue> keyValues = genDefault();

    public Resource(io.opentelemetry.sdk.resources.Resource ir) {
        KeyValue.extractFromAttributes(ir.getAttributes());
    }

    private static List<KeyValue> genDefault() {
        List<KeyValue> resource = new ArrayList<>();

        Map<String, String> map = Host.getMap();
        map.forEach((k, v) -> {
            resource.add(new KeyValue(k, v));
        });

        Map<String, String> osMap = Os.getMap();
        osMap.forEach((k, v) -> {
            resource.add(new KeyValue(k, v));
        });

        Map<String, String> serviceMap = Service.getMap();
        serviceMap.forEach((k, v) -> {
            resource.add(new KeyValue(k, v));
        });

        Map<String, String> TelemetryMap = Telemetry.getMap();
        TelemetryMap.forEach((k, v) -> {
            resource.add(new KeyValue(k, v));
        });

        return resource;
    }

    public static List<KeyValue> getResource(Attributes attributes) {
        Map<String, String> map = Host.getMap();

        Map<String, String> osMap = Os.getMap();

        Map<String, String> serviceMap = Service.getMap();

        Map<String, String> TelemetryMap = Telemetry.getMap();
        map.putAll(osMap);
        map.putAll(serviceMap);
        map.putAll(TelemetryMap);

        attributes.forEach((k, v) -> {
            map.put(k.getKey(), v.toString());

        });
        List<KeyValue> resource = new ArrayList<>();
        map.forEach((k, v) -> {
            resource.add(new KeyValue(k, v));
        });
        // }
        return resource;
    }

    public static void setResourceList(List<KeyValue> resourceList) {
        keyValues = resourceList;
    }
}
