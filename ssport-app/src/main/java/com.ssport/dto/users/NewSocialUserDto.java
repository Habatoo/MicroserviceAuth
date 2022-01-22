package com.ssport.dto.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "NewSocialUserDto")
public class NewSocialUserDto {

    @NotBlank
    @ApiModelProperty(required = true, notes = "id_token in case provider is google")
    private String token;

    @NotBlank
    @ApiModelProperty(required = true, example = "facebook", allowableValues = "facebook, google, apple")
    private String provider;

    @ApiModelProperty(required = true, example = "android", allowableValues = "android, ios")
    private String platform;

    @ApiModelProperty(notes = "required for sign in with apple")
    private String email;

    @ApiModelProperty(notes = "required for sign in with apple")
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
