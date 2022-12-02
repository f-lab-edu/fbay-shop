package com.flab.fbayshop.auth.service;

import java.security.Principal;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.flab.fbayshop.auth.dto.AuthUser;
import com.flab.fbayshop.auth.dto.UserInfo;
import com.flab.fbayshop.auth.model.CustomUserDetails;
import com.flab.fbayshop.user.exception.UserNotFoundException;
import com.flab.fbayshop.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserMapper userMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        AuthUser authUser = parameter.getParameterAnnotation(AuthUser.class);
        boolean result = false;

        if (authUser != null) {
            result = parameter.getParameterType().equals(UserInfo.class);
        }

        return result;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Object principal = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            principal = authentication.getPrincipal();
        }

        if (principal == null || principal instanceof String) {
            return null;
        }

        User user = (User)authentication.getPrincipal();

        return new UserInfo(userMapper.findByEmail(user.getUsername()).orElseThrow(UserNotFoundException::new));
    }
}
