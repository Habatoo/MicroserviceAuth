package com.ssport.dto.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@ApiModel(value = "ChangeEmailDto")
public class ChangeEmailDto {

    @NotNull
    @ApiModelProperty(required = true)
    private UUID userId;

    @NotBlank
    @Email
    @ApiModelProperty(required = true)
    private String oldEmail;

    @NotBlank
    @Email
    @ApiModelProperty(required = true)
    private String newEmail;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
