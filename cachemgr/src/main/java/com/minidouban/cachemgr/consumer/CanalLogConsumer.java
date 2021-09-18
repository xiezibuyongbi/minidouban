package com.minidouban.cachemgr.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.minidouban.cachemgr.exception.UnrecognizedSqlTypeException;
import com.minidouban.cachemgr.exception.UnrecognizedTableException;
import com.minidouban.cachemgr.pojo.MQDelCacheMsg;
import com.minidouban.cachemgr.pojo.ReadingList;
import com.minidouban.cachemgr.pojo.ReadingListBook;
import com.minidouban.cachemgr.pojo.User;
import com.minidouban.cachemgr.producer.CacheDelMsgProducer;
import com.minidouban.cachemgr.util.CacheKeyGenerator;
import com.minidouban.cachemgr.util.JedisUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private CacheKeyGenerator cacheKeyGenerator;
    @Resource
    private CacheDelMsgProducer cacheDelMsgProducer;

    @KafkaListener(topics = {CANAL_TOPIC_NAME})
    public void consume(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) throws UnrecognizedSqlTypeException, UnrecognizedTableException {
        String log = consumerRecord.value();
        JSONObject logJson = JSON.parseObject(log);
        String sqlType = logJson.getString(TYPE);
        if (!sqlType.equals(INSERT) && !sqlType.equals(UPDATE)) {
            throw new UnrecognizedSqlTypeException(sqlType);
        }
        String key;
        switch (logJson.getString(TABLE)) {
            case "UserInfo":
                List<User> users = JSON.parseArray(logJson.getString("data"), User.class);
                List<MQDelCacheMsg> notDeletedUser = new ArrayList<>(users.size());
                users.forEach(user -> {
                    String userPOJOKey = cacheKeyGenerator.getRedisKey(User.getTableName(), user.getUsername());
                    if (!jedisUtils.delIfExisting(userPOJOKey)) {
                        MQDelCacheMsg mqDelCacheMsg = new MQDelCacheMsg();
                        mqDelCacheMsg.setBusinessName(MQDelCacheMsg.BusinessName.USER);
                        mqDelCacheMsg.setOperation(MQDelCacheMsg.CacheOperation.DELETE);
                        mqDelCacheMsg.setEntryId(user.getUsername());
                        notDeletedUser.add(mqDelCacheMsg);
                    }
                });
                notDeletedUser.forEach(msg -> cacheDelMsgProducer.sendDeleteMsg(msg));
                break;
            case "ReadingList":
                List<ReadingList> readingLists = JSON.parseArray(logJson.getString("data"), ReadingList.class);
                key = cacheKeyGenerator.getRedisKey(ReadingList.getTableName(), readingLists.get(0).getUserId());
                if (!jedisUtils.delIfExisting(key)) {
                    return;
                }
                break;
            case "ReadingList_Book":
                List<ReadingListBook> readingListBooks = JSON.parseArray(logJson.getString("data"), ReadingListBook.class);
                key = cacheKeyGenerator.getRedisKey(ReadingListBook.getTableName(), readingListBooks.get(0).getListId());
                if (!jedisUtils.delIfExisting(key)) {
                    return;
                }
            default:
                acknowledgment.acknowledge();
                throw new UnrecognizedTableException(logJson.getString("table"));
        }
        acknowledgment.acknowledge();
    }
}
