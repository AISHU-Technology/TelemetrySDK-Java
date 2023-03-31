package cn.aishu.telemetry.log;


import cn.aishu.telemetry.log.config.SamplerLogConfig;


public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();


    public static Dispatcher getInstance() {
        return INSTANCE;
    }


    public void dispatchEvent(LogContent logContent) {
        SamplerLogConfig.getSender().send(logContent);
    }
}
