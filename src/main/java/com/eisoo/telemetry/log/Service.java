package com.eisoo.telemetry.log;


import com.eisoo.telemetry.log.constant.KeyConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Service {
    private  Instance instance = new Instance();

    private  String name = "UnknownServiceName";

    private  String version = "UnknownServiceVersion";

    public Service() {
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(KeyConstant.CONFIGFILE.toString());
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

    class Instance{
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
}
