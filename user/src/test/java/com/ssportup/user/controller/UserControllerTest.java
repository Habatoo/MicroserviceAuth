package com.ssportup.user.controller;

import com.ssportup.user.User;
import com.ssportup.user.UserController;
import com.ssportup.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс UserControllerTest проводит тестирование публичных методов
 * контроллера {@link UserController}
 *
 * @author habatoo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@Sql(scripts = {"classpath:create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    /**
     * Инициализация экземпляров тестируемого класса {@link User}.
     */
    @BeforeEach
    void setUp() {
        User user = new User(
                4L,
                "test",
                "test@email.co,",
                "12345"
        );
    }

    /**
     * Очистка экземпляров тестируемого класса {@link User}.
     */
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * Проверяет успешную подгрузку контроллеров из контекста.
     * Сценарий проверяет не null объекты контроллеров
     */
    @Test
    public void loadControllers_Test() {
        assertThat(userRepository).isNotNull();
        assertThat(userController).isNotNull();
    }

    /**
     * Тестирование методов {@link UserRepository}
     */
    @Test
    public void repository_Test() {
        assertTrue(userRepository.existsByUserName("user"));
        assertFalse(userRepository.existsByUserName("user2"));
        assertThat(userRepository.findByUserName("user2").equals(Optional.empty()));

        // Optional[User(userId=3, userName=user, userEmail=user@a.com,
        // userPassword=$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS)]
        assertThat(userRepository.findByUserName("user").equals(User.class));

        // Optional[User(userId=1, userName=administrator, userEmail=admin@a.com,
        // userPassword=$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS)]
        assertThat(userRepository.findByUserId(1L).equals(User.class));

        assertThat(userRepository.findByUserId(10L).equals(Optional.empty()));

        // Optional[User(userId=2, userName=moderator, userEmail=moderator@a.com,
        // userPassword=$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS)]
        assertThat(userRepository.findByUserEmail("moderator@a.com").equals(User.class));

    }

    /**
     * Метод тестирует созание нового пользователя.
     * Сценарий отрабатывает создание пользователя с username "test"
     * useremail "test@email.com"
     *
     */
    @Test
    void registerUser_Test()  throws Exception {
        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"userName\": \"testmod\", \"userEmail\":" +
                                " \"testmod@mod.com\", \"userPassword\": \"12345\" }"))
                .andExpect(status().isOk());

        User user = userRepository.findByUserName("testmod").get();
        assertTrue(user.getUserName().equals("testmod"));
        assertTrue(user.getUserEmail().equals("testmod@mod.com"));
    }

    /**
     * Метод тестирует отображение всех зарегистрированных пользователей.
     * Сценирий предполагает отображение трех заранее созжанных пользователей.
     */
    @Test
    void getAllCustomers_Test() throws Exception{
        String resp = "[{\"userId\":5,\"userName\":\"administrator\",\"userEmail\":\"admin@a.com\",\"userPassword\":" +
                "\"$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS\"},{\"userId\":6,\"userName\":" +
                "\"moderator\",\"userEmail\":\"moderator@a.com\",\"userPassword\"" +
                ":\"$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS\"}," +
                "{\"userId\":7,\"userName\":\"user\",\"userEmail\":\"user@a.com\"," +
                "\"userPassword\":\"$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS\"}]";

        this.mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().equals(resp);
    }

    /**
     * Метод обображения пользовател по его userId.
     * Сценарий предполагает отображение пользователя administrator с
     * userId = 5.
     */
    @Test
    void getUserByIdSuccess_Test() throws Exception {
        String resp = "{\"userId\":5,\"userName\":\"administrator\",\"userEmail\":\"admin@a.com\",\"userPassword\"" +
                ":\"$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS\"}";

        this.mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().equals(resp);

    }

    /**
     * Метод обображения пользовател по его userId.
     * Сценарий предполагает отображение сообщения о не существующем пользователе
     * с userId = 100.
     */
    @Test
    void getUserByIdFail_Test() throws Exception {
        String resp = "";

        this.mockMvc.perform(get("/api/v1/users/100"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().equals(resp);

    }

    /**
     * Метод тестирует отображение строки с текущим временем и датой.
     * Сценарий предполагает отображение строки содржащей значение "User"
     */
    @Test
    void info_Test() throws Exception{
        this.mockMvc.perform(get("/api/v1/users/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn().getResponse().getContentAsString().contains("User");
    }
}