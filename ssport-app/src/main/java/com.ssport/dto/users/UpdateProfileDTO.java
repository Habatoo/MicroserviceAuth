package com.ssport.dto.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;

@ApiModel(value = "UpdateProfileDTO")
public class UpdateProfileDTO {

    @Email
    @ApiModelProperty(notes = "email (REQUIRED = TRUE when used for password recovery)")
    private String email;

    private String fullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
