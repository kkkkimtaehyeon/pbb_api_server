package com.nhnacademy.shop.auth.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cookie")
@Getter
@Setter
public class CookieProperties {
    private boolean httpOnly = true;
    private String sameSite = "Lax"; // Default value
    private boolean secure = true;
    private String path = "/";
    private int maxAge = 3600; // Default or use separate token expiration
}
