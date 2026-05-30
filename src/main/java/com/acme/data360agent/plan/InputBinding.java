package com.acme.data360agent.plan;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record InputBinding(
        @NotBlank String fromStep,
        @NotBlank String path
) implements Serializable {
}
