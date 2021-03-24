package com.minidouban.component;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class BeanManager implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanManager.applicationContext = applicationContext;
    }

    public static <T> T getBean(final Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }
}
