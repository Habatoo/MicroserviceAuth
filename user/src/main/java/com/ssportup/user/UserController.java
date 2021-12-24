package com.ssportup.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
//@RequestMapping("api/v1/users")
@Data
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    /**
     * Метод создания объекта типа {@link User} при запросе методом
     * POST по адресу "api/v1/users"
     *
     * @param userRequest объект запроса с параметрами для формирования объекта
     */
    @PostMapping
    public void registerUser(@RequestBody UserRegistrationRequest userRequest) {
        log.info("new user registration {}", userRequest);
        userService.registerUser(userRequest);
    }

    /**
     * Метод отображения списка всех пользователей при запросе
     * методом GET по адресу "api/v1/users"
     *
     * @return список объектов {@link User}
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Метод поиска пользователя по его id при запросе методом GET по адресу "api/v1/users/{id}"
     *
     * @param id уникальный идентификатор пользовател
     * @return объект {@link Optional} содержащий пользователя {@link User} или пустой если пользователь не найден.
     */
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findByUserId(id);
    }

    /**
     * Метод для проверки корректности работы модуля {@link User}
     * при запросе методом GET по адресу "api/v1/users/info"
     *
     * @return возвращает объект "User " {@link String} и текущаю дату.
     */
    @GetMapping("info")
    public String info() {
        log.info("method .info invoked");
        return "User " + new Date();
    }
}
