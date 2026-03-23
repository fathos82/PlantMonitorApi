package io.athos.agrocore.plantmonitor.security.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AuthUpdateRequest(

        @Size(min = 3, message = "O nome (name) deve ter pelo menos 3 caracteres.")
        String name,

        // todo: review validation
        @Pattern(
                regexp = "^\\([0-9]{2}\\) [0-9]{5}-[0-9]{4}$",
                message = "numero de telefone invalido (phone), ele deve estar no formato (11) 11111-1111"
        )        String phone

)  {}
