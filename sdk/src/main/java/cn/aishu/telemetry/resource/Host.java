package cn.aishu.telemetry.resource;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

public class Host {
    private final String arch = System.getProperty("os.arch");

    private String ip = "UnknownIP";

    private String name = "UnknownHost";

    private static final Host HOST = new Host();

    public static Host getHost() {
        return HOST;
    }

    public Host() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.name = inetAddress.getHostName();
            this.ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            // 暂时吃掉该异常
        }
    }

    public static Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ResourceAttributes.HOST_ARCH.getKey(), Host.HOST.arch);
        map.put("host.ip", Host.HOST.ip);
        map.put(ResourceAttributes.HOST_NAME.getKey(), Host.HOST.name);
        return map;
    }
}
