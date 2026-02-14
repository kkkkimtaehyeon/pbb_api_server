package com.nhnacademy.shop.auth.dto;

import lombok.Data;

@Data
public class PaycoTokenResponse {
    private String access_token;
    private String access_token_secret;
    private String refresh_token;
    private String token_type;
    private String expires_in;
    private String state;
}
