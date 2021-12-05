package com.ssportup.user.request;

import lombok.*;

@Data
@NoArgsConstructor
public class UserRegistrationRequest {
    private String user_name;
    private String user_email;
    private String user_password;

}
