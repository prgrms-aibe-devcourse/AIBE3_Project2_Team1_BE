package com.hotsix.server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto (
        @NotBlank
        @Email
        @Size(min = 3, max = 100)
        String email,
        @NotBlank
        @Size(min = 3, max = 20)
        String password
) {
}