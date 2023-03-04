package cn.aishu.telemetry.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private JsonUtil() {
    }

    private static final GsonBuilder gsonBuilder = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
    private static final Gson gson = gsonBuilder.create();

    // 序列化成json格式
    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
