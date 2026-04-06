package io.athos.agrocore.plantmonitor.security.dtos;

import jakarta.validation.constraints.Pattern;
public record RegisterAuthRequest(String username, String email, String name,
                                  @Pattern(
                                          regexp = "^\\([0-9]{2}\\) [0-9]{5}-[0-9]{4}$",
                                          message = "numero de telefone invalido (phone), ele deve estar no formato (11) 11111-1111"
                                  )
                                  String phone,
                                  String password) {
}
