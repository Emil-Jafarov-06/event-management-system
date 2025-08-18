package com.example.eventmanagementsystem.service;

import com.example.eventmanagementsystem.exception.ForbiddenAccessException;
import com.example.eventmanagementsystem.mapper.EnrollmentMapper;
import com.example.eventmanagementsystem.mapper.EventMapper;
import com.example.eventmanagementsystem.model.MyPage;
import com.example.eventmanagementsystem.model.collections.Comment;
import com.example.eventmanagementsystem.model.collections.Event;
import com.example.eventmanagementsystem.model.dto.EnrollmentDTO;
import com.example.eventmanagementsystem.model.dto.EventDTO;
import com.example.eventmanagementsystem.model.entity.Enrollment;
import com.example.eventmanagementsystem.model.entity.User;
import com.example.eventmanagementsystem.model.request.AddCommentRequest;
import com.example.eventmanagementsystem.model.request.EventCreateRequest;
import com.example.eventmanagementsystem.repository.EnrollmentRepository;
import com.example.eventmanagementsystem.repository.EventRepository;
import com.example.eventmanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final MongoTemplate mongoTemplate;

    @CachePut(value = "EVENT_CACHE", key = "#result.id")
    public EventDTO createEvent(Long id, EventCreateRequest eventCreateRequest) {
        Event event = new Event();
        event.setOrganizerId(id);
        event.setTitle(eventCreateRequest.getTitle());
        event.setDescription(eventCreateRequest.getDescription());
        event.setExtraInfo(eventCreateRequest.getExtraInfo());
        event.setDate(eventCreateRequest.getDate());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.mapIntoDTO(savedEvent);
    }

    @CachePut(value = "EVENT_CACHE", key = "#result.id")
    public EventDTO updateEvent(Long userId, String eventId, EventCreateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        if(!event.getOrganizerId().equals(userId)){
            throw new ForbiddenAccessException("You are not authorized to update this event!");
        }
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setExtraInfo(request.getExtraInfo());
        event.setDate(request.getDate());
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.mapIntoDTO(updatedEvent);
    }

    public EventDTO addComment(Long userId, String eventId, AddCommentRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        if(Objects.isNull(enrollmentRepository.findEnrollmentsByUser_IdAndEventId(userId, eventId))){
            throw new ForbiddenAccessException("Only enrolled users can add comments to an event!");
        }
        Comment comment = Comment.builder()
                .content(request.getContent())
                .date(LocalDateTime.now())
                .userId(userId).build();
        event.getComments().add(comment);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.mapIntoDTO(updatedEvent);
    }

    @CachePut(value = "ENROLLMENT_CACHE", key = "#result.id")
    public EnrollmentDTO enrollUserForCourse(Long userId, String eventId) {
        if(Objects.nonNull(enrollmentRepository.findEnrollmentsByUser_IdAndEventId(userId, eventId))){
            throw new ForbiddenAccessException("You are already enrolled in this event!");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        if(event.getOrganizerId().equals(userId)){
            throw new ForbiddenAccessException("You cannot enroll for your own event!");
        }
        if(event.getDate().isBefore(LocalDateTime.now())){
            throw new ForbiddenAccessException("You cannot enroll for an event that has already passed!");
        }
        User user = userRepository.findById(userId)
                .orElseThrow();

        Enrollment enrollment = Enrollment
                .builder()
                .user(user)
                .eventId(eventId)
                .date(LocalDateTime.now()).build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.mapIntoDTO(savedEnrollment);
    }

    @Cacheable(value = "EVENT_CACHE", key = "#eventId")
    public EventDTO getEventDetails(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow();
        return eventMapper.mapIntoDTO(event);
    }

    @Cacheable(value = "ALL_EVENTS_CACHE", key = "'AllEvents'")
    public MyPage<EventDTO> getAllEvents(int pageNumber, int pageSize) {
        Page<Event> allEvents = eventRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return new MyPage<>(allEvents.map(eventMapper::mapIntoDTO));
    }

    @Cacheable(value = "UPCOMING_EVENTS_CACHE", key = "'UpcomingEvents'")
    public MyPage<EventDTO> getUpcomingEvents(int pageNumber, int pageSize) {
        MatchOperation matchOperation = Aggregation
                .match(Criteria.where("date").gt(LocalDateTime.now()));
        SortOperation sortOperation = Aggregation
                .sort(Sort.Direction.ASC, "date");
        SkipOperation skipOperation = Aggregation
                .skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation
                .limit(pageSize);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation, sortOperation, skipOperation, limitOperation
        );

        List<EventDTO> events = mongoTemplate
                .aggregate(aggregation, Event.class, EventDTO.class)
                .getMappedResults();

        return MyPage.<EventDTO>builder()
                .content(events)
                .pageSize(pageSize)
                .currentPage(pageNumber)
                .build();
    }

    @Cacheable(value = "NameFilteredEvents", key = "#name")
    public MyPage<EventDTO> searchEventsByName(String name, int pageNumber, int pageSize) {
        MatchOperation matchOperation = Aggregation
                .match(Criteria.where("title").regex(name, "i"));
        SortOperation sortOperation = Aggregation
                .sort(Sort.Direction.ASC, "date");
        LimitOperation limitOperation = Aggregation
                .limit(pageSize);
        SkipOperation skipOperation = Aggregation
                .skip((long) pageNumber * pageSize);

        Aggregation aggregation = Aggregation.newAggregation(matchOperation,sortOperation, skipOperation, limitOperation);
        List<EventDTO> events = mongoTemplate.aggregate(aggregation, Event.class, EventDTO.class).getMappedResults();
        return MyPage.<EventDTO>builder()
                .content(events)
                .pageSize(pageSize)
                .currentPage(pageNumber)
                .build();
    }

    public List<Document> getEventCountsByOrganizers() {
        GroupOperation groupOperation = Aggregation
                .group("organizerId")
                .count().as("count");
        SortOperation sortOperation = Aggregation
                .sort(Sort.Direction.DESC, "count");
        Aggregation aggregation = Aggregation.newAggregation(groupOperation, sortOperation);
        return mongoTemplate.aggregate(aggregation, Event.class, Document.class).getMappedResults();
    }
}
