package com.example.eventmanagementsystem.mapper;

import com.example.eventmanagementsystem.model.dto.EnrollmentDTO;
import com.example.eventmanagementsystem.model.entity.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    public EnrollmentDTO mapIntoDTO(Enrollment enrollment);

}
