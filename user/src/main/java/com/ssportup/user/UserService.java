package com.ssportup.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired UserRepository userRepository;

    public void registerUser(UserRegistrationRequest userRequest) {
        User user = User.builder()
                .user_name(userRequest.getUser_name())
                .user_email(userRequest.getUser_email())
                .user_password(userRequest.getUser_password())
                .build();
        userRepository.save(user);
    }
}
