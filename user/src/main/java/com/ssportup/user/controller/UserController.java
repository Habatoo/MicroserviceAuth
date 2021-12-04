package com.ssportup.user.controller;

import com.ssportup.user.UserRegistrationRequest;
import com.ssportup.user.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
@Data
@NoArgsConstructor
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public void registerUser(@RequestBody UserRegistrationRequest userRequest) {
        log.info("new user registration {}", userRequest);
        userService.registerUser(userRequest);
    }
}
