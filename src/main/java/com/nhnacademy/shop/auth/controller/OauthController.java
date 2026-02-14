package com.nhnacademy.shop.auth.controller;

import com.nhnacademy.shop.auth.dto.TokenDto;
import com.nhnacademy.shop.auth.property.CookieProperties;
import com.nhnacademy.shop.auth.property.PaycoOAuthProperties;
import com.nhnacademy.shop.auth.service.OauthService;
import com.nhnacademy.shop.common.properties.AppProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.nhnacademy.shop.auth.property.JwtProperties.ACCESS_TOKEN_EXPIRE_TIME;
import static com.nhnacademy.shop.auth.property.JwtProperties.REFRESH_TOKEN_EXPIRE_TIME;


@RequiredArgsConstructor
@RequestMapping("/oauth")
@RestController
public class OauthController {
    private final OauthService oauthService;
    private final CookieProperties cookieProperties;
    private final PaycoOAuthProperties paycoOAuthProperties;
    private final AppProperties appProperties;

    @GetMapping("/login/payco")
    public void paycoLogin(HttpServletResponse response) {
        String paycoLoginUri = UriComponentsBuilder
                .fromHttpUrl("https://id.payco.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", paycoOAuthProperties.getPaycoClientId())
                .queryParam("redirect_uri", paycoOAuthProperties.getPaycoRedirectUri())
                .queryParam("serviceProviderCode", "FRIENDS")
                .queryParam("userLocale", "ko_KR")
                .build()
                .toUriString();
        try {
            response.sendRedirect(paycoLoginUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/login/payco/callback")
    public void paycoLoginCallback(@RequestParam String code,
                                   HttpServletResponse response) throws IOException {
        TokenDto tokenDto = oauthService.oauthLogin(code);
        bakeTokenCookies(tokenDto, response);
        response.sendRedirect(appProperties.getFrontDomain());
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
