package com.minidouban.producer;

import com.alibaba.fastjson.JSON;
import com.minidouban.pojo.MQDelCacheMsg;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

public class CacheDelMsgProducer {
    private static final String TOPIC = "cache_delete";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendDeleteMsg(MQDelCacheMsg mqDelCacheMsg) {
        kafkaTemplate.send(TOPIC, JSON.toJSONString(mqDelCacheMsg)).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(SendResult<String, String> stringStringSendResult) {
            }
        });
    }
}
