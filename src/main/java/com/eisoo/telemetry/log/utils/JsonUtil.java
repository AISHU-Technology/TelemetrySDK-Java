package com.eisoo.telemetry.log.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonUtil {
    private JsonUtil() {}

    //序列化成json格式
    public static String toJson(Object object){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
}
