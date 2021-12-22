package com.ssportup.user;

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
public class UserService {

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
