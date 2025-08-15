package com.example.eventmanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshRequest {
    @NotBlank
    private String refreshToken;
}
