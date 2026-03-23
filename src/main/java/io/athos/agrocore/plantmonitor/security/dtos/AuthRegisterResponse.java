package io.athos.agrocore.plantmonitor.security.dtos;

import io.athos.agrocore.plantmonitor.users.User;

import java.time.LocalDateTime;

public record AuthRegisterResponse(
        Long userId,
        String username,
        String name,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public AuthRegisterResponse(User user) {
        this(user.getId(),user.getUsername(), user.getName(), user.getEmail(), user.getPhone(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
