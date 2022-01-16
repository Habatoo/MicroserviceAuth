package com.ssportup.user.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssportup.user.dto.JsonResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Set;

@Setter
@Getter
public class UserDataResponseDTO implements JsonResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponseDTO user;

    @JsonInclude()
    private OAuth2AccessToken token;

    @JsonInclude()
    private boolean isEmailVerified = true;

    @JsonInclude()
    private boolean isSocialInput = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> roles;

}
