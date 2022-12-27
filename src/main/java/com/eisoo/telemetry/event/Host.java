package com.eisoo.telemetry.event;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {
    private  String arch = System.getProperty("os.arch");

    private  String ip = "UnknownIP";

    private  String name = "UnknownHost";

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
