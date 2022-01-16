package com.ssportup.user.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewSocialUserDTO {

    private String token;

    private String provider;

    private String platform;

    private String email;

    private String fullName;

}
