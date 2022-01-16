package com.ssportup.user.dto.user;

import com.ssportup.user.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {

    private UUID userId;

    private String fullName;

    private String email;

    public UserResponseDTO(User user) {
        this.userId = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
    }

}
