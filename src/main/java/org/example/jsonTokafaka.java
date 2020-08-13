package org.example;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public interface jsonTokafaka {
    public static final String url = "http://113.106.111.75:5040/demo/kafka/produce";

    public JSONObject strTojson(String[] str);

    public String postToKafaka(String httpUrl, JSONObject jsonObject);
}
