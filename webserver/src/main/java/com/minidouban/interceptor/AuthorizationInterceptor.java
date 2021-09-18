package com.minidouban.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.minidouban.component.CacheKeyGenerator;
import com.minidouban.component.JedisUtils;
import com.minidouban.component.SafetyUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

import static com.minidouban.component.TokenGenerator.Token;

public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final long authorizeInterval = 1;
    private static final TemporalUnit intervalUnit = ChronoUnit.DAYS;
    private static final String TOKEN_CACHE_KEY_PREFIX = "token";
    private static final int TOKEN_EXPIRE_SECONDS = 60 * 20;

    @Resource
    private JedisUtils jedisUtils;
    @Resource
    private SafetyUtils safetyUtils;
    @Resource
    private CacheKeyGenerator cacheKeyGenerator;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String cipherToken = request.getHeader(AUTH_HEADER_NAME);
        if (cipherToken == null || "".equals(cipherToken)) {
            if (request.getRequestURI().equals("/login")) {
                return true;
            }
            response.sendRedirect("/login");
            return false;
        }
        Token declaredToken = JSON.parseObject(safetyUtils.decrypt(cipherToken), Token.class);
        if (declaredToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/login");
            return false;
        }
        long declaredTimeStamp = declaredToken.getTimestamp();
        long declaredUserId = declaredToken.getUserId();
        if (declaredTimeStamp < System.currentTimeMillis()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/login");
            return false;
        }
        String cacheKey = cacheKeyGenerator.getRedisKey(TOKEN_CACHE_KEY_PREFIX, declaredUserId);
        Token storedToken = JSON.parseObject(jedisUtils.get(cacheKey), Token.class);
        if (storedToken == null || storedToken.getUserId() != declaredUserId || storedToken.getTimestamp() != declaredTimeStamp) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/login");
            return false;
        }
        long expireTime =
                Instant.now().plus(authorizeInterval, intervalUnit)
                        .toEpochMilli();
        storedToken.setTimestamp(expireTime);
        String newTokenStr = JSON.toJSONString(storedToken);
        jedisUtils.setExpire(cacheKey, TOKEN_EXPIRE_SECONDS, newTokenStr);
        response.setHeader(AUTH_HEADER_NAME, safetyUtils.encrypt(newTokenStr));
        if (request.getRequestURI().equals("/login")) {
            response.sendRedirect("/search");
        }
        return true;
    }
}
