package com.minidouban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minidouban.annotation.ExpireToken;
import com.minidouban.component.JedisUtils;
import com.minidouban.component.SafetyUtils;
import com.minidouban.dao.UserRepository;
import com.minidouban.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.regex.Pattern;

import static com.minidouban.configuration.Prompt.*;
import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Service
@ExpireToken
public class UserService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private SafetyUtils safetyUtils;
    @Resource
    private JedisUtils jedisUtils;
    private static final int expireSeconds = 10 * 60;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String register(String username, String password, String email) {
        if (isNullOrEmpty(username, password, email)) {
            return fillEmptyBlankPrompt;
        }
        if (containsInvalidCharacter(username, email)) {
            return invalidCharExistPrompt;
        }
        String key = md5DigestAsHex(username.getBytes());
        User user = getUser(username, key);
        if (user != null) {
            return repeatedUsernamePrompt;
        }
        if (emailAlreadyExists(email)) {
            return repeatedEmailPrompt;
        }
        if (userRepository
                .insert(username, safetyUtils.encodePassword(password),
                        email) !=
                1) {
            return unexpectedFailure;
        }
        user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        try {
            jedisUtils.setExpire(key, expireSeconds,
                    objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String resetPassword(String username, String desiredPassword,
                                String email) {
        if (isNullOrEmpty(username, desiredPassword, email)) {
            return fillEmptyBlankPrompt;
        }
        if (containsInvalidCharacter(username, email)) {
            return invalidCharExistPrompt;
        }
        String key = md5DigestAsHex(username.getBytes());
        User user = getUser(username, key);
        if (user == null || !user.getEmail().equals(email)) {
            return notExistedUserOrWrongEmailPrompt;
        }
        String redisResult = jedisUtils.get(key);
        if (redisResult != null) {
            if (jedisUtils.del(key) == 0) {
                return unexpectedFailure;
            }
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
        String key = md5DigestAsHex(username.getBytes());
        User user = getUser(username, key);
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

    private User getUser(String username, String key) {
        User user = null;
        String redisResult = jedisUtils.get(key);
        if (redisResult != null) {
            try {
                user = objectMapper.readValue(redisResult, User.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            user = userRepository.findByUsername(username);
        }
        try {
            jedisUtils.setExpire(key, expireSeconds,
                    objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return user;
    }
}
