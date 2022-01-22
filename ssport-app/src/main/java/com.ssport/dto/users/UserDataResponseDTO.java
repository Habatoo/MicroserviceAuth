package com.ssport.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssport.dto.JsonResponse;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Set;

public class UserDataResponseDTO implements JsonResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Include if not null")
    private UserResponseDTO user;

    @JsonInclude()
    private OAuth2AccessToken token;

    @JsonInclude()
    private boolean isEmailVerified = true;

    @JsonInclude()
    private boolean isSocialInput = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Include if not null", allowableValues = "ROLE_REGISTERED_USER, ROLE_SOCIAL_USER, " +
            "ROLE_GUEST_USER, ROLE_RESTAURANT_USER, ROLE_ADMINISTRATOR")
    private Set<String> roles;

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public OAuth2AccessToken getToken() {
        return token;
    }

    public void setToken(OAuth2AccessToken token) {
        this.token = token;
    }

    public boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isSocialInput() {
        return isSocialInput;
    }

    public void setSocialInput(boolean socialInput) {
        isSocialInput = socialInput;
    }
}
