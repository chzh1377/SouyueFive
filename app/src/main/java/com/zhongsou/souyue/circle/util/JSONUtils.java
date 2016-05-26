package com.zhongsou.souyue.circle.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.lang.reflect.Type;

/**
 * Created by wangqiang on 15/8/4.
 * json解析工具
 */
public class JSONUtils {

    public static <T> T fromJson(String json, Class<T> classOfT){
        return new Gson().fromJson(json,classOfT);
    }

    public static <T> T fromJsonArray(JsonArray jsonArray,Type type){
        return new Gson().fromJson(jsonArray,type);
    }

}
