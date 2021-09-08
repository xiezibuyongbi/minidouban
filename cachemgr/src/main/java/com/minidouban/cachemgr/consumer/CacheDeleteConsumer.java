package com.minidouban.cachemgr.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.minidouban.cachemgr.util.JedisUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CacheDeleteConsumer {
    private static final String TOPIC = "cache_delete";
    private static final String OPERATION = "DELETE";
    private static final String tableNamePattern =
            "(UserInfo|ReadingList|ReadingListBook)";
    @Resource
    private JedisUtils jedisUtils;

    @KafkaListener(topics = {TOPIC})
    public void consume(List<String> data) {
        for (String message : data) {
            JSONObject jsonObject;
            try {
                jsonObject = JSON.parseObject(message);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            if (!OPERATION.equals(jsonObject.getString("type"))) {
                continue;
            }
            String tableName = jsonObject.getString("table");
            if (!Pattern.matches(tableNamePattern, tableName)) {
                continue;
            }
        }
    }
}
