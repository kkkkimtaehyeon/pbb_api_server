package com.nhnacademy.shop.auth.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PaycoOAuthProperties {

    @Value("${oauth.payco.client_id}")
    private String paycoClientId;
    @Value("${oauth.payco.client_secret}")
    private String paycoClientSecret;
    @Value("${oauth.payco.redirect_uri}")
    private String paycoRedirectUri;
}
