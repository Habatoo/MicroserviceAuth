package com.ssportup.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий с реализацией методов CRUD сущности {@link Event}.
 *
 * @author habatoo
 * @version 0.001
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
        Optional<Event> findByEventTitle(String eventTitle);
        Optional<Event> findByEventId(Long eventId);
        Optional<Event> findByEventDate(LocalDateTime eventDate);

        Boolean existsByEventTitle(String eventTitle);
        Boolean existsByEventId(Long eventId);
}
