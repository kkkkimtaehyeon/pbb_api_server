package com.nhnacademy.shop.auth.service;

import com.nhnacademy.shop.auth.dto.PaycoMemberResponse;
import com.nhnacademy.shop.auth.dto.PaycoTokenResponse;
import com.nhnacademy.shop.auth.dto.TokenDto;
import com.nhnacademy.shop.auth.property.PaycoOAuthProperties;
import com.nhnacademy.shop.common.enums.MemberGrade;
import com.nhnacademy.shop.common.enums.MemberRole;
import com.nhnacademy.shop.member.v2.entity.Member;
import com.nhnacademy.shop.member.v2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class OauthService {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final PaycoOAuthProperties paycoOAuthProperties;


    @Transactional
    public TokenDto oauthLogin(String code) {
        PaycoTokenResponse token = issueAccessTokenFromPayco(code);
        PaycoMemberResponse oauthMember = fetchUserInfoFromPayco(token.getAccess_token());

        Member member = memberRepository.findByEmail(oauthMember.getEmail())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .name(oauthMember.getName())
                        .email(oauthMember.getEmail())
                        .password(oauthMember.getMemberId())
                        .role(MemberRole.ROLE_MEMBER)
                        .memberGrade(MemberGrade.BRONZE)
                        .build())
                );
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


    private PaycoTokenResponse issueAccessTokenFromPayco(String code) {
        URI paycoTokenUri = UriComponentsBuilder
                .fromHttpUrl("https://id.payco.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", paycoOAuthProperties.getPaycoClientId())
                .queryParam("client_secret", paycoOAuthProperties.getPaycoClientSecret())
                .queryParam("code", code)
                .build()
                .toUri();
        return restTemplate.getForObject(paycoTokenUri, PaycoTokenResponse.class);
    }

    private PaycoMemberResponse fetchUserInfoFromPayco(String accessToken) {
        String paycoUserInfoFetchUri = "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", paycoOAuthProperties.getPaycoClientId());
        headers.set("access_token", accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.postForObject(paycoUserInfoFetchUri, entity, PaycoMemberResponse.class);
    }
}
