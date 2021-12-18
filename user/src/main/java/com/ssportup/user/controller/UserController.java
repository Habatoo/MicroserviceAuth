package com.ssportup.user.controller;

import com.ssportup.user.model.User;
import com.ssportup.user.request.UserRegistrationRequest;
import com.ssportup.user.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Класс конфигурации контроллера.
 *
 * @author habatoo
 */
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

    @GetMapping
    public List<User> getAllCustomers() {
        // TODO List of users
        return null;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // TODO user by id
        return null;
    }

    @GetMapping("info")
    public String info() {
        log.info("method .info invoked");
        return "Controller " + new Date();

    }
}
