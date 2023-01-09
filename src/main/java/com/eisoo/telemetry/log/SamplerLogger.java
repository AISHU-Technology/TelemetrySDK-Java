package com.eisoo.telemetry.log;


import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.constant.KeyConstant;
import io.opentelemetry.api.trace.SpanContext;

import java.util.HashMap;
import java.util.Map;

public class SamplerLogger implements com.eisoo.telemetry.log.Logger {

    static final Dispatcher dispatcher = Dispatcher.getInstance();

    private String name;

    private void common(Level l, Object... objects) {
        //日志等级低于配置，直接返回
        if (l.toInt() < SamplerLogConfig.getLevel().toInt()) {
            return;
        }

        LogContent log = new LogContent();

        //日志等级
        log.setSeverityText(l.toString());

        //消息体
        HashMap<String, Object> bodyMap = new HashMap<>();


        for (Object o : objects) {
            //消息体只有字符串时才可以有Message属性
            if (o instanceof String && bodyMap.isEmpty()) {
                bodyMap.put(KeyConstant.MESSAGE.toString(), o);
                //消息体是一个Body类实例
            } else if (o instanceof Body) {
                Body body = (Body) o;
                bodyMap.put(KeyConstant.TYPE.toString(), body.getType());
                bodyMap.put(body.getType(), body.getField());
                //传入的是Attributes类实例
            } else if(o instanceof Attributes){
                Attributes attributes = (Attributes) o;
                log.setAttributes(attributes.getAttributes());
            } else if(o instanceof Link){
                log.setLink((Link) o);
            }else if(o instanceof Service){
                Service service = (Service) o;
                Map<String, Object> resource = log.getResource();
                resource.put("service", service);
                log.setResource(resource);
            }else if (o instanceof SpanContext) {
                final SpanContext spanContext = (SpanContext) o;
                log.setLink(new Link(spanContext.getTraceId(), spanContext.getSpanId()));
            }
        }

        log.setBody(bodyMap);

        dispatcher.dispatchEvent(log);
    }

    public void trace(Object... o) {
        common(Level.TRACE, o);
    }

    public void debug(Object... o) {
        common(Level.DEBUG, o);
    }

    public void info(Object... o) {
        common(Level.INFO, o);
    }

    public void warn(Object... o) {
        common(Level.WARN, o);
    }

    public void error(Object... o) {
        common(Level.ERROR, o);
    }

    public void fatal(Object... o) {
        common(Level.FATAL, o);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
