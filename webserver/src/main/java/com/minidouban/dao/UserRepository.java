package com.minidouban.dao;

import com.minidouban.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    public User findByUsername(String username);

    public User findByEmail(String email);

    public int updatePasswordByUsernameAndByEmail(@Param("username") String username, @Param("email") String email, @Param("password") String desiredPassword);

    public int insert(@Param("username") String username, @Param("password") String password, @Param("email") String email);

    public boolean existsById(@Param("userId") long userId);
}
