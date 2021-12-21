package com.ssportup.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Модель события. Записывается в БД в таблицу с имененм event.
 *
 * @param "eventId"          - primary key таблицы event.
 * @param "eventTitle"       - название события.
 * @param "eventDescription" - описание события.
 * @param "eventDate"        - дата события.
 * @param "eventPlace"       - адрес события.
 * @param "eventLatitude"    - широта гео координаты события.
 * @param "eventLongitude"   - долгота гео координаты события.
 * @author habatoo
 * @version 0.001
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class Event {
    @Id
    @SequenceGenerator(
            name = "event_id_sequence",
            sequenceName = "event_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_id_sequence"
    )
    Long eventId;
    String eventTitle;
    String eventDescription;
    LocalDateTime eventDate;
    String eventPlace;
    String eventLatitude;
    String eventLongitude;
}
