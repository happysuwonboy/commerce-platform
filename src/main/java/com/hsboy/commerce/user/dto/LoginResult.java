package com.hsboy.commerce.user.dto;

import java.time.Duration;

public record LoginResult(String accessToken, String refreshToken, Duration refreshTokenTtl) { }
