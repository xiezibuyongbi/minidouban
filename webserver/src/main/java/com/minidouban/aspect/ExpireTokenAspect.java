package com.minidouban.aspect;

import com.minidouban.annotation.ExpireToken;
import com.minidouban.component.JedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

// not in use
@Aspect
@Component
public class ExpireTokenAspect {
    @Resource
    private JedisUtils jedisUtils;

    //    @After("execution(public * com.minidouban.service.*.*(..))")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getAnnotation(ExpireToken.class) == null
                && method.getDeclaringClass().getAnnotation(ExpireToken.class) == null) {
            return;
        }
        jedisUtils.zremRangeByScore("token", 0, System.currentTimeMillis());
    }
}
