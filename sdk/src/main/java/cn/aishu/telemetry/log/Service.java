package cn.aishu.telemetry.log;


import cn.aishu.telemetry.log.constant.KeyConstant;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Service {
    private  Instance instance = new Instance();

    @SerializedName("name")
    private  String name = "UnknownServiceName";

    @SerializedName("version")
    private  String version = "UnknownServiceVersion";

    private static final Service SERVICE = new Service();

    public static Service getService(){
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
                Logger.getGlobal().warning("get service info error: " + e.getMessage());
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
