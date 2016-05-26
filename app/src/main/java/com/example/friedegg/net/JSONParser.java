package com.example.friedegg.net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by CRZ on 2016/5/26 11:17.
 * 使用gson解析json
 */
public class JSONParser {

    protected static Gson gson = new Gson();
    public static String toString(Object o) {
        return gson.toJson(o);
    }

    /**
     * @param type 类型反射(Class<?>)或反射令牌(TypeToken)
     * @return Object
     * @throws RuntimeException
     * @Description: 将标准JSON字符串反序列化为对象
     *
     */
    public static Object toObject(String jsonString, Object type) {
        jsonString = jsonString.replace("&nbsp", "");
        jsonString = jsonString.replace("﹠nbsp", "");
        jsonString = jsonString.replace("nbsp", "");
        jsonString = jsonString.replace("&amp;", "");
        jsonString = jsonString.replace("&amp", "");
        jsonString = jsonString.replace("amp", "");
        if (type instanceof Type) {
            try {
                return gson.fromJson(jsonString, (Type) type);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else if (type instanceof Class<?>) {
            try {
                return gson.fromJson(jsonString, (Class<?>) type);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new RuntimeException("只能是Class<?>或者通过TypeToken获取的Type类型");
        }
    }

}
