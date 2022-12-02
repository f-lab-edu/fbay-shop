package com.flab.fbayshop.auth.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import org.springframework.web.server.MethodNotAllowedException;

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
            throw new MethodNotAllowedException(request.getMethod(), List.of(HttpMethod.POST));
        }
        if (!request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            // throw new InvalidContentTypeException("지원하지 않는 요청타입입니다.");
            throw new CustomAuthenticationException("지원하지 않는 요청타입입니다.");
        }

        UserInfo userInfo = objectMapper.readValue(
            StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8), UserInfo.class);

        String email = userInfo.getEmail();
        String password = userInfo.getPassword();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new CustomAuthenticationException("이메일 또는 비밀번호가 잘못 입력되었습니다.");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new CustomUserDetails(userInfo.toModel()), password);

        token.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return getAuthenticationManager().authenticate(token);
    }

}
