package com.minidouban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minidouban.annotation.BusinessPrefix;
import com.minidouban.annotation.ExpireToken;
import com.minidouban.annotation.ItemId;
import com.minidouban.annotation.SendDelMsg;
import com.minidouban.component.CacheKeyGenerator;
import com.minidouban.component.JedisUtils;
import com.minidouban.component.SafetyUtils;
import com.minidouban.dao.UserRepository;
import com.minidouban.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.regex.Pattern;

import static com.minidouban.configuration.Prompt.*;

@Service
@ExpireToken
public class UserService {
    private static final int expireSeconds = 10 * 60;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BusinessPrefix
    private final String redisKeyPrefix = User.getTableName();
    @Resource
    private UserRepository userRepository;
    @Resource
    private SafetyUtils safetyUtils;
    @Resource
    private JedisUtils jedisUtils;
    @Resource
    private CacheKeyGenerator cacheKeyGenerator;

    @SendDelMsg
    public String register(@ItemId("username") String username, String password, String email) {
        if (isNullOrEmpty(username, password, email)) {
            return fillEmptyBlankPrompt;
        }
        if (containsInvalidCharacter(username, email)) {
            return invalidCharExistPrompt;
        }
        User user = getUser(username);
        if (user != null) {
            return repeatedUsernamePrompt;
        }
        if (emailAlreadyExists(email)) {
            return repeatedEmailPrompt;
        }
        if (userRepository.insert(username, safetyUtils.encodePassword(password), email) != 1) {
            return unexpectedFailure;
        }
        user = getUser(username);
        if (user == null) {
            return unexpectedFailure;
        }
        return null;
    }

    @SendDelMsg
    public String resetPassword(@ItemId("username") String username, String desiredPassword,
                                String email) {
        if (isNullOrEmpty(username, desiredPassword, email)) {
            return fillEmptyBlankPrompt;
        }
        if (containsInvalidCharacter(username, email)) {
            return invalidCharExistPrompt;
        }
        User user = getUser(username);
        if (user == null || !user.getEmail().equals(email)) {
            return notExistedUserOrWrongEmailPrompt;
        }
        String encodedPassword = safetyUtils.encodePassword(desiredPassword);
        user.setPassword(encodedPassword);
        return userRepository
                .updatePasswordByUsernameAndByEmail(username, email,
                        encodedPassword) == 1 ? null : unexpectedFailure;
    }

    public String login(String username, String password) {
        if (isNullOrEmpty(username, password)) {
            return fillEmptyBlankPrompt;
        }
        if (containsInvalidCharacter(username)) {
            return invalidCharExistPrompt;
        }
        User user = getUser(username);
        return user != null &&
                safetyUtils.matches(password, user.getPassword()) ?
                " " + user.getUserId() : failToLoginPrompt;
    }

    private boolean usernameAlreadyExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    private boolean emailAlreadyExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    private boolean containsInvalidCharacter(String... strs) {
        for (String str : strs) {
            str = str.trim();
            final String pattern =
                    ".*[\\s~·`!！#￥$%^……&*（()）\\-——\\-=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。》>、/？?].*";
            if (Pattern.matches(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNullOrEmpty(String... strs) {
        for (String s : strs) {
            if (s == null || "".equals(s)) {
                return true;
            }
        }
        return false;
    }

    private User getUser(String username) {
        String key = cacheKeyGenerator.getRedisKey(redisKeyPrefix, username);
        User user = null;
        String redisResult = jedisUtils.get(key);
        if (redisResult != null) {
            try {
                user = objectMapper.readValue(redisResult, User.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            jedisUtils.setExpire(key, expireSeconds, redisResult);
        } else {
            user = userRepository.findByUsername(username);
            try {
                jedisUtils.setExpire(key, expireSeconds,
                        objectMapper.writeValueAsString(user));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
