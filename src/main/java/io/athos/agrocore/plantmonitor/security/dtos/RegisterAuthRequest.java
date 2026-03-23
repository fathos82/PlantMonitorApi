package io.athos.agrocore.plantmonitor.security.dtos;

public record RegisterAuthRequest(String username, String email, String name, String phone, String password) {
}
