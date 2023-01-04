package com.eisoo.telemetry.event;


import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.constant.KeyConstant;

import java.util.Map;

public class SamplerEvent implements Event {

    static final Dispatcher dispatcher = Dispatcher.getInstance();

    private String name;

    private void common(Level l, Object... objects) {
        //日志等级低于配置，直接返回
        if (l.toInt() < EventConfig.getLevel().toInt()) {
            return;
        }

        EventContent eventContent = new EventContent();

        //日志等级
        eventContent.setLevel(l.toString());

        //消息体
        for (Object o : objects) {
            if(o instanceof Subject){
                Subject s = (Subject) o;
                eventContent.setSubject(s.getValue());
            }else if(o instanceof EventType){
                EventType e = (EventType) o;
                eventContent.setEventType(e.getValue());
            }else if(o instanceof Service){
                Service s = (Service) o;
                Map<String, Object> resource = eventContent.getResource();
                resource.put(KeyConstant.SERVICE.toString(), s);
                eventContent.setResource(resource);
            }else if(o instanceof Link){
                Link link = (Link) o;
                eventContent.setLink(link);
            }else if(o instanceof Attributes){
                Attributes attributes = (Attributes) o;
                eventContent.setAttributes(attributes.getAttributes());
            } else {
                eventContent.setData(o);
            }
        }

        dispatcher.dispatchEvent(eventContent);
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
