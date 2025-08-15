package com.example.eventmanagementsystem.repository;

import com.example.eventmanagementsystem.model.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Page<Enrollment> findEnrollmentsByUser_Id(Long userId, Pageable pageable);

    Enrollment findEnrollmentsByUser_IdAndEventId(Long userId, String eventId);
}
