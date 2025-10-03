package com.hotsix.server.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequestDto(
        @NotBlank String username,
        @NotBlank String password
) {}
