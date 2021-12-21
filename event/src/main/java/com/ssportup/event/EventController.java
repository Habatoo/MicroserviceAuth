package com.ssportup.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("api/v1/events")
@Data
@NoArgsConstructor
public class EventController {
    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    @PostMapping
    public void registerUser(@RequestBody EventRegistrationRequest eventRequest) {
        log.info("new event registration {}", eventRequest);
        eventService.registerEvent(eventRequest);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findByEventId(id);
    }

    @GetMapping("info")
    public String info() {
        log.info("method .info invoked");
        return "Event " + new Date();

    }
}
