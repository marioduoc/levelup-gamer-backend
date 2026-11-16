package cl.levelup.backend.dto;

import java.time.LocalDate;

import cl.levelup.backend.validacion.CorreoPermitido;
import cl.levelup.backend.validacion.MayorDeEdad;
import cl.levelup.backend.validacion.RunValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @RunValido
        String run,

        @NotBlank(message = "El nombre es requerido.")
        @Size(max = 50, message = "Máximo 50 caracteres.")
        String nombre,

        @NotBlank(message = "Los apellidos son requeridos.")
        @Size(max = 100, message = "Máximo 100 caracteres.")
        String apellidos,

        @CorreoPermitido
        String correo,

        @NotBlank(message = "La contraseña es requerida.")
        @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres.")
        String password,

        @NotNull(message = "La fecha de nacimiento es requerida.")
        @MayorDeEdad
        LocalDate fechaNacimiento,

        @NotBlank(message = "Selecciona una región.")
        String region,

        @NotBlank(message = "Selecciona una comuna.")
        String comuna,

        @NotBlank(message = "La dirección es requerida.")
        @Size(max = 300, message = "Máximo 300 caracteres.")
        String direccion) {
}
