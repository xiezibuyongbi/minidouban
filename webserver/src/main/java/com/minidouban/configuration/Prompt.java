package com.minidouban.configuration;

import java.lang.reflect.Field;

public class Prompt {
    // 1
    public static final String failToLoginPrompt = "用户名不存在或密码不正确";
    public static final String fillEmptyBlankPrompt = "请输入";
    public static final String passwordsNotMatchPrompt = "两次输入的密码不一致";
    // 4
    public static final String repeatedUsernamePrompt = "该用户名已存在";
    public static final String repeatedEmailPrompt = "该邮箱已被使用";
    public static final String invalidCharExistPrompt = "不能含有非法字符";
    // 7
    public static final String unexpectedFailure = "未知错误，操作失败";
    public static final String notExistedUserOrWrongEmailPrompt = "用户名不存在或邮箱不正确";
    public static final String noSearchResultPrompt = "暂时无相关结果，可以换个关键词~";
}
