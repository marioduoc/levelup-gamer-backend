package cl.levelup.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCompraRequest(
        @NotBlank(message = "El código del producto es requerido.")
        String codigo,

        @NotNull(message = "La cantidad es requerida.")
        @Min(value = 1, message = "La cantidad mínima es 1.")
        @Max(value = 99, message = "La cantidad máxima es 99.")
        Integer cantidad) {
}
