package com.flab.fbayshop.error;

import static com.flab.fbayshop.error.dto.ErrorType.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flab.fbayshop.common.dto.response.ErrorResponse;
import com.flab.fbayshop.error.dto.ErrorType;
import com.flab.fbayshop.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 바인드 예외 핸들링
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException exception, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        if (!exception.getBindingResult().hasErrors()) {
            return super.handleBindException(exception, headers, status, request);
        }

        String firstMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("BindException : {} | {}", firstMessage, request.getContextPath());
        return ResponseEntity.badRequest().body(ErrorResponse.error(INVALID_PARAMETER, firstMessage).getBody());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        if (!exception.getBindingResult().hasErrors()) {
            return super.handleBindException(exception, headers, status, request);
        }

        String firstMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("MethodArgumentNotValidException : {} | {}", firstMessage, request.getContextPath());
        return ResponseEntity.badRequest().body(ErrorResponse.error(INVALID_PARAMETER, firstMessage).getBody());
    }

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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        log.error("{} : {} | {}", ex.getClass().getName(), ex.getMessage(), request.getContextPath());
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
