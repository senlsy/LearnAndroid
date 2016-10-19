package com.quickartifact.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Description: JSON解析的工具类
 * <p/>
 * 目前采用fast-json
 * 更多用法 #Link{https://github.com/Alibaba/fastjson/wiki/%E9%A6%96%E9%A1%B5}
 *
 * @author mark.lin
 * @date 16/3/8 下午12:28
 */
public final class JsonUtils {

    //    FastJson常用的API:
    //    public static final Object parse(String text); // 把JSON文本parse为JSONObject或者JSONArray
    //    public static final JSONObject parseObject(String text)； // 把JSON文本parse成JSONObject
    //    public static final <T> T parseObject(String text, Class<T> clazz); // 把JSON文本parse为JavaBean
    //    public static final JSONArray parseArray(String text); // 把JSON文本parse成JSONArray
    //    public static final <T> List<T> parseArray(String text, Class<T> clazz); //把JSON文本parse成JavaBean集合
    //    public static final String toJSONString(Object object); // 将JavaBean序列化为JSON文本
    //    public static final String toJSONString(Object object, boolean prettyFormat); // 将JavaBean序列化为带格式的JSON文本
    //    public static final Object toJSON(Object javaObject); 将JavaBean转换为JSONObject或者JSONArray。


    private JsonUtils() {
    }

    /**
     * 根据class将json字符串转换成对应的对象
     * 要转换的对象可以用public字段，也可以用getter setter
     * 抽象类 无法 转换
     *
     * @param jsonStr json str
     * @param clazz   解析对象的class
     * @param <T>     具体类型
     * @return 解析的对象
     */
    public static <T> T read(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 将json string转换成 ArrayList<Model>
     *
     * @param jsonStr json string
     * @param clazz   Model的class
     */
    public static <T> List<T> readList(String jsonStr, Class<T> clazz) {
        return JSON.parseArray(jsonStr, clazz);
    }


    /**
     * 将对象转换成json string
     *
     * @param obj 要转换成json string 的 对象，
     */
    public static String write(Object obj) {
        return JSON.toJSONString(obj);
    }

    //====================================
    // 普通的JSON 解析
    //====================================

    /**
     * 解析int
     *
     * @param jsonObject 父JSON Oject
     * @param key        节点key
     * @return String value
     */
    public static int getJSONInt(JSONObject jsonObject, String key) {
        return jsonObject.getIntValue(key);
    }

    /**
     * 解析String
     *
     * @param jsonObject 父JSON Oject
     * @param key        节点key
     * @return String value
     */
    public static String getJSONString(JSONObject jsonObject, String key) {
        return jsonObject.getString(key);
    }


    /**
     * 获取JSONObject
     *
     * @param jsonStr json 字符串
     * @return JSONObject 可能为null
     */
    public static JSONObject getJSONObject(String jsonStr) {
        return JSON.parseObject(jsonStr);
    }

    /**
     * 解析JSON中的一个Object节点
     *
     * @param jsonObject 父JSON Object
     * @param key        节点名称
     * @return 节点对象
     */
    public static Object getObject(JSONObject jsonObject, String key) {
        return jsonObject.getJSONObject(key);
    }
}
