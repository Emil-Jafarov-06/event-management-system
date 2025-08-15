package com.example.eventmanagementsystem.repository;

import com.example.eventmanagementsystem.model.collections.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface EventRepository extends MongoRepository<Event, String> {
    Page<Event> findAllByOrganizerId(Long organizerId, Pageable pageable);

    Page<Event> findEventsByTitleLikeIgnoreCaseAndDateAfterOrderByDate(String title, LocalDateTime dateAfter,
                                                                       Pageable pageable);
}
