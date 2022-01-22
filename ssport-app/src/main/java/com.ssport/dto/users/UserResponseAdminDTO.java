package com.ssport.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssport.model.users.Role;
import com.ssport.model.users.SocialMediaService;
import com.ssport.model.users.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Set;

@ApiModel(value = "UserResponseAdminDTO", description = "UserDTO for admins")
public class UserResponseAdminDTO extends UserResponseDTO {

    @ApiModelProperty(notes = "Social sign-in provider", example = "GOOGLE")
    private SocialMediaService signInProvider;

    @ApiModelProperty(notes = "Is email verified")
    private boolean isEmailVerified;

    @ApiModelProperty(notes = "Date joined")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dateJoined;

    @ApiModelProperty(notes = "Set of user's roles")
    private Set<Role> roles;

    public UserResponseAdminDTO() {
    }

    public UserResponseAdminDTO(User user) {
        super(user);
        this.signInProvider = user.getSignInProvider();
        this.isEmailVerified = user.isEmailVerified();
        this.dateJoined = user.getDateJoined();
        this.roles = user.getRoles();
    }

    public SocialMediaService getSignInProvider() {
        return signInProvider;
    }

    public void setSignInProvider(SocialMediaService signInProvider) {
        this.signInProvider = signInProvider;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
