package com.example.eventmanagementsystem.mapper;

import com.example.eventmanagementsystem.model.dto.UserDTO;
import com.example.eventmanagementsystem.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public UserDTO mapIntoDTO(User user);
}
