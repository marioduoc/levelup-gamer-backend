package cl.levelup.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es requerido.")
        @Size(max = 50, message = "Máximo 50 caracteres.")
        String nombre) {
}
