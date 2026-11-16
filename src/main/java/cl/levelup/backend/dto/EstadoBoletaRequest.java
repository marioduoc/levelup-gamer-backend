package cl.levelup.backend.dto;

import cl.levelup.backend.entidad.EstadoBoleta;
import jakarta.validation.constraints.NotNull;

public record EstadoBoletaRequest(
        @NotNull(message = "El estado es requerido.")
        EstadoBoleta estado) {
}
