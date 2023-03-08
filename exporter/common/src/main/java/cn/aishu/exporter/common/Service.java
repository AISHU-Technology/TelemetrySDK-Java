package cn.aishu.exporter.common;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import java.util.HashMap;
import java.util.Map;

public class Service {
    private  Instance instance = new Instance();

    private  String name = "UnknownServiceName";

    private  String version = "UnknownServiceVersion";

    private static final Service SERVICE = new Service();


    class Instance{
        String id = "UnknownServiceInstance";
    }

    public static Map<String, String> getMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceAttributes.SERVICE_NAME.getKey(), Service.SERVICE.name);
        map.put(ResourceAttributes.SERVICE_VERSION.getKey(), Service.SERVICE.version);
        map.put(ResourceAttributes.SERVICE_INSTANCE_ID.getKey(), Service.SERVICE.instance.id);

        return map;
    }
}
