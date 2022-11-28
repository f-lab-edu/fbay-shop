package com.flab.fbayshop.user.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

    private Long userId;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public User(Long userId, String email, String password, String name, String nickname) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

    public User modify(User user) {
        this.name = user.getName();
        this.nickname = user.getNickname();
        return this;
    }
}
