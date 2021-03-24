package com.minidouban.controller;

import com.minidouban.component.EmailUtils;
import com.minidouban.component.JedisUtils;
import com.minidouban.dao.UserRepository;
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

    @GetMapping("/test")
    public ModelAndView test() {
//        emailUtils.sendSimpleMail("1950302664@qq.com", "hello", "迷你豆瓣");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("test-1")
    public String tt(@RequestParam("data") String data) {
        System.out.println(data);
        return "hello ajax";
    }
}
