package cn.aishu.telemetry.resource;

import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class Telemetry {

    private final SDK sdk = new SDK();

    private static final Telemetry TELEMETRY = new Telemetry();

    public static final Telemetry getTelemetry() {
        return TELEMETRY;
    }

    class SDK {

        private String language = "java";

        private String name = "TelemetrySDK-Java/span";

        private String version = "v2.5.0";
    }

    public static Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceAttributes.TELEMETRY_SDK_LANGUAGE.getKey(), Telemetry.TELEMETRY.sdk.language);
        map.put(ResourceAttributes.TELEMETRY_SDK_NAME.getKey(), Telemetry.TELEMETRY.sdk.name);
        map.put(ResourceAttributes.TELEMETRY_AUTO_VERSION.getKey(), Telemetry.TELEMETRY.sdk.version);
        return map;
    }

}
