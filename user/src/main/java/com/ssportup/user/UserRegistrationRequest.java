package com.ssportup.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс конструированного запроса для работы с
 * контроллером {@link UserController} сущности {@link User}.
 *
 * @author habatoo
 * @version 0.001
 */
@Data
@NoArgsConstructor
public class UserRegistrationRequest {
    private String userName;
    private String userEmail;
    private String userPassword;

}
