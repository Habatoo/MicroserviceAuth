package com.ssport.dto.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;

@ApiModel(value = "PasswordRecoveryDTO")
public class PasswordRecoveryDTO {
    @Email
    @ApiModelProperty(required = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
