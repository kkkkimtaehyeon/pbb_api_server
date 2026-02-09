package com.nhnacademy.shop.common.security;

import com.nhnacademy.shop.common.enums.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class MemberDetail implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Long memberId;
    private final MemberRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    public MemberDetail(Long memberId, MemberRole role, String password, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.username = String.valueOf(memberId);
        this.password = password;
        this.authorities = authorities;
        this.role = role;
    }
}
