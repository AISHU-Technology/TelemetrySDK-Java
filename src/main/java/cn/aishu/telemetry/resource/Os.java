package cn.aishu.telemetry.resource;

import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class Os {

    private String description = System.getProperty("os.name");

    private String type = "linux";

    private String version = System.getProperty("os.version");

    private static final Os OS = new Os();

    public static Os getOs() {
        return OS;
    }

    public Os() {
        String dl = description.toLowerCase();
        if (dl.contains("mac")) {
            type = "darwin";
        } else if (dl.contains("windows")) {
            type = "windows";
        }
    }

    public static Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceAttributes.OS_DESCRIPTION.getKey(), Os.OS.description);
        map.put(ResourceAttributes.OS_TYPE.getKey(), Os.OS.type);
        map.put(ResourceAttributes.OS_VERSION.getKey(), Os.OS.version);
        return map;
    }
}
