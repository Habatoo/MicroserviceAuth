package com.ssportup.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Класс конфигурации контроллера с основными методами работы с
 * сущностью {@link User}.
 *
 * @author habatoo
 * @version 0.001
 */
@Slf4j
@RestController
@RequestMapping("api/v1/users")
@Data
@NoArgsConstructor
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public void registerUser(@RequestBody UserRegistrationRequest userRequest) {
        log.info("new user registration {}", userRequest);
        userService.registerUser(userRequest);
    }

    @GetMapping
    public List<User> getAllCustomers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findByUserId(id);
    }

    @GetMapping("info")
    public String info() {
        log.info("method .info invoked");
        return "Controller " + new Date();

    }
}
