package cl.levelup.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank(message = "El código es requerido.")
        @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "El código debe tener 2 letras mayúsculas y 3 números, ej: JM001.")
        String codigo,

        @NotBlank(message = "El nombre es requerido.")
        @Size(max = 100, message = "Máximo 100 caracteres.")
        String nombre,

        @Size(max = 500, message = "Máximo 500 caracteres.")
        String descripcion,

        @NotNull(message = "El precio es requerido.")
        @Min(value = 0, message = "El precio no puede ser negativo.")
        Integer precio,

        @Min(value = 0, message = "La oferta va de 0 a 100.")
        @Max(value = 100, message = "La oferta va de 0 a 100.")
        Integer oferta,

        @NotNull(message = "El stock es requerido.")
        @Min(value = 0, message = "El stock no puede ser negativo.")
        Integer stock,

        @NotNull(message = "El stock crítico es requerido.")
        @Min(value = 0, message = "El stock crítico no puede ser negativo.")
        Integer stockCritico,

        @Size(max = 255, message = "Máximo 255 caracteres.")
        String imagen,

        @NotNull(message = "La categoría es requerida.")
        Integer categoriaId) {
}
