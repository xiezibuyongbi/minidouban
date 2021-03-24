package com.minidouban.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
public class EmailUtils {
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.nickname}")
    private String nickname;
    @Resource
    private JavaMailSender mailSender;

    public boolean sendSimpleMail(String to, String subject, String content) {
        MimeMessage mail = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mail, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            helper.setFrom(from, nickname);
            mailSender.send(mail);
            return true;
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
