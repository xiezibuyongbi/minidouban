package com.minidouban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minidouban.annotation.ExpireToken;
import com.minidouban.component.JedisUtils;
import com.minidouban.dao.ReadingListBookRepository;
import com.minidouban.dao.ReadingListRepository;
import com.minidouban.dao.UserRepository;
import com.minidouban.pojo.ReadingList;
import com.minidouban.pojo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Service
@ExpireToken
public class ReadingListService {
    private final String redisKeyPrefix = "readingList";
    private final int expireSeconds = 60 * 20;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private ReadingListRepository readingListRepository;
    @Resource
    private ReadingListBookRepository readingListBookRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private JedisUtils jedisUtils;

    public List<ReadingList> getReadingListsOfUser(long userId) {
        String key = getRedisKey(userId);
        List<ReadingList> readingList = new ArrayList<>();
        try {
            readingList = objectMapper.readValue(jedisUtils.get(key), new TypeReference<List<ReadingList>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (readingList.size() != 0) {
            try {
                jedisUtils.setExpire(key, expireSeconds, objectMapper.writeValueAsString(readingList));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return readingList;
        }
        readingList = readingListRepository.findByUserId(userId);
        try {
            jedisUtils.setExpire(key, expireSeconds, objectMapper.writeValueAsString(readingList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return readingList;
    }

    // TODO 用注解给kafka发删除readinglist缓存消息
    public int renameReadingList(long userId, String oldListName, String desiredListName) {
        if (containsInvalidCharacter(desiredListName)) {
            return 0;
        }
        String key = getRedisKey(userId);
        jedisUtils.del(key);
        int ret = readingListRepository.updateListName(userId, oldListName, desiredListName);
        jedisUtils.del(key);
        return ret;
    }

    @Transactional
    public long createReadingList(long userId, String listName) {
        if (containsInvalidCharacter(listName)) {
            return 0;
        }
        if (readingListRepository.insert(userId, listName) == 0) {
            return 0;
        }
        ReadingList readingList = readingListRepository.findByUserIdAndListName(userId, listName);
        if (readingList == null) {
            return 0;
        }
        return readingList.getListId();
    }

    @Transactional
    public int deleteReadingList(long userId, long listId) {
        readingListBookRepository.deleteAllByListId(listId);
        return readingListRepository.deleteByUserIdAndListId(userId, listId);
    }

    public int deleteAllReadingLists(long userId) {
        if (!userRepository.existsById(userId)) {
            return 0;
        }
        getReadingListsOfUser(userId).forEach(list -> readingListBookRepository.deleteAllByListId(list.getListId()));
        return readingListRepository.deleteAllByUserId(userId);
    }

    private boolean containsInvalidCharacter(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        final String pattern = ".*[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?].*";
        if (Pattern.matches(pattern, str) || "".equals(str)) {
            return true;
        }
        return false;
    }

    private String getRedisKey(long userId) {
        return redisKeyPrefix + md5DigestAsHex(String.valueOf(userId).getBytes());
    }
}
