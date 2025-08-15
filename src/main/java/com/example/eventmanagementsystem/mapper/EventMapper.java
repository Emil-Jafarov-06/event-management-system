package com.example.eventmanagementsystem.mapper;

import com.example.eventmanagementsystem.model.collections.Comment;
import com.example.eventmanagementsystem.model.collections.Event;
import com.example.eventmanagementsystem.model.dto.EventDTO;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface EventMapper {

    public EventDTO mapIntoDTO(Event event);

}
