package cn.aishu.exporter.common.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonUtil {
    private JsonUtil() {}
    private static final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
    private static final Gson gson = gsonBuilder.create();

    private static final GsonBuilder gsonBuilderSimple = new GsonBuilder();
    private static final Gson gsonSimple = gsonBuilderSimple.create();

    //序列化成首字母大写json格式
    public static String toJson(Object object){
        return gson.toJson(object);
    }

    //序列化成简单的json格式
    public static String toJsonSimple(Object object){
        return gsonSimple.toJson(object);
    }
}