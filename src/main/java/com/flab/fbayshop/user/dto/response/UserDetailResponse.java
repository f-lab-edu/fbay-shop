package com.flab.fbayshop.user.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flab.fbayshop.user.model.User;

import lombok.Getter;

@Getter
public class UserDetailResponse implements Serializable {

    private String email;

    private String name;

    private String nickname;

    @JsonCreator
    private UserDetailResponse(@JsonProperty("email") String email, @JsonProperty("name") String name,
        @JsonProperty("nickname") String nickname) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }

    public static UserDetailResponse of(User user) {
        return new UserDetailResponse(user.getEmail(), user.getName(), user.getNickname());
    }

}
