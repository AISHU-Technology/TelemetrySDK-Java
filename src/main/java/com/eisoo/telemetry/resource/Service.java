package com.eisoo.telemetry.resource;

import com.eisoo.telemetry.constant.KeyConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Service {
    private Instance instance = new Instance();

    private String name = "UnknownServiceName";

    private String version = "UnknownServiceVersion";

    private static Service SERVICE = new Service();

    public static Service getService() {
        return SERVICE;
    }

    public Service() {
        this(KeyConstant.CONFIG_FILE.toString());
    }

    public Service(String filename) {
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (resourceAsStream != null) {
            try {
                properties.load(resourceAsStream);
                String serviceInstanceId = properties.getProperty("service.instance.id");
                if (serviceInstanceId != null) {
                    instance.setId(serviceInstanceId);
                }
                String serviceName = properties.getProperty("service.name");
                if (serviceName != null) {
                    setName(serviceName);
                }
                String serviceVersion = properties.getProperty("service.version");
                if (serviceVersion != null) {
                    setVersion(serviceVersion);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Instance {
        String id = "UnknownServiceInstance";

        public void setId(String id) {
            this.id = id;
        }
    }

    public void setInstanceId(String instanceId) {
        this.instance.setId(instanceId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("service.name", Service.SERVICE.name);
        map.put("service.version", Service.SERVICE.version);
        map.put("service.instance.id", Service.SERVICE.instance.id);

        return map;
    }
}
