package com.example.eventmanagementsystem.service;

import com.example.eventmanagementsystem.mapper.EnrollmentMapper;
import com.example.eventmanagementsystem.mapper.EventMapper;
import com.example.eventmanagementsystem.mapper.UserMapper;
import com.example.eventmanagementsystem.model.MyPage;
import com.example.eventmanagementsystem.model.collections.Event;
import com.example.eventmanagementsystem.model.dto.EnrollmentDTO;
import com.example.eventmanagementsystem.model.dto.EventDTO;
import com.example.eventmanagementsystem.model.dto.UserDTO;
import com.example.eventmanagementsystem.model.entity.Enrollment;
import com.example.eventmanagementsystem.model.entity.User;
import com.example.eventmanagementsystem.repository.EnrollmentRepository;
import com.example.eventmanagementsystem.repository.EventRepository;
import com.example.eventmanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserMapper userMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public User registerUser(User user){
        return userRepository.save(user);
    }

    public UserDTO getAccountInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow();
        return userMapper.mapIntoDTO(user);
    }

    public MyPage<EnrollmentDTO> getUserEnrollments(Long id, int pageNumber, int pageSize) {
        Page<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByUser_Id(id, PageRequest.of(pageNumber, pageSize));
        Page<EnrollmentDTO> enrollmentDTOS = enrollments.map(enrollmentMapper::mapIntoDTO);
        return new MyPage<>(enrollmentDTOS);
    }

    public MyPage<EventDTO> getOrganizedEvents(Long id, int pageNumber, int pageSize) {
        Page<Event> events = eventRepository.findAllByOrganizerId(id, PageRequest.of(pageNumber, pageSize));
        return new MyPage<>(events.map(eventMapper::mapIntoDTO));
    }
}
