package cl.levelup.backend.dto;

import java.time.LocalDate;

import cl.levelup.backend.entidad.Rol;
import cl.levelup.backend.entidad.Usuario;

public record UsuarioResponse(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        Rol rol,
        LocalDate fechaNacimiento,
        String region,
        String comuna,
        String direccion,
        Boolean activo) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getRun(),
                u.getNombre(),
                u.getApellidos(),
                u.getCorreo(),
                u.getRol(),
                u.getFechaNacimiento(),
                u.getRegion(),
                u.getComuna(),
                u.getDireccion(),
                u.getActivo());
    }
}
