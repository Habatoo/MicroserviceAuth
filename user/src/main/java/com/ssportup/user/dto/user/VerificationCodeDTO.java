package com.ssportup.user.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeDTO {

    private String email;

    private String code;

}
