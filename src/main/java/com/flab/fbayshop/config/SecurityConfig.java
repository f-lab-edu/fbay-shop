package com.flab.fbayshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
            .antMatchers("/favicon*/**")
            .antMatchers("/resources/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        return security

            .csrf().disable()
            .headers().frameOptions().disable()

            .and()
            .authorizeRequests()

            .antMatchers("/api/v1/login/**").permitAll()

            .anyRequest().authenticated()

            .and().build();
    }

}
