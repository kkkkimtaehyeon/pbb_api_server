package com.nhnacademy.shop.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.shop.auth.service.JwtProvider;
import com.nhnacademy.shop.common.properties.AppProperties;
import com.nhnacademy.shop.common.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/reissue") || path.startsWith("/auth/logout");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // preflight 넘기기
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessTokenFromCookie(request);

        if (accessToken != null) {
            if (jwtProvider.validateToken(accessToken) && !jwtProvider.isBlacklisted(accessToken)) {

                Authentication auth = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                sendErrorResponse(response, 401, "Invalid Access Token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // CORS 헤더 추가 (생략)
        response.setHeader("Access-Control-Allow-Origin", appProperties.getFrontDomain()); // 프론트엔드 주소
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");

        ApiResponse<Object> errorResponse = ApiResponse.fail(null, String.valueOf(status), message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
