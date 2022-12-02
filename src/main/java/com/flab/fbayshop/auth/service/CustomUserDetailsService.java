package com.flab.fbayshop.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.flab.fbayshop.auth.model.CustomUserDetails;
import com.flab.fbayshop.user.exception.UserNotFoundException;
import com.flab.fbayshop.user.mapper.UserMapper;
import com.flab.fbayshop.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(username).orElseThrow(UserNotFoundException::new);
        return new CustomUserDetails(user);
    }

}
