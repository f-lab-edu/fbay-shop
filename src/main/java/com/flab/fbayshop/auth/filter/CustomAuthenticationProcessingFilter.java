package com.flab.fbayshop.auth.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.fbayshop.auth.dto.UserInfo;
import com.flab.fbayshop.auth.exception.CustomAuthenticationException;
import com.flab.fbayshop.auth.model.CustomUserDetails;

@Component
public class CustomAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationProcessingFilter(ObjectMapper objectMapper,
        AuthenticationManager authenticationManager,
        AuthenticationSuccessHandler authenticationSuccessHandler,
        AuthenticationFailureHandler authenticationFailureHandler) {
        super(new AntPathRequestMatcher("/api/v1/login", HttpMethod.POST.toString()), authenticationManager);
        this.objectMapper = objectMapper;
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
        AuthenticationException, IOException, ServletException {

        if (!HttpMethod.POST.matches(request.getMethod())) {
            throw new CustomAuthenticationException();
        }

        if (!MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
            throw new CustomAuthenticationException();
        }

        UserInfo userInfo = objectMapper.readValue(
            StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8), UserInfo.class);

        String email = userInfo.getEmail();
        String password = userInfo.getPassword();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new CustomAuthenticationException();
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new CustomUserDetails(userInfo.toModel()), password);

        token.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return getAuthenticationManager().authenticate(token);
    }

}
