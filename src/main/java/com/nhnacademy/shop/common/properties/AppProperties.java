package com.nhnacademy.shop.common.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppProperties {
    @Value("${app.front_domain}")
    private String frontDomain;
}
