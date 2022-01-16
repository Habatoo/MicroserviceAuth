package com.ssportup.user.service.impl;

import com.ssportup.user.controller.UserController;
import com.ssportup.user.dto.UserRegistrationRequest;
import com.ssportup.user.model.User;
import com.ssportup.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис реализует логику работы методов контроллера {@link UserController}
 * с сущностью {@link User}.
 *
 * @author habatoo
 * @version 0.001
 */
@Service
@AllArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;

    /**
     * Метод регистрирует нового пользователя по переданным данным
     *
     * @param userRequest объект запроса с полями сущности {@link User}.
     */
    public void registerUser(UserRegistrationRequest userRequest) {
        User user = User.builder()
                .userName(userRequest.getUserName())
                .userEmail(userRequest.getUserEmail())
                .userPassword(userRequest.getUserPassword())
                .build();
        userRepository.save(user);
    }

}
