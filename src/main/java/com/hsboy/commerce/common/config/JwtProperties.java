package com.hsboy.commerce.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secret;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtProperties(String secret, long accessTokenExpiry, long refreshTokenExpiry) {
        this.secret = secret;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

}
