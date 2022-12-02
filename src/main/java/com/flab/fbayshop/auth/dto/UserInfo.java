package com.flab.fbayshop.auth.dto;

import java.io.Serializable;

import com.flab.fbayshop.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String nickname;

    public UserInfo(User user) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.nickname = user.getNickname();
    }

    public User toModel() {
        return User.builder()
            .userId(id)
            .email(email)
            .password(password)
            .name(name)
            .nickname(nickname)
            .build();
    }

}
