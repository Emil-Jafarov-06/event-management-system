package com.example.eventmanagementsystem.controller;

import com.example.eventmanagementsystem.model.MyPage;
import com.example.eventmanagementsystem.model.dto.EnrollmentDTO;
import com.example.eventmanagementsystem.model.dto.EventDTO;
import com.example.eventmanagementsystem.model.dto.UserDTO;
import com.example.eventmanagementsystem.model.response.InfoResponse;
import com.example.eventmanagementsystem.security.SecurityUser;
import com.example.eventmanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Controller", description = "User related operations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user details", description = "Gets the details of a user.")
    @GetMapping("/me")
    public ResponseEntity<InfoResponse<UserDTO>> getMyAccount(){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "User information retrieved successfully.",
                userService.getAccountInfo(securityUser.getUser().getId())));
    }

    @Operation(summary = "Get user enrollments", description = "Gets the enrollments of a user.")
    @GetMapping("/enrollments")
    public ResponseEntity<InfoResponse<MyPage<EnrollmentDTO>>> getMyEnrollments(@RequestParam(defaultValue = "0") int pageNumber,
                                                                                @RequestParam(defaultValue = "10") int pageSize){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "User enrollments retrieved successfully.",
                userService.getUserEnrollments(securityUser.getUser().getId(), pageNumber, pageSize)));
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @Operation(summary = "Get events created by the user", description = "Gets the events created by the user.")
    @GetMapping("/events-created")
    public ResponseEntity<InfoResponse<MyPage<EventDTO>>> getMyEvents(@RequestParam(defaultValue = "0") int pageNumber,
                                                                      @RequestParam(defaultValue = "10") int pageSize){
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new InfoResponse<>(true,
                "Organized events retrieved successfully.",
                userService.getOrganizedEvents(securityUser.getUser().getId(), pageNumber, pageSize)));
    }


}
