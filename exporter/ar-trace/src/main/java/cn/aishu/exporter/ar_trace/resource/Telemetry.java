//package cn.aishu.exporter.ar_trace.resource;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Telemetry {
//
//    private final SDK sdk = new SDK();
//
//    private static final Telemetry TELEMETRY = new Telemetry();
//
//    public static final Telemetry getTelemetry(){
//        return TELEMETRY;
//    }
//
//    class SDK {
//
//        private  String language = "java";
//
//        private  String name = "TelemetrySDK-Java/span";
//
//        private  String version = "v2.5.0";
//    }
//
//    public static Map<String, String> getMap(){
//        HashMap<String, String> map = new HashMap<>();
//        map.put("telemetry.sdk.language", Telemetry.TELEMETRY.sdk.language);
//        map.put("telemetry.sdk.name", Telemetry.TELEMETRY.sdk.name);
//        map.put("telemetry.sdk.version", Telemetry.TELEMETRY.sdk.version);
//        return map;
//    }
//
//}
