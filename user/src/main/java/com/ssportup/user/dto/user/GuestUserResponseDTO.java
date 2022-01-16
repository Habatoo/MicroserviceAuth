package com.ssportup.user.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssportup.user.dto.JsonResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class GuestUserResponseDTO implements JsonResponse {

    @JsonInclude
    private OAuth2AccessToken token = null;

    @JsonInclude
    private boolean isActive = false;

    public OAuth2AccessToken getToken() {
        return token;
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = token;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
