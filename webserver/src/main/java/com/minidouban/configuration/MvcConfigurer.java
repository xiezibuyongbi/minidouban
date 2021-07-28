package com.minidouban.configuration;

import com.minidouban.interceptor.AuthorizationInterceptor;
import com.minidouban.interceptor.NotLoggedOnInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthorizationInterceptor()).addPathPatterns(
//                "/login**", "/register**", "reset_password**"
//        );
        registry.addInterceptor(new AuthorizationInterceptor()).addPathPatterns(
                "/logout**", "/reading_list**", "/add-book**", "/rename-list**",
                "/create-list**", "/delete-list**", "/delete-all-list**",
                "/remove-book**", "/login**"
        );
    }

}
