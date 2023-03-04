package cn.aishu.telemetry.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class Service {
    private Instance instance = new Instance();

    private String name = "UnknownServiceName";

    private String version = "UnknownServiceVersion";

    private static String DEFAULT_CONFIG_FILE = ("traceExporter.properties");

    private static Service defaultService = new Service();

    public static Service getService() {
        return defaultService;
    }

    public Service() {
        this(DEFAULT_CONFIG_FILE);
    }

    public Service(String filename) {
        Properties properties = new Properties();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (resourceAsStream != null) {
            try {
                properties.load(resourceAsStream);
                String serviceInstanceId = properties.getProperty(ResourceAttributes.SERVICE_INSTANCE_ID.getKey());
                if (serviceInstanceId != null) {
                    instance.setId(serviceInstanceId);
                }
                String serviceName = properties.getProperty(ResourceAttributes.SERVICE_NAME.getKey());
                if (serviceName != null) {
                    setName(serviceName);
                }
                String serviceVersion = properties.getProperty(ResourceAttributes.SERVICE_VERSION.getKey());
                if (serviceVersion != null) {
                    setVersion(serviceVersion);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

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
        map.put(ResourceAttributes.SERVICE_NAME.getKey(), Service.defaultService.name);
        map.put(ResourceAttributes.SERVICE_VERSION.getKey(), Service.defaultService.version);
        map.put(ResourceAttributes.SERVICE_INSTANCE_ID.getKey(), Service.defaultService.instance.id);

        return map;
    }
}
