package com.minidouban.controller;

import com.minidouban.component.EmailUtils;
import com.minidouban.component.JedisUtils;
import com.minidouban.dao.UserRepository;
import org.springframework.stereotype.Controller;

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
