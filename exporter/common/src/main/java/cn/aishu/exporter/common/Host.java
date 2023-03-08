package cn.aishu.exporter.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Host {
    private final String arch = System.getProperty("os.arch");

    private String ip = "UnknownIP";

    private String name = "UnknownHost";

    private static final Host HOST = new Host();


    public Host() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.name = inetAddress.getHostName();
            this.ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            Log logger = LogFactory.getLog(getClass());
            logger.error(e.getMessage(), e);
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
