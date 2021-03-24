package com.minidouban.component;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Component
public class RandomUtils {
    private final Random random = new Random();

    public String getRandomVerificationCode() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }
}
