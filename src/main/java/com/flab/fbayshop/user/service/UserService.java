package com.flab.fbayshop.user.service;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.flab.fbayshop.error.exception.InvalidParameterException;
import com.flab.fbayshop.user.dto.request.UserModifyRequest;
import com.flab.fbayshop.user.dto.request.UserSignupRequest;
import com.flab.fbayshop.user.exception.UserDuplicatedException;
import com.flab.fbayshop.user.exception.UserNotFoundException;
import com.flab.fbayshop.user.exception.UserProcessException;
import com.flab.fbayshop.user.mapper.UserMapper;
import com.flab.fbayshop.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Boolean isExistsEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new InvalidParameterException("이메일이 입력되지 않았습니다.");
        }
        return userMapper.isExistsEmail(email);
    }

    @Transactional
    public User signupUser(UserSignupRequest request) {

        if (userMapper.isExistsEmail(request.getEmail())) {
            throw new UserDuplicatedException();
        }

        User saveUser = request.toEntity(passwordEncoder.encode(request.getPassword()));

        int res = userMapper.insertUser(saveUser);

        if (res != 1) {
            throw new UserProcessException(USER_SIGNUP_FAIL);
        }

        return saveUser;
    }

    @Transactional
    public User getUserById(Long userId) {
        return userMapper.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User modifyUser(Long userId, UserModifyRequest request) {

        User saveUser = userMapper.findById(userId).orElseThrow(UserNotFoundException::new);

        int res = userMapper.updateUser(saveUser.modify(request.toEntity()));

        if (res != 1) {
            throw new UserProcessException(USER_MODIFY_FAIL);
        }

        return saveUser;
    }

    @Transactional
    public void deleteByEmail(String email) {

        if (!StringUtils.hasText(email)) {
            throw new InvalidParameterException();
        }
        userMapper.deleteByEmail(email);
    }

}
