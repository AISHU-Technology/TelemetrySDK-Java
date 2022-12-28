package com.eisoo.telemetry.event;


public class Telemetry {

    private SDK sdk = new SDK();

    public Telemetry() {

    }

    class SDK {

        private  String language = "java";

        private  String name = "TelemetrySDK-Java/exporters/arevent";

        private  String version = "v2.4.0";

    }

}
