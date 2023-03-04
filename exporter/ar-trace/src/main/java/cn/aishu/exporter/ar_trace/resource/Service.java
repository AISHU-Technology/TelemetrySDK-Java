package cn.aishu.exporter.ar_trace.resource;


import java.util.HashMap;
import java.util.Map;

public class Service {
    private  Instance instance = new Instance();

    private  String name = "UnknownServiceName";

    private  String version = "UnknownServiceVersion";

    private static Service SERVICE = new Service();


    class Instance{
        String id = "UnknownServiceInstance";
    }

    public static Map<String, String> getMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("service.name", Service.SERVICE.name);
        map.put("service.version", Service.SERVICE.version);
        map.put("service.instance.id", Service.SERVICE.instance.id);

        return map;
    }
}
