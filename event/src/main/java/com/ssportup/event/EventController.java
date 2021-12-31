package com.ssportup.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Класс конфигурации контроллера с основными методами работы с
 * сущностью {@link Event}.
 *
 * @author habatoo
 * @version 0.001
 */
@Slf4j
@RestController
@Data
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventRepository eventRepository;

    /**
     * Метод создания объекта типа {@link Event} при запросе методом
     * POST по адресу "/"
     *
     * @param eventRequest объект запроса с параметрами для формирования объекта
     *                     типа {@link Event}
     */
    @PostMapping
    public void createEvent(@RequestBody EventRegistrationRequest eventRequest) {
        log.info("new event registration {}", eventRequest);
        eventService.registerEvent(eventRequest);
    }

    /**
     * Метод отображения списка всех событий при запросе
     * методом GET по адресу "/"
     *
     * @return список объектов {@link Event}
     */
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Метод поиска пользователя по его id при запросе методом GET по адресу "api/v1/events/{id}"
     *
     * @param id уникальный идентификатор события
     * @return объект {@link Optional} содержащий событие {@link Event} или пустой если событие не найдено.
     */
    @GetMapping("/{id}")
    public Optional<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findByEventId(id);
    }

    /**
     * Метод для проверки корректности работы модуля {@link Event}
     * при запросе методом GET по адресу "/info"
     *
     * @return возвращает объект {@link String} "Event " и текущаю дату.
     */
    @GetMapping("info")
    public String info() {
        log.info("method .info invoked");
        return "Event " + new Date();

    }
}
