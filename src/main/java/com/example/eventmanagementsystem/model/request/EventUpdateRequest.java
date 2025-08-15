package com.example.eventmanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {

    private String title;
    private String description;
    private LocalDateTime date;
    private Map<String, Object> extraInfo = new HashMap<>();

}
