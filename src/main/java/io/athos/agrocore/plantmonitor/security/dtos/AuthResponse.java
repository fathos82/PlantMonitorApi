package io.athos.agrocore.plantmonitor.security.dtos;

import io.athos.agrocore.plantmonitor.users.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public record AuthResponse(Long id, String email,   String phone,LocalDateTime createdAt, LocalDateTime updatedAt) {
    public AuthResponse(User persistentUser) {
        this(persistentUser.getId(), persistentUser.getEmail(), persistentUser.getPhone(), persistentUser.getCreatedAt(), persistentUser.getUpdatedAt());
    }
}
