package com.ssport.dto.users;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "UserFilterDTO", description = "Filter options for users")
public class UserFilterDTO {

    @ApiModelProperty(notes = "User's name")
    private String name;

    @ApiModelProperty(notes = "User's email")
    private String email;

    @ApiModelProperty(notes = "User's role", allowableValues = "ROLE_ADMINISTRATOR, ROLE_RESTAURANT_USER, ROLE_COMMON_USER")
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
