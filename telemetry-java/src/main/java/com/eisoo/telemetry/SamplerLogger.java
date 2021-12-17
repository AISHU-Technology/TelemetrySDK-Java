package com.eisoo.telemetry;

import com.eisoo.telemetry.constant.KeyConstant;
import io.opentelemetry.api.trace.Span;
import com.eisoo.telemetry.utils.JsonUtil;

import java.util.HashMap;



public class SamplerLogger {


    private Level level = Level.INFO;

    private String result;

    public Level getLevel() {
        return level;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setLevel(Level l) {
        level = l;
    }


    private void common(Level l, Object... objects) {
        //日志等级低于配置，直接返回
        if (l.toInt() < level.toInt()) {
            return;
        }

        final LogSpan logSpan = new LogSpan();

        //日志等级
        logSpan.setSeverityText(l.toString());

        //消息体
        final HashMap<String, Object> bodyMap = new HashMap<>();


        for (Object o : objects) {
            //消息体只有字符串时才可以有Message
            if (o instanceof String && bodyMap.isEmpty()) {
                bodyMap.put(KeyConstant.MESSAGE.toString(), o);
                //消息体是一个Body类实例
            } else if (o instanceof Body) {
                final Body body = (Body) o;
                bodyMap.put(KeyConstant.TYPE.toString(), body.getType());
                bodyMap.put(body.getType(), body.getField());
                //传入的是Attributes类实例
            } else if (o instanceof Attributes) {
                final Attributes attributes = (Attributes) o;
                final HashMap<String, Object> attrMap = new HashMap<>();
                attrMap.put(KeyConstant.TYPE.toString(), attributes.getType());
                attrMap.put(attributes.getType(), attributes.getField());
                logSpan.setAttributes(attrMap);
                //传入的是span类实例，把原traceId和spanId修改为span的traceId和spanId
            } else if (o instanceof Span) {
                final Span span = (Span) o;
                logSpan.setTraceId(span.getSpanContext().getTraceId());
                logSpan.setSpanId(span.getSpanContext().getSpanId());
            }
        }

        logSpan.setBody(bodyMap);
        setResult(JsonUtil.toJson(logSpan));
        System.out.println(getResult());
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

}
