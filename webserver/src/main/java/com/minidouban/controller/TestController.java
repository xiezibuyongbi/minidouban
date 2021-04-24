package com.minidouban.controller;

import com.minidouban.component.EmailUtils;
import com.minidouban.component.JedisUtils;
import com.minidouban.dao.UserRepository;
import org.apache.ibatis.annotations.Param;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class TestController {
    @Resource
    private JedisUtils jedisUtils;
    @Resource
    private UserRepository userRepository;
    @Resource
    private EmailUtils emailUtils;

}
