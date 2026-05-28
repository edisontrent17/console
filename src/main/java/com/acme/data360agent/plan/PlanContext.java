package com.acme.data360agent.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record PlanContext(
        @NotBlank String org,
        @NotBlank String dataspace,
        @Pattern(regexp = "sandbox|production") String environment
) implements Serializable {
}
