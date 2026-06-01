package com.acme.data360agent.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 8000) String message,
        @Size(max = 32) String mode
) {
    public ChatRequest(String message) {
        this(message, "auto");
    }
}
