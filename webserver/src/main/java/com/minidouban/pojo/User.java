package com.minidouban.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private long userId;
    private String username;

    private String password;
    private String email;

    public static String getTableName() {
        return "UserInfo";
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
