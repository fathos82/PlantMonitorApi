package io.athos.agrocore.plantmonitor.security.dtos;

public record AuthTokenResponse(String accessToken, String refreshToken, String tokenType) {
}
