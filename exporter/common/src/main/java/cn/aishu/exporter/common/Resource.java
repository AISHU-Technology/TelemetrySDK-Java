package cn.aishu.exporter.common;

import io.opentelemetry.api.common.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Resource {
    private Resource() {
    }

    public static List<KeyValue> getResource(Attributes attributes) {
        Map<String, String> map = Host.getMap();

        Map<String, String> osMap = Os.getMap();

        Map<String, String> serviceMap = Service.getMap();

        Map<String, String> telemetryMap = Telemetry.getMap();
        map.putAll(osMap);
        map.putAll(serviceMap);
        map.putAll(telemetryMap);

        attributes.forEach((k, v) -> map.put(k.getKey(), v.toString()));

        List<KeyValue> resource = new ArrayList<>();
        map.forEach((k, v) -> resource.add(KeyValue.createWithStringType(k, v)));
        return resource;
    }

}
