package cl.levelup.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo es requerido.")
        String correo,

        @NotBlank(message = "La contraseña es requerida.")
        String password) {
}
