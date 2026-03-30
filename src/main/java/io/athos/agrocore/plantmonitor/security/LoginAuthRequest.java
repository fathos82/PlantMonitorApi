package io.athos.agrocore.plantmonitor.security;

import jakarta.validation.constraints.NotNull;

public record LoginAuthRequest(


        @NotNull
        String email,
        @NotNull
        String password) {
}
