package com.ssportup.user.service;

import com.ssportup.user.request.UserRegistrationRequest;
import com.ssportup.user.model.User;
import com.ssportup.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(UserRegistrationRequest userRequest) {
        User user = User.builder()
                .user_name(userRequest.getUser_name())
                .user_email(userRequest.getUser_email())
                .user_password(userRequest.getUser_password())
                .build();
        userRepository.save(user);
    }
}
