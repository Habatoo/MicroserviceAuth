package com.ssport.dto.users;

import com.ssport.model.users.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

@ApiModel(value = "UserResponseDTO", description = "UserDTO for common users")
public class UserResponseDTO {

    @ApiModelProperty(notes = "User's id")
    private UUID userId;

    @ApiModelProperty(notes = "Full name(first name + last name)")
    private String fullName;

    @ApiModelProperty(notes = "Avatar link")
    private String avatarLink;

    @ApiModelProperty(notes = "User's email")
    private String email;

    public UserResponseDTO(User user) {
        this.userId = user.getId();
        this.fullName = user.getFullName();
        this.avatarLink = user.getAvatarLink();
        this.email = user.getEmail();
    }

    public UserResponseDTO() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
