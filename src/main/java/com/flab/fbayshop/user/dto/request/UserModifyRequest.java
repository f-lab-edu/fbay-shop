package com.flab.fbayshop.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flab.fbayshop.user.model.User;

import lombok.Getter;

@Getter
public class UserModifyRequest {

    @NotBlank(message = "{spring.validation.name.NotBlank.message}")
    @Size(min = 2, max = 10, message = "{spring.validation.name.Size.message}")
    private final String name;

    @NotBlank(message = "{spring.validation.nickname.NotBlank.message}")
    @Size(min = 2, max = 20, message = "{spring.validation.nickname.Size.message}")
    private final String nickname;

    @JsonCreator
    public UserModifyRequest(@JsonProperty("name") String name, @JsonProperty("nickname") String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.builder()
            .nickname(this.nickname)
            .name(this.name)
            .build();
    }

}
