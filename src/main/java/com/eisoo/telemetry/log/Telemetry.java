package com.eisoo.telemetry.log;


public class Telemetry {

    private final SDK sdk = new SDK();

    private static final Telemetry TELEMETRY = new Telemetry();

    public static final Telemetry getTelemetry(){
        return TELEMETRY;
    }

    class SDK {

        private  String language = "java";

        private  String name = "TelemetrySDK-Java/span";

        private  String version = "2.2.3";

    }

}
