package com.example.eventmanagementsystem.model.dto;

import com.example.eventmanagementsystem.model.collections.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {

    private String id;
    private String title;
    private String description;
    private LocalDateTime date;
    private Long organizerId;
    private Map<String, Object> extraInfo = new HashMap<>();
    private List<Comment> comments = new ArrayList<>();

}
