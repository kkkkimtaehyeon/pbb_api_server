package com.nhnacademy.shop.common.handler;

import com.nhnacademy.shop.common.exceptions.LoginFailedException;
import com.nhnacademy.shop.common.exceptions.SignupFailedException;
import com.nhnacademy.shop.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {LoginFailedException.class})
    public ResponseEntity<ApiResponse<Void>> handleLoginFailed(LoginFailedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(null, "LOGIN_FAILED", e.getMessage()));
    }

    @ExceptionHandler(value = {SignupFailedException.class})
    public ResponseEntity<ApiResponse<Void>> handleSignupFailed(SignupFailedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(null, "SIGNUP_FAILED", e.getMessage()));
    }
}
