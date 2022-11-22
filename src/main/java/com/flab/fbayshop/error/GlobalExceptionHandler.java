package com.flab.fbayshop.error;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flab.fbayshop.common.dto.response.ErrorResponse;
import com.flab.fbayshop.error.dto.ErrorType;
import com.flab.fbayshop.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException exception,
        final HttpServletRequest request) {

        log.error("BusinessException : {} | {}", exception.getMessage(), request.getRequestURL());
        return ErrorResponse.error(exception.getErrorType());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(final Exception exception,
        final HttpServletRequest request) {

        log.error("Exception : {} | {}", exception.getMessage(), request.getRequestURL());
        return ErrorResponse.error(ErrorType.INTERNAL_SERVER_ERROR);
    }

}
