package com.example.eventmanagementsystem.controller;

import com.example.eventmanagementsystem.model.MyPage;
import com.example.eventmanagementsystem.model.dto.EnrollmentDTO;
import com.example.eventmanagementsystem.model.dto.EventDTO;
import com.example.eventmanagementsystem.model.request.AddCommentRequest;
import com.example.eventmanagementsystem.model.request.EventCreateRequest;
import com.example.eventmanagementsystem.model.response.InfoResponse;
import com.example.eventmanagementsystem.security.SecurityUser;
import com.example.eventmanagementsystem.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "Event Controller", description = "Event related operations")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events", description = "Gets all the events.")
    @GetMapping("/all")
    public ResponseEntity<InfoResponse<MyPage<EventDTO>>> getAllEvents(@RequestParam(defaultValue = "0") int pageNumber,
                                                                       @RequestParam(defaultValue = "10") int pageSize){
        return ResponseEntity.ok(new InfoResponse<>(true,
                "All events fetched successfully.",
                eventService.getAllEvents(pageNumber, pageSize)));
    }

    @Operation(summary = "Get event details", description = "Gets the details of an existing event.")
    @GetMapping("/{eventId}")
    public ResponseEntity<InfoResponse<EventDTO>> getEventDetails(@PathVariable @NotBlank String eventId){
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Event fetched successfully.",
                eventService.getEventDetails(eventId)));
    }

    @Operation(summary = "Get upcoming events", description = "Gets upcoming events in order.")
    @GetMapping("/upcoming")
    public ResponseEntity<InfoResponse<MyPage<EventDTO>>> getUpcomingEvents(@RequestParam(defaultValue = "0") int pageNumber,
                                                                            @RequestParam(defaultValue = "10") int pageSize){
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Upcoming events fetched successfully.",
                eventService.getUpcomingEvents(pageNumber, pageSize)));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<InfoResponse<MyPage<EventDTO>>> searchEvents(@RequestParam(defaultValue = "0") int pageNumber,
                                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                                       @RequestParam(defaultValue = "") @PathVariable String name){
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Events fetched successfully.",
                eventService.searchEventsByName(name, pageNumber, pageSize)));
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @Operation(summary = "Create event", description = "Creates a new event. Only available for organizers.")
    @PostMapping("")
    public ResponseEntity<InfoResponse<EventDTO>> createEvent(@RequestBody @Valid EventCreateRequest request){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Event created successfully.",
                eventService.createEvent(securityUser.getUser().getId(), request)));
    }

    @Operation(summary = "Add comment to an event", description = "Adds a comment to an existing event. Only available for enrolled users.")
    @PostMapping("/{eventId}/add-comment")
    public ResponseEntity<InfoResponse<EventDTO>> addComment(@PathVariable @NotBlank String eventId, @RequestBody @Valid AddCommentRequest request){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Event created successfully.",
                eventService.addComment(securityUser.getUser().getId(), eventId, request)));
    }

    @Operation(summary = "Enroll for an event", description = "Enrolls for an existing event.")
    @PostMapping("/{eventId}/enroll")
    public ResponseEntity<InfoResponse<EnrollmentDTO>> enrollForEvent(@PathVariable @NotBlank String eventId){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Event created successfully.",
                eventService.enrollUserForCourse(securityUser.getUser().getId(), eventId)));
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @Operation(summary = "Update event", description = "Updates an existing event. Only available for organizers.")
    @PutMapping("/{eventId}/update")
    public ResponseEntity<InfoResponse<EventDTO>> updateEvent(@PathVariable @NotBlank String eventId, @RequestBody @Valid EventCreateRequest request){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Event created successfully.",
                eventService.updateEvent(securityUser.getUser().getId(), eventId, request)));
    }

}
