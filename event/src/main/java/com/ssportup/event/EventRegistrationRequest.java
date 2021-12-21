package com.ssportup.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Класс конструированного запроса для работы с
 * контроллером {@link EventController} сущности {@link Event}.
 *
 * @author habatoo
 * @version 0.001
 */
@Data
@NoArgsConstructor
public class EventRegistrationRequest {
    String eventTitle;
    String eventDescription;
    LocalDateTime eventDate;
    String eventPlace;
    String eventLatitude;
    String eventLongitude;
}
