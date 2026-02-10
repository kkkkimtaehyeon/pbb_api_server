package com.nhnacademy.shop.auth.controller;

import com.nhnacademy.shop.auth.dto.LoginRequest;
import com.nhnacademy.shop.auth.dto.LoginResponse;
import com.nhnacademy.shop.auth.dto.TokenDto;
import com.nhnacademy.shop.auth.service.AuthService;
import com.nhnacademy.shop.common.response.ApiResponse;
import com.nhnacademy.shop.common.security.MemberDetail;
import com.nhnacademy.shop.member.v2.dto.MemberRegisterRequest;
import com.nhnacademy.shop.member.v2.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nhnacademy.shop.auth.property.CookieProperties;

import static com.nhnacademy.shop.auth.property.JwtProperties.ACCESS_TOKEN_EXPIRE_TIME;
import static com.nhnacademy.shop.auth.property.JwtProperties.REFRESH_TOKEN_EXPIRE_TIME;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final CookieProperties cookieProperties;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLogin(@Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        TokenDto tokens = authService.login(request);
        // Refresh Token을 HttpOnly 쿠키에 담기
        bakeTokenCookies(tokens, response);
        return ResponseEntity.ok(ApiResponse.success(new LoginResponse(tokens.getAccessToken())));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        TokenDto tokens = authService.login(request);
        // Refresh Token을 HttpOnly 쿠키에 담기
        bakeTokenCookies(tokens, response);
        return ResponseEntity.ok(new LoginResponse(tokens.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "accessToken") String accessToken,
            HttpServletResponse response) {
        authService.logout(accessToken);
        // access token 쿠키 무효화
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .maxAge(0)
                .path(cookieProperties.getPath())
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        // refresh token 쿠키 무효화
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path(cookieProperties.getPath())
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody MemberRegisterRequest request) {
        Long memberId = memberService.register(request);
        return ResponseEntity.ok(ApiResponse.success(memberId));

    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        TokenDto tokens = authService.reissue(refreshToken);
        // Refresh Token을 HttpOnly 쿠키에 담기
        bakeTokenCookies(tokens, response);
        return ResponseEntity.ok(new LoginResponse(tokens.getAccessToken()));
    }

    private void bakeTokenCookies(TokenDto tokens, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .path(cookieProperties.getPath())
                .build();
        // Refresh Token을 HttpOnly 쿠키에 담기
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(cookieProperties.isHttpOnly())
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .path(cookieProperties.getPath())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
