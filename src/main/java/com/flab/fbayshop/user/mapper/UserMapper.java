package com.flab.fbayshop.user.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.flab.fbayshop.user.model.User;

@Mapper
public interface UserMapper {

    int insertUser(User user);
    boolean isExistsEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);

    int getCountByEmail(String email);

    int updateUser(User user);

    void deleteByEmail(String email);
}
