package com.ssportup.user.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class TokenResponse {

    private final String idToken;
    private final String accessToken;
    private final String tokenType;
    private final Long expiresIn;
    private final String refreshToken;

}