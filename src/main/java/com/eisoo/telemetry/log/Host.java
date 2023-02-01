package com.eisoo.telemetry.log;


import java.net.InetAddress;
import java.net.UnknownHostException;

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
            //暂时吃掉该异常
        }
    }
}
