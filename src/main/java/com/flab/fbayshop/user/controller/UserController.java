package com.flab.fbayshop.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flab.fbayshop.auth.dto.AuthUser;
import com.flab.fbayshop.auth.dto.UserInfo;
import com.flab.fbayshop.common.dto.response.ApiResponse;
import com.flab.fbayshop.error.exception.UnAuthorizedException;
import com.flab.fbayshop.user.dto.request.UserModifyRequest;
import com.flab.fbayshop.user.dto.request.UserSignupRequest;
import com.flab.fbayshop.user.dto.response.UserDetailResponse;
import com.flab.fbayshop.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Value("${server.api.url:}")
    private String serverUrl;

    private static final String PROFILE_API_PATH = "/api/v1/user";
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> signupUser(@Valid @RequestBody UserSignupRequest request) {
        return ApiResponse.created(UserDetailResponse.of(userService.signupUser(request)), "회원가입이 완료되었습니다.",
            serverUrl + PROFILE_API_PATH);
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile(@AuthUser UserInfo userInfo) {
        if (userInfo == null) {
            throw new UnAuthorizedException();
        }
        return ApiResponse.ok(UserDetailResponse.of(userService.getUserById(userInfo.getId())));
    }

    @PutMapping
    public ResponseEntity<?> modifyUser(@AuthUser UserInfo userInfo, @Valid @RequestBody UserModifyRequest request) {
        if (userInfo == null) {
            throw new UnAuthorizedException();
        }
        return ApiResponse.created(UserDetailResponse.of(userService.modifyUser(userInfo.getId(), request)), "회원정보가 수정되었습니다.",
            serverUrl + PROFILE_API_PATH);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<?> isExistsEmail(@RequestParam(required = false, value = "email") String email) {
        return ApiResponse.ok(userService.isExistsEmail(email));
    }
}
