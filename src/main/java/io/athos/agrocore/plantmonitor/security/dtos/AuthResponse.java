package io.athos.agrocore.plantmonitor.security.dtos;

import io.athos.agrocore.plantmonitor.users.User;

import java.time.LocalDateTime;

public record AuthResponse(
        Long userId,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public AuthResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
