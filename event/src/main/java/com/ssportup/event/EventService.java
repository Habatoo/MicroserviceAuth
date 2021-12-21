package com.ssportup.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис реализует логику работы методов контроллера {@link EventController}
 * с сущностью {@link Event}.
 *
 * @author habatoo
 * @version 0.001
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Метод регистрирует новое событие по переданным данным
     * @param eventRequest объект запроса с полями сущности {@link Event}.
     */
    public void registerEvent(EventRegistrationRequest eventRequest) {
        Event event = Event.builder()
                .eventTitle(eventRequest.getEventTitle())
                .eventDescription(eventRequest.getEventDescription())
                .eventDate(eventRequest.getEventDate())
                .eventPlace(eventRequest.getEventPlace())
                .eventLatitude(eventRequest.getEventLatitude())
                .eventLongitude(eventRequest.getEventLongitude())
                .build();
        eventRepository.save(event);
    }

}
