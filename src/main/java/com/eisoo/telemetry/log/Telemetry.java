package com.eisoo.telemetry.log;


public class Telemetry {

    private SDK sdk = new SDK();

    public Telemetry() {

    }

    class SDK {

        private  String language = "java";

        private  String name = "TelemetrySDK-Java/span";

        private  String version = "2.2.0";

    }

}
