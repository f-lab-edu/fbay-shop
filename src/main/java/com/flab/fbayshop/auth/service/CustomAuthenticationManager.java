package com.flab.fbayshop.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flab.fbayshop.auth.exception.CustomAuthenticationException;
import com.flab.fbayshop.auth.model.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final CustomUserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomUserDetails getDetails = (CustomUserDetails)authentication.getPrincipal();
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(getDetails.getUsername());

        if (!passwordEncoder.matches((String)authentication.getCredentials(), userDetails.getPassword())) {
            log.error("로그인 실패 >> 비밀번호 불일치");
            throw new CustomAuthenticationException();
        }

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
            userDetails.getAuthorities());

    }
}
