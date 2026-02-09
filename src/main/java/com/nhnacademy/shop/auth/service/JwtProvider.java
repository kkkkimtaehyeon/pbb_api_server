package com.nhnacademy.shop.auth.service;

import com.nhnacademy.shop.auth.dto.TokenDto;
import com.nhnacademy.shop.auth.property.JwtProperties;
import com.nhnacademy.shop.common.enums.MemberRole;
import com.nhnacademy.shop.common.security.MemberDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nhnacademy.shop.auth.property.JwtProperties.ACCESS_TOKEN_EXPIRE_TIME;
import static com.nhnacademy.shop.auth.property.JwtProperties.REFRESH_TOKEN_EXPIRE_TIME;

@Service
public class JwtProvider {
    @Qualifier("authRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public JwtProvider(
            JwtProperties jwtProperties,
            @Qualifier("authRedisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
    }

    public TokenDto issueTokens(Long memberId, MemberRole role) {
        return new TokenDto(issueAccessToken(memberId, role), issueRefreshToken(memberId));
    }

    public String issueAccessToken(Long memberId, MemberRole role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME); // 만료시각

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim("role", role.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(jwtProperties.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String issueRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME); // 만료시각

        return Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(jwtProperties.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // 2. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtProperties.getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiration(String token) {
        // 1. JWT 토큰에서 만료 시간(Expiration) 추출
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSigningKey()) // 발급 시 사용했던 서명 키
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        // 2. 현재 시간 가져오기
        long now = new Date().getTime();

        // 3. (만료 시간 - 현재 시간) 계산하여 남은 밀리초(ms) 반환
        return (expiration.getTime() - now);
    }

    public void registerBlacklist(String token) {
        long expiration = getExpiration(token);
        redisTemplate.opsForValue().set(
                "blacklist:" + token,
                "logout",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBlacklisted(String token) {
        // Redis에 해당 토큰을 키로 가진 데이터가 있는지 확인
        String values = redisTemplate.opsForValue().get("blacklist:" + token);

        // 값이 존재한다면(null이 아니라면) 블랙리스트에 등록된 토큰임
        return StringUtils.hasText(values);
    }

    public Authentication getAuthentication(String token) {
        // 1. 토큰 복호화 (Payload 꺼내기)
        Claims claims = parseClaim(token);

        // 2. 권한 정보가 있는지 확인 (예: "auth"라는 클레임에 "ROLE_USER,ROLE_ADMIN" 저장됨)
        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 3. 클레임에서 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 4. UserDetails 객체를 생성 (User는 시큐리티에서 제공하는 기본 클래스)
        // claims.getSubject()는 보통 사용자의 ID나 이메일이 들어있음
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
        MemberDetail principal = new MemberDetail(
                Long.valueOf(claims.getSubject()), MemberRole.valueOf((String) claims.get("role")),
                "", authorities);

        // 5. 시큐리티용 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Long getMemberId(String token) {
        Claims claims = null;
        try {
            claims = parseClaim(token);
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        return Long.valueOf(claims.getSubject());
    }

    public MemberRole getRole(String token) {
        Claims claims = parseClaim(token);
        return MemberRole.valueOf((String) claims.get("role"));
    }

    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
