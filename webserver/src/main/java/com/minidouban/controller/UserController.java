package com.minidouban.controller;

import com.minidouban.component.EmailUtils;
import com.minidouban.component.RandomUtils;
import com.minidouban.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@Controller
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private EmailUtils emailUtils;

    @Resource
    private RandomUtils randomUtils;

    private final static String emailSubject = "【迷你豆瓣】注册验证";

    @GetMapping("/")
    public String homepage() {
        return "forward:/search";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model, HttpSession session, String username, String password) {
        String message = userService.login(username, password);
        if (message.charAt(0) != ' ') {
            model.addAttribute("msg", message);
            return "login";
        }
        session.setAttribute("userId", Long.valueOf(message.substring(1)));
        session.setAttribute("username", username);
        model.addAttribute("username", username);
        return "redirect:/search";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(String username, String password, String email) {
        String message = userService.register(username, password, email);
        if (message != null) {
            return message;
        }
        return "";
    }

    @PostMapping("/get-verify-code")
    @ResponseBody
    public String sendVerificationCode(@RequestParam("email") String email) {
        String code = randomUtils.getRandomVerificationCode();
        boolean sendResult = emailUtils.sendSimpleMail(email, emailSubject, code);
        if (!sendResult) {
            return "";
        }
        return md5DigestAsHex(code.getBytes());
    }

    @GetMapping("/reset_password")
    public String resetPassword() {
        return "reset_password";
    }

    @PostMapping("/reset_password")
    @ResponseBody
    public String resetPassword(@RequestParam("username") String username, @RequestParam("password") String desiredPassword, @RequestParam("email") String email) {
        String message = userService.resetPassword(username, desiredPassword, email);
        if (message != null) {
            return message;
        }
        return "";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("username");
        return "redirect:/search";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
