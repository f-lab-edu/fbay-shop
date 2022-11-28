package com.flab.fbayshop.user.service

import com.flab.fbayshop.user.dto.request.UserModifyRequest
import com.flab.fbayshop.user.dto.request.UserSignupRequest
import com.flab.fbayshop.user.exception.UserDuplicatedException
import com.flab.fbayshop.user.exception.UserNotFoundException
import com.flab.fbayshop.user.mapper.UserMapper
import com.flab.fbayshop.user.model.User
import org.spockframework.spring.SpringBean
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceTest extends Specification {

    @SpringBean
    private UserMapper userMapper = Mock()

    @SpringBean
    private PasswordEncoder passwordEncoder = Mock()

    private UserService userService = new UserService(userMapper, passwordEncoder)

    def "회원가입 - 성공"() {

        given:
        UserSignupRequest request = new UserSignupRequest("test@t.c", "1234", "테스트1", "테스트1")
        userMapper.isExistsEmail(request.getEmail()) >> false
        passwordEncoder.encode(request.getPassword()) >> "qwerty"
        userMapper.insertUser(_) >> 1

        when:
        User user = userService.signupUser(request)

        then:
        println user
        user != null
        user.getName() == request.getName()
        user.getEmail() == request.getEmail()
        user.getNickname() == request.getNickname()

    }

    def "회원가입 - 실패 (이메일 중복)"() {

        given:
        UserSignupRequest request = new UserSignupRequest("test@t.c", "1234", "테스트1", "테스트1")
        userMapper.isExistsEmail(request.getEmail()) >> true

        when:
        userService.signupUser(request)

        then:
        thrown(UserDuplicatedException)

    }

//    시큐리티 로그인 사용
//    def "이메일 로그인 - 성공"() {
//        given:
//        UserLoginRequest request = new UserLoginRequest("test@t.c", "1234")
//        String encodedPw = "qwrqwer"
//        userMapper.findByEmail(request.getEmail()) >> Optional.of(User.builder().email(request.getEmail()).password(encodedPw).build())
//        passwordEncoder.matches(request.getPassword(), encodedPw) >> true
//        MockHttpSession session = new MockHttpSession();
//
//        when:
//        boolean result = userService.loginUser(request, session)
//
//        then:
//        result == true
//        SessionUtils.getSession(session).getUserInfo().getEmail() == request.getEmail()
//    }
//
//    def "이메일 로그인 - 실패(비밀번호 오류)"() {
//        given:
//        UserLoginRequest request = new UserLoginRequest("test@t.c", "1234")
//        String encodedPw = "qwrqwer"
//        userMapper.findByEmail(request.getEmail()) >> Optional.of(User.builder().email(request.getEmail()).password(encodedPw).build())
//        passwordEncoder.matches(request.getPassword(), encodedPw) >> false
//        MockHttpSession session = new MockHttpSession()
//
//        when:
//        userService.loginUser(request, session)
//
//        then:
//        thrown(UserLoginFailException)
//    }
//
//    def "로그아웃 - 성공"() {
//        given:
//        UserLoginRequest request = new UserLoginRequest("test@t.c", "1234")
//        String encodedPw = "qwrqwer"
//        userMapper.findByEmail(request.getEmail()) >> Optional.of(User.builder().email(request.getEmail()).password(encodedPw).build())
//        passwordEncoder.matches(request.getPassword(), encodedPw) >> true
//        MockHttpSession session = new MockHttpSession();
//
//        when:
//        boolean isLogin = userService.loginUser(request, session)
//        boolean isLogout = userService.logoutUser(session)
//
//        then:
//        isLogin == true
//        isLogout == true
//        SessionUtils.getSession(session) == null
//    }

    def "사용자 프로필 조회 - 성공"() {
        given:
        String email = "test@t.c"
        userMapper.findById(_) >> Optional.of(User.builder().email(email).build())

        when:
        User user = userService.getUserById(1L)

        then:
        user.getEmail() == email
    }

    def "사용자 프로필 조회 - 실패(사용자 없음)"() {
        given:
        userMapper.findById(_) >> Optional.empty()

        when:
        userService.getUserById(1L)

        then:
        thrown(UserNotFoundException)
    }

    def "사용자 프로필 수정 - 성공"() {
        given:
        Long userId = 1L
        User savedUser = User.builder().userId(userId).email("test@c.m").nickname("1234").build()
        UserModifyRequest request = new UserModifyRequest("afterName", "afterNickname")
        userMapper.findById(_) >> Optional.of(savedUser)
        userMapper.updateUser(_) >> 1

        when:
        User modifyUser = userService.modifyUser(userId, request)

        then:
        modifyUser.getUserId() == userId
        modifyUser.getName() == request.getName()
        modifyUser.getNickname() == request.getNickname()

    }

    def "사용자 프로필 수정 - 실패(사용자 없음)"() {

        given:
        userMapper.findById(_) >> Optional.empty()

        when:
        userService.modifyUser(1L, new UserModifyRequest("afterName", "afterNickname"))

        then:
        thrown(UserNotFoundException)
    }

}
