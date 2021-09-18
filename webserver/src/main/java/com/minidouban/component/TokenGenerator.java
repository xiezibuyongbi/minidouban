package com.minidouban.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    public Token generateToken(long userId) {
        long now = System.currentTimeMillis();
        Token token = new Token();
        token.setTimestamp(now);
        token.setUserId(userId);
        return token;
    }

    public static class Token {
        private long userId;
        private long timestamp;

        private Token() {
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
