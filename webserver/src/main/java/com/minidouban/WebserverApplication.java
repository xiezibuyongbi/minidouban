package com.minidouban;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.minidouban", exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = "com.minidouban.pojo")
@MapperScan(basePackages = "com.minidouban.dao")
public class WebserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserverApplication.class, args);
    }

}
