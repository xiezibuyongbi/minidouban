package com.minidouban.cachemgr.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.minidouban.cachemgr.exception.UnrecognizedSqlTypeException;
import com.minidouban.cachemgr.exception.UnrecognizedTableException;
import com.minidouban.cachemgr.pojo.User;
import com.minidouban.cachemgr.util.JedisUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Component
public class CanalLogConsumer {
    private static final String TYPE = "type";
    private static final String TABLE = "table";
    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private static final String CANAL_TOPIC_NAME = "topiccanal";
    private static final int EXPIRE_SECONDS = 10 * 60;
    @Resource
    private JedisUtils jedisUtils;

    @KafkaListener(topics = {CANAL_TOPIC_NAME})
    public void consume(List<String> data) {
        for (String log : data) {
            JSONObject logJson = JSON.parseObject(log);
            String sqlType = logJson.getString(TYPE);
            switch (logJson.getString(TABLE)) {
                case "UserInfo":
                    List<User> users = JSON.parseArray(logJson.getString("data"), User.class);
                    if (sqlType.equals(INSERT) || sqlType.equals(UPDATE)) {
                        users.forEach(user -> {
                            String key = md5DigestAsHex(user.getUsername().getBytes());
                            jedisUtils.setExpire(key, EXPIRE_SECONDS, JSON.toJSONString(user));
                        });
                    } else {
                        try {
                            throw new UnrecognizedSqlTypeException(sqlType);
                        } catch (UnrecognizedSqlTypeException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "ReadingList":
                case "ReadingList_Book":
                    continue;
                default:
                    try {
                        throw new UnrecognizedTableException(logJson.getString("table"));
                    } catch (UnrecognizedTableException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
