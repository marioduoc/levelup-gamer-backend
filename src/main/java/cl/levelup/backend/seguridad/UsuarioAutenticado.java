package cl.levelup.backend.seguridad;

import cl.levelup.backend.entidad.Rol;

// datos del usuario que vienen dentro del token
public record UsuarioAutenticado(Long id, String correo, Rol rol) {

    public boolean esAdminOVendedor() {
        return rol == Rol.ADMIN || rol == Rol.VENDEDOR;
    }
}
