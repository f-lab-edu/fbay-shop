package com.flab.fbayshop.user.dto.request;

import static com.flab.fbayshop.common.validate.ValidationPattern.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.flab.fbayshop.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {

    @NotBlank(message = "{spring.validation.email.NotBlank.message}")
    @Pattern(regexp = EMAIL_PATTERN, message = "{spring.validation.email.regexp.message}")
    private String email;

    @NotBlank(message = "{spring.validation.password.NotBlank.message}")
    @Pattern(regexp = PASSWORD_PATTERN, message = "{spring.validation.password.regexp.message}")
    private String password;

    @NotBlank(message = "{spring.validation.name.NotBlank.message}")
    @Size(min = 2, max = 10, message = "{spring.validation.name.Size.message}")
    private String name;

    @NotBlank(message = "{spring.validation.nickname.NotBlank.message}")
    @Size(min = 2, max = 20, message = "{spring.validation.nickname.Size.message}")
    private String nickname;

    public User toEntity(String password) {
        return User.builder()
            .email(this.email)
            .password(password)
            .nickname(this.nickname)
            .name(this.name)
            .build();
    }

}
