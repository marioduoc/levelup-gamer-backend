package cl.levelup.backend.dto;

import java.time.LocalDate;

import cl.levelup.backend.validacion.MayorDeEdad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// lo que el usuario puede cambiar de su propia cuenta (no correo, run ni rol)
public record PerfilRequest(
        @NotBlank(message = "El nombre es requerido.")
        @Size(max = 50, message = "Máximo 50 caracteres.")
        String nombre,

        @NotBlank(message = "Los apellidos son requeridos.")
        @Size(max = 100, message = "Máximo 100 caracteres.")
        String apellidos,

        @MayorDeEdad
        LocalDate fechaNacimiento,

        @Size(max = 100, message = "Máximo 100 caracteres.")
        String region,

        @Size(max = 100, message = "Máximo 100 caracteres.")
        String comuna,

        @Size(max = 300, message = "Máximo 300 caracteres.")
        String direccion,

        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres.")
        String password) {
}
