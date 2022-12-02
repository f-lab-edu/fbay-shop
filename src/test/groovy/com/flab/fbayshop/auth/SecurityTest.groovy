package com.flab.fbayshop.auth

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

import spock.lang.Specification

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest extends Specification {

    @Autowired
    private PasswordEncoder passwordEncoder

    def "패스워드 인코딩 테스트 - 성공"() {
        given:
        String beforePw = "1234"

        when:
        String afterPw = passwordEncoder.encode(beforePw)

        then:
        passwordEncoder.matches(beforePw, afterPw) == true
        passwordEncoder.matches("12345", afterPw) == false

    }
}
