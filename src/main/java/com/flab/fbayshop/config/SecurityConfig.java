package com.flab.fbayshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.flab.fbayshop.auth.filter.CustomAuthenticationProcessingFilter;
import com.flab.fbayshop.auth.handler.CustomLogoutSuccessHandler;
import com.flab.fbayshop.auth.service.CustomAuthenticationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProcessingFilter authenticationProcessingFilter;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final CustomAuthenticationManager authenticationManager;

    @Bean
    PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return passwordEncoder;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
            .antMatchers("/favicon*/**")
            .antMatchers("/resources/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        security.logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/logout", HttpMethod.POST.toString()))
            .logoutSuccessHandler(logoutSuccessHandler);

        return security

            .csrf().disable()
            .headers().frameOptions().disable()

            .and()
            .authorizeRequests()

            .antMatchers("/api/v1/login/**").permitAll()
            .antMatchers("/api/v1/user/**").permitAll()

            .anyRequest().authenticated()

            .and()
            // .formLogin().disable()
            // .and()
            .addFilterBefore(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)

            // .loginPage("/api/v1/login")
            // .usernameParameter("email")
            // .passwordParameter("password")
            // .successHandler(successHandler)
            // .and()
            // .userDetailsService(userDetailsService)

            .build();
    }


}
