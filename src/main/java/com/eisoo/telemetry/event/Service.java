package com.eisoo.telemetry.event;


public class Service {

    private  String instance = "UnknownServiceInstance";

    private  String name = "UnknownServiceName";

    private  String version = "UnknownServiceVersion";

    public Service() {
    }

    public Service(String instance, String name, String version) {
        this.instance = instance;
        this.name = name;
        this.version = version;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
