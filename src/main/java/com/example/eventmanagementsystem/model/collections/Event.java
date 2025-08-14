package com.example.eventmanagementsystem.model.collections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String id;
    private String name;
    private String title;
    private String description;
    private LocalDateTime date;
    private Long organizerId;
    private Map<String, Object> extraInfo = new HashMap<>();
    private List<Comment> comments = new ArrayList<>();

}