package com.minidouban.aspect;

import com.minidouban.annotation.BusinessPrefix;
import com.minidouban.annotation.ItemId;
import com.minidouban.annotation.SendDelMsg;
import com.minidouban.component.CacheKeyGenerator;
import com.minidouban.pojo.MQDelCacheMsg;
import com.minidouban.producer.CacheDelMsgProducer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class SendDelMsgAspect {
    @Resource
    private CacheKeyGenerator cacheKeyGenerator;
    @Resource
    private CacheDelMsgProducer cacheDelMsgProducer;

    @After("execution(public * com.minidouban.service.*.*(..))")
    public void after(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        SendDelMsg sendDelMsgAnnotation = method.getAnnotation(SendDelMsg.class);
        if (sendDelMsgAnnotation == null) {
            return;
        }
        Field businessPrefixField = null;
        for (Field field : method.getDeclaringClass().getDeclaredFields()) {
            BusinessPrefix businessPrefixAnnotation = field.getDeclaredAnnotation(BusinessPrefix.class);
            if (businessPrefixAnnotation != null) {
                businessPrefixField = field;
                break;
            }
        }
        if (businessPrefixField == null || businessPrefixField.getType() != String.class) {
            return;
        }
        String businessPrefix;
        try {
            businessPrefix = (String) businessPrefixField.get(joinPoint.getThis());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        String entryId = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(ItemId.class) != null) {
                try {
                    if (parameters[i].getType().isPrimitive()) {
                        entryId = String.valueOf(joinPoint.getArgs()[i]);
                    } else {
                        entryId = (String) joinPoint.getArgs()[i];
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        if (entryId == null) {
            return;
        }
        String redisKey = cacheKeyGenerator.getRedisKey(businessPrefix, entryId);
        MQDelCacheMsg mqDelCacheMsg = new MQDelCacheMsg();
        mqDelCacheMsg.setEntryId(entryId);
        mqDelCacheMsg.setBusinessName(MQDelCacheMsg.businessNameMap.get(businessPrefix));
        mqDelCacheMsg.setOperation(MQDelCacheMsg.CacheOperation.DELETE);
        cacheDelMsgProducer.sendDeleteMsg(mqDelCacheMsg);
    }
}
