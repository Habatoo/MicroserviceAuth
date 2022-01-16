package com.ssportup.user.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssportup.user.model.user.Role;
import com.ssportup.user.model.user.SocialMediaService;
import com.ssportup.user.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseAdminDTO extends UserResponseDTO {

    private SocialMediaService signInProvider;

    private boolean isEmailVerified;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dateJoined;

    private Set<Role> roles;

    public UserResponseAdminDTO(User user) {
        super(user);
        this.signInProvider = user.getSignInProvider();
        this.isEmailVerified = user.isEmailVerified();
        this.dateJoined = user.getDateJoined();
        this.roles = user.getRoles();
    }

}
