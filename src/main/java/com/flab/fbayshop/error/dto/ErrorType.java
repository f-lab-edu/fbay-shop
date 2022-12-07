package com.flab.fbayshop.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    /**
     * 클라이언트 에러
     */
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 4000, "잘못된 요청 파라미터입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 4001, "허용되지 않은 요청입니다."),

    USER_LOGIN_FAIL(HttpStatus.FORBIDDEN, 4003, "로그인에 실패하였습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4100, "등록된 사용자가 없습니다."),

    USER_PROCESS_FAIL(HttpStatus.CONFLICT, 4110, "사용자 요청에 실패하였습니다."),
    USER_DUPLICATED(HttpStatus.CONFLICT, 4111, "중복된 회원정보가 존재합니다."),
    USER_SIGNUP_FAIL(HttpStatus.CONFLICT, 4112, "회원가입에 실패하였습니다."),
    USER_MODIFY_FAIL(HttpStatus.CONFLICT, 4113, "회원정보 수정에 실패하였습니다."),

    /**
     * 서버 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "서버 내부 오류가 발생하였습니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;

}
