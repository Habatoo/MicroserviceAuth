package com.ssportup.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис реализует логику работы методов контроллера {@link EventController}
 * с сущностью {@link Event}.
 *
 * @author habatoo
 * @version 0.001
 */
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Метод регистрирует новое событие по переданным данным
     *
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
