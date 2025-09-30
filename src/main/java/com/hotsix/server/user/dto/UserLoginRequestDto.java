package com.hotsix.server.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto (
        @NotBlank
        @Size(min = 3, max = 20)
        String email,
        @NotBlank
        @Size(min = 3, max = 20)
        String password
) {
}