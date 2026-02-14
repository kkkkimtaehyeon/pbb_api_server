package com.nhnacademy.shop.auth.service;

import com.nhnacademy.shop.auth.dto.LoginRequest;
import com.nhnacademy.shop.auth.dto.TokenDto;
import com.nhnacademy.shop.common.enums.MemberRole;
import com.nhnacademy.shop.common.enums.OauthProvider;
import com.nhnacademy.shop.common.exceptions.LoginFailedException;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(
            @Qualifier("authRedisTemplate") RedisTemplate<String, String> redisTemplate,
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider) {
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public TokenDto login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new LoginFailedException("이메일/비밀번호가 일치하지 않습니다."));
        // 비밀번호 비교
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new LoginFailedException("이메일/비밀번호가 일치하지 않습니다.");
        }
        // jwt 토큰 발급
        TokenDto tokens = jwtProvider.issueTokens(member.getId(), member.getRole());
        // redis에 rt 저장
        redisTemplate.opsForValue().set(
                "rt:" + member.getId(),
                tokens.getRefreshToken(),
                7, TimeUnit.DAYS
        );
        return tokens;
    }

    public void logout(String accessToken) {
        Long memberId = jwtProvider.getMemberId(accessToken);
        // redis에서 rt 삭제
        redisTemplate.delete("rt:" + memberId);
        // at 만료기간 남아있을 경우를 고려해 블랙리스트 처리
        jwtProvider.registerBlacklist(accessToken);
    }



    @Transactional
    public TokenDto reissue(String refreshToken) {
        // 1. RT 유효성 검사 (만료 여부 등)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. RT에서 사용자 정보 추출
        Long memberId = jwtProvider.getMemberId(refreshToken);

        // 3. Redis에 저장된 RT와 일치하는지 확인
        String savedRefreshToken = redisTemplate.opsForValue().get("rt:" + memberId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token 정보가 일치하지 않습니다.");
        }

        // 5. [핵심] DB에서 최신 회원 정보 조회 (권한 변경 등 반영)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 6. 새로운 토큰 세트 발급 (RTR 적용)
        TokenDto newTokenDto = jwtProvider.issueTokens(memberId, member.getRole());

        // 7. Redis 정보 업데이트 (기존 RT 삭제 후 새 RT 저장)
        redisTemplate.opsForValue().set(
                "rt:" + memberId,
                newTokenDto.getRefreshToken(),
                7, TimeUnit.DAYS
        );

        return newTokenDto;
    }

    public void oauthLogin(OauthProvider provider, String code) {

    }
}
