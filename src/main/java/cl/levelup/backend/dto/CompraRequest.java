package cl.levelup.backend.dto;

import java.util.List;

import cl.levelup.backend.validacion.CorreoPermitido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// solo viaja el código y la cantidad; los precios los calcula el servidor
public record CompraRequest(
        @NotBlank(message = "El nombre es requerido.")
        @Size(max = 50, message = "Máximo 50 caracteres.")
        String nombre,

        @NotBlank(message = "Los apellidos son requeridos.")
        @Size(max = 100, message = "Máximo 100 caracteres.")
        String apellidos,

        @CorreoPermitido
        String correo,

        @NotBlank(message = "La calle es requerida.")
        @Size(max = 200, message = "Máximo 200 caracteres.")
        String calle,

        @Size(max = 50, message = "Máximo 50 caracteres.")
        String departamento,

        @NotBlank(message = "Selecciona una región.")
        String region,

        @NotBlank(message = "Selecciona una comuna.")
        String comuna,

        @Size(max = 300, message = "Máximo 300 caracteres.")
        String indicaciones,

        @NotBlank(message = "La tarjeta es requerida.")
        @Pattern(regexp = "^[0-9 ]{16,19}$", message = "Ingresa los 16 dígitos de la tarjeta.")
        String tarjeta,

        @NotEmpty(message = "El carrito está vacío.")
        List<@Valid ItemCompraRequest> items) {
}
