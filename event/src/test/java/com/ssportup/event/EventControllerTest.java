package com.ssportup.event;

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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс EventControllerTest проводит тестирование публичных методов
 * контроллера {@link EventController}
 *
 * @author habatoo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@Sql(scripts = {"classpath:create-event-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:create-event-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventController eventController;

    /**
     * Инициализация экземпляров тестируемого класса {@link Event}.
     */
    @BeforeEach
    void setUp() {
        Event event = new Event(
                1L,
                "fighting",
                "Fighting activity.",
                LocalDateTime.now(),
                "Central park  222",
                "11.9874",
                "23.3654"
        );
    }

    /**
     * Очистка экземпляров тестируемого класса {@link Event}.
     */
    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    /**
     * Проверяет успешную подгрузку контроллеров из контекста.
     * Сценарий проверяет не null объекты контроллеров
     */
    @Test
    public void loadControllers_Test() {
        assertThat(eventRepository).isNotNull();
        assertThat(eventController).isNotNull();
    }

    @Test
    void createEvent_Test() {
    }

    @Test
    void getAllEvents_Test() {
    }

    @Test
    void getEventById_Test() {
    }

    /**
     * Метод тестирует отображение строки с текущим временем и датой.
     * Сценарий предполагает отображение строки содржащей значение "Event"
     */
    @Test
    void info_Test() throws Exception {
        this.mockMvc.perform(get("/api/v1/events/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse().getContentAsString().contains("Event");
    }

    @Test
    void getEventService_Test() {
    }

    /**
     * Тестирование методов {@link EventRepository}
     */
    @Test
    void getEventRepository_Test() {
        assertTrue(eventRepository.existsByEventTitle("jogging"));
        assertFalse(eventRepository.existsByEventTitle("jogging2"));
        assertFalse(eventRepository.existsByEventId(1L));
        assertFalse(eventRepository.existsByEventId(10L));

        assertThat(eventRepository.findByEventTitle("jogging2").equals(Optional.empty()));
        assertThat(eventRepository.findByEventTitle("jogging").equals(Event.class));

        assertThat(eventRepository.findByEventId(1L).equals(Event.class));
        assertThat(eventRepository.findByEventId(10L).equals(Optional.empty()));
    }

    @Test
    void setEventService_Test() {
    }

    @Test
    void setEventRepository_Test() {
    }
}