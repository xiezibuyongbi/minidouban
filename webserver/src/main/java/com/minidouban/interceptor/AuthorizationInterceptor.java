package com.minidouban.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.minidouban.component.JedisUtils;
import com.minidouban.component.SafetyUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String headerName = "Authorization";
    private static final long authorizeInterval = 1;
    private static final TemporalUnit intervalUnit = ChronoUnit.DAYS;

    @Resource
    private JedisUtils jedisUtils;
    @Resource
    private SafetyUtils safetyUtils;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String token = request.getHeader(headerName);
        if (token == null || "".equals(token)) {
            if (request.getRequestURI().equals("/login")) {
                return true;
            }
            response.sendRedirect("/login");
            return false;
        }
        try {
            JSONObject plain =
                    JSON.parseObject(new String(
                            safetyUtils.decrypt(token.getBytes())));
            String userId = plain.getString("userId");
            long timestamp = plain.getLongValue("timestamp");
            if (timestamp <= System.currentTimeMillis()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendRedirect("/login");
                return false;
            }
            long storedTimestamp =
                    jedisUtils.zScore("token", userId);
            if (Math.abs(storedTimestamp - timestamp) >=
                    ChronoUnit.SECONDS.getDuration().toMillis()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendRedirect("/login");
                return false;
            }
            long expireTime =
                    Instant.now().plus(authorizeInterval, intervalUnit)
                            .toEpochMilli();
            plain.replace("timestamp",
                    expireTime);
            jedisUtils.zAddExpire("token", userId,
                    expireTime);
            response.setHeader(headerName,
                    safetyUtils.encrypt(plain.toJSONString()
                            .getBytes()));
            response.sendRedirect("/search");
            return true;
        } catch (NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/login");
            return false;
        }
    }
}
